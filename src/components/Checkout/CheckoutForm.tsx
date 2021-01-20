import React, {useEffect, useState} from "react";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import dynamic from "next/dynamic";
import DeliveryDetailsStep from "./DeliveryDetailsStep";
import {Box, Fade, LinearProgress, Paper, Slide, Snackbar, Typography,} from "@material-ui/core";
import {Form} from "react-final-form";
import {mixed, number, object, ObjectSchema, string} from "yup";
import {makeValidateSync} from "mui-rff";
import {DeliveryOption, DeliveryProvider, Order, PaymentOption,} from "../../order-model";
import FormStepper from "./FormStepper";
import Link from "next/link";
import {CartProduct} from "../../pages/checkout";
import {clearCartAction, StoreState} from "../../store";
import {connect} from "react-redux";
import promiseRetry from "promise-retry";
import {Alert} from "@material-ui/lab";
import {Offline} from "react-detect-offline";
import theme from "../../theme";

const SummaryStep = dynamic(() => import("./SummaryStep"));
const PaymentStep = dynamic(() => import("./Payment"));
const ContactUs = dynamic(() => import("./ContactUs"));

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: "100%",
    },
    button: {
      marginRight: theme.spacing(1),
    },
    instructions: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
  })
);

function getButtonTexts() {
  return [
    "Перейти к проверке заказа",
    "Перейти к оплате заказа",
    "Отправить заказ",
  ];
}

export type OrderForm = Partial<{
  city: string;
  warehouseNumber: number;
  deliveryProvider: DeliveryProvider;
  name: string;
  deliveryOption: DeliveryOption;
  phone: string;
  paymentOption: PaymentOption;
}>;

function getStepContent(step: number, orderData: OrderForm) {
  switch (step) {
    case 0:
      return <DeliveryDetailsStep orderForm={orderData} />;
    case 1:
      return <SummaryStep orderForm={orderData} />;
    case 2:
      return <PaymentStep />;
    default:
      return "Unknown step";
  }
}

const schema: ObjectSchema<OrderForm> = object({
  paymentOption: mixed().oneOf([PaymentOption.COD]).default(PaymentOption.COD),
  city: string()
    .required("Введите город, пожалуйста")
    .max(50, "Слишком длинный город"),
  warehouseNumber: number()
      .required("Введите номер отделения, пожалуйста")
      .positive("Номер отделения должен быть больше нуля."),
  deliveryProvider: mixed()
    .oneOf([DeliveryProvider.NOVAYA_POCHTA])
    .required()
    .default(DeliveryProvider.NOVAYA_POCHTA),
  name: string()
    .required("Введите имя, пожалуйста")
    .min(5, "Cлишком короткое имя")
    .max(100, "Слишком длинное имя"),
  deliveryOption: mixed().oneOf([
    DeliveryOption.COURIER,
    DeliveryOption.TO_WAREHOUSE,
  ]),
  phone: string()
    .required("Введите номер телефона, пожалуйста")
    .min(6, "Слишком короткий номер телефона")
    .max(100, "Слишком длинный номер телефона"),
});

const validate = makeValidateSync(schema);
const steps = ["Доставка", "Проверка", "Оплата"];

type SubmitState =
  | "NOT_SUBMITTED"
  | "SENDING"
  | "OK"
  | "RETRY_TIMEOUT"
  | "RETRYING"
  | "CLIENT_ERROR"
  | "SERVER_ERROR"
  | "CANCELLED";

function CountDown({
  countDownId,
  periodSec,
}: {
  countDownId: number;
  periodSec: number;
}) {
  const [time, setTime] = useState(-1);
  useEffect(() => {
    setTime(periodSec);
    countDown(countDownId, periodSec);
  }, [countDownId]);

  function countDown(idAtStart: number, seconds: number) {
    setTimeout(() => {
      const newTime = seconds - 1;
      if (idAtStart === countDownId && newTime > 0) {
        setTime(newTime);
        countDown(idAtStart, newTime);
      }
    }, 1000);
  }

  return (
    <Typography component={"span"} display={"inline"}>
      {time.toString()}
    </Typography>
  );
}

const Checkout = ({
  cart,
  total,
  clearCart,
}: {
  cart: Record<string, CartProduct>;
  total: number;
  clearCart: () => void;
}) => {
  const classes = useStyles();
  const initialValues: Partial<OrderForm> = {
    paymentOption: PaymentOption.COD,
    deliveryProvider: DeliveryProvider.NOVAYA_POCHTA,
    deliveryOption: DeliveryOption.TO_WAREHOUSE
  };
  const [activeStep, setActiveStep] = React.useState(0);
  const [formState, setFormState] = useState<OrderForm>(initialValues);
  const [orderSubmitState, setOrderSubmitState] = useState<SubmitState>(
    "NOT_SUBMITTED"
  );
  const [retryNumber, setRetryNumber] = useState(0);

  useEffect(() => {
    if (orderSubmitState === "OK") {
      clearCart();
    }
  }, [orderSubmitState]);

  function isLastStep() {
    return activeStep === steps.length - 1;
  }

  const handleNext = (newFormState: OrderForm) => {
    if (isLastStep()) {
      postFormWithErrorHandling(newFormState).catch(console.log);
    } else {
      setFormState(newFormState);
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
    }
  };

  const handleBack = (newFormState: OrderForm) => {
    return () => {
      setFormState(newFormState);
      setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };
  };

  async function postForm(orderForm: OrderForm): Promise<Response> {
    const order: Order = {
      paymentOption: PaymentOption.COD,
      cart: cart,
      total: total,
      deliveryDetails: {
        provider: orderForm.deliveryProvider,
        option: orderForm.deliveryOption,
        address: orderForm.city + orderForm.warehouseNumber,
        phone: orderForm.phone,
        fullName: orderForm.name,
      },
    };

    return fetch("/api/orders", {
      method: "POST",
      body: JSON.stringify(order),
    });
  }

  const RETRY_INTERVAL_SEC = 10;
  function postFormWithErrorHandling(orderForm: OrderForm): Promise<void> {
    setOrderSubmitState("SENDING");
    return promiseRetry(
      async (retry, retryNumber) => {
        setRetryNumber(retryNumber);
        if (retryNumber !== 1) {
          setOrderSubmitState("RETRYING");
        }
        let newState: SubmitState = "OK";
        try {
          const res = await postForm(orderForm);
          if (res.status != 201) {
            newState = "SERVER_ERROR";
          }
        } catch (err) {
          newState = "CLIENT_ERROR";
        }
        const CLIENT_ERRORS_MAX_RETRY = 6;
        const SERVER_ERRORS_MAX_RETRY = 2;
        if (
          (newState === "CLIENT_ERROR" &&
            retryNumber <= CLIENT_ERRORS_MAX_RETRY) ||
          (newState === "SERVER_ERROR" &&
            retryNumber <= SERVER_ERRORS_MAX_RETRY)
        ) {
          newState = "RETRY_TIMEOUT";
        }

        setOrderSubmitState(newState);
        if (newState === "RETRY_TIMEOUT") {
          retry(new Error("Error posting order"));
        }
      },
      {
        retries: 100,
        factor: 1,
        minTimeout: RETRY_INTERVAL_SEC * 1000,
        maxTimeout: RETRY_INTERVAL_SEC * 1000,
      }
    );
  }

  const buttonTexts = getButtonTexts();
  const isProcessingOrder = orderSubmitState === "SENDING";
  const wasSubmitted = orderSubmitState !== "NOT_SUBMITTED";
  const isRetryState =
    orderSubmitState === "RETRY_TIMEOUT" || orderSubmitState === "RETRYING";
  const isRetryTimeout = orderSubmitState === "RETRY_TIMEOUT";
  const isOk = orderSubmitState === "OK";
  const isServerError = orderSubmitState === "SERVER_ERROR";

  function isNextDisabled<FormValues>(values: FormValues) {
    return !schema.isValidSync(values) || (wasSubmitted && isLastStep());
  }

  function snackbars() {
    return (
      <>
        <Snackbar open={isOk} TransitionComponent={Slide}>
          <Alert severity={"success"}>
            <Typography>Заказ отправлен успешно</Typography>
          </Alert>
        </Snackbar>
        <Snackbar open={isRetryState} TransitionComponent={Slide}>
          <Alert severity={"warning"}>
            <Typography>Ошибка при отправке заказа</Typography>
            <Offline>
              <Typography>Скорее всего у вас пропал интернет</Typography>
            </Offline>
            {isRetryTimeout ? (
              <Typography>
                Попробую еще раз через{" "}
                <CountDown
                  countDownId={retryNumber}
                  periodSec={RETRY_INTERVAL_SEC}
                />
              </Typography>
            ) : (
              <Typography>Отправляю заказ...</Typography>
            )}
          </Alert>
        </Snackbar>
        <Snackbar open={isServerError} TransitionComponent={Slide}>
          <Alert severity={"info"}>
            Не удалось отправить заказ из-за проблем с сайтом. Пожалуйста,
            попробуйте другой метод:
            <ContactUs />
          </Alert>
        </Snackbar>
      </>
    );
  }

  function buttons<FormValues>(values: FormValues, handleSubmit: any) {
    return (
      <Box margin={1} className={"flex justify-between"}>
        <Button disabled={activeStep === 0} onClick={handleBack(values)}>
          Назад
        </Button>
        <Box>
          <Box height={"4px"} marginTop={"-4px"} marginX={"2px"}>
            <Fade
              in={isProcessingOrder}
              style={{
                transitionDelay: isProcessingOrder ? "800ms" : "0ms",
              }}
              unmountOnExit
            >
              <LinearProgress
                style={{
                  borderRadius: "2px",
                }}
                color="secondary"
              />
            </Fade>
          </Box>
          <Button
            variant="contained"
            color="primary"
            onClick={handleSubmit}
            type="submit"
            disabled={isNextDisabled(values)}
          >
            {buttonTexts[activeStep]}
          </Button>
        </Box>
      </Box>
    );
  }

  return (
    <Paper
      style={{ padding: theme.spacing(5), marginTop: theme.spacing(1) }}
      className={"overflow-hidden "}
    >
      <FormStepper {...{ activeStep, steps }} />
      <Box padding={1}>
        <Form
          {...{ onSubmit: handleNext, validate }}
          initialValues={formState}
          render={({
            handleSubmit,
            values,
          }: {
            handleSubmit: any;
            values: OrderForm;
          }) => (
            <form noValidate>
              <div>
                {activeStep === steps.length ? (
                  <div>
                    <Button className={classes.button}>
                      <Link href={"/"}>
                        <Typography>На главную</Typography>
                      </Link>
                    </Button>
                  </div>
                ) : (
                  <Box>
                    {getStepContent(activeStep, values)}
                    {buttons(values, handleSubmit)}
                  </Box>
                )}
              </div>
            </form>
          )}
        />
      </Box>
      {snackbars()}
    </Paper>
  );
};

function mapStateToProps(state: StoreState) {
  return {
    cart: state.cartState.selectedProducts,
    total: state.cartState.total,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    clearCart: () => dispatch(clearCartAction()),
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Checkout);
