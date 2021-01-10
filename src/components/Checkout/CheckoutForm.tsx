import React, {useEffect, useState} from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import SummaryStep from "./SummaryStep";
import DeliveryDetailsStep from "./DeliveryDetailsStep";
import {
  Backdrop,
  Box,
  CircularProgress,
  Fade,
  LinearProgress,
  Slide,
  Snackbar,
  Typography,
} from "@material-ui/core";
import PaymentStep from "./Payment";
import { Form } from "react-final-form";
import { mixed, object, ObjectSchema, string } from "yup";
import { makeValidateSync } from "mui-rff";
import {
  DeliveryOption,
  DeliveryProvider,
  Order,
  PaymentOption,
} from "../../order-model";
import FormStepper from "./FormStepper";
import Link from "next/link";
import { CartProduct } from "../../pages/checkout";
import { StoreState } from "../../store";
import exp from "constants";
import { connect } from "react-redux";
import promiseRetry from "promise-retry";
import {Alert} from "@material-ui/lab";

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
    "Оплатить при получении 😌",
  ];
}

export type OrderForm = Partial<{
  address: string;
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
  address: string()
    .required("Введите адрес, пожалуйста")
    .max(500, "Слишком длинный адрес"),
  deliveryProvider: mixed()
    .oneOf([DeliveryProvider.NOVAYA_POCHTA])
    .required()
    .default(DeliveryProvider.NOVAYA_POCHTA),
  name: string()
    .required("Введите имя, пожалуйста")
    .min(5, "Cлишком короткое имя")
    .max(500, "Слишком длинное имя"),
  deliveryOption: mixed().oneOf([
    DeliveryOption.COURIER,
    DeliveryOption.TO_WAREHOUSE,
  ]),
  phone: string()
    .required("Введите номер телефона, пожалуйста")
    .min(6, "Слишком короткий номер телефона")
    .max(500, "Слишком длинный номер телефона"),
});

const validate = makeValidateSync(schema);
const steps = ["Доставка", "Проверка", "Оплата"];

function postOrder(order: Order): Promise<Response> {
  return fetch("/api/orders", {
    method: "POST",
    body: JSON.stringify(order),
  });
}

type SubmitState =
  | "NOT_SUBMITTED"
  | "SENDING"
  | "OK"
  | "RETRYING"
  | "CLIENT_ERROR"
  | "SERVER_ERROR";


function CountDown({countDownId, periodSec}: {countDownId: number, periodSec: number}){
  console.log("RENDRING" + countDownId.toString());
  const [time, setTime] = useState(-1);

  useEffect(() => {
    console.log("Effect triggered");
    setTime(periodSec);
    countDown(countDownId, periodSec);
  }, [countDownId]);

  function countDown(idAtStart: number, seconds: number) {
    setTimeout(() => {
      const newTime = seconds - 1;
      if(idAtStart === countDownId && newTime > 0) {
        setTime(newTime);
        countDown(idAtStart, newTime);
      }
    }, 1000)
  }

  return <Typography display={"inline"}>{time.toString()}</Typography>
}

const Checkout = ({
  cart,
  total,
}: {
  cart: Record<string, CartProduct>;
  total: number;
}) => {
  const classes = useStyles();
  const initialValues = {
    paymentOption: PaymentOption.COD,
    deliveryProvider: DeliveryProvider.NOVAYA_POCHTA,
  };
  const [activeStep, setActiveStep] = React.useState(0);
  const [formState, setFormState] = useState<OrderForm>(initialValues);
  const [orderSubmitState, setOrderSubmitState] = useState<SubmitState>("NOT_SUBMITTED");
  const [retryNumber, setRetryNumber] = useState(0);

  const handleSubmit = (newFormState: OrderForm) => {
    const isLastStep = activeStep === steps.length - 1;
    if (isLastStep) {
      postOrderWithErrorHandling(newFormState).catch(console.log);
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

  async function submitForm(orderForm: OrderForm): Promise<Response> {
    const order: Order = {
      paymentOption: PaymentOption.COD,
      cart: cart,
      total: total,
      deliveryDetails: {
        provider: orderForm.deliveryProvider,
        option: orderForm.deliveryOption,
        address: orderForm.address,
        phone: orderForm.phone,
        fullName: orderForm.name,
      },
    };
    return postOrder(order);
  }

  const RETRY_INTERVAL_SEC = 10;
  function postOrderWithErrorHandling(orderForm: OrderForm): Promise<void> {
    setOrderSubmitState("SENDING");
    return promiseRetry(
      async (retry, retryNumber) => {
        setRetryNumber(retryNumber);
        let newState: SubmitState = "OK";
        try {
          const res = await submitForm(orderForm);
          if (res.status != 201) {
            newState = "SERVER_ERROR";
          }
        } catch (err) {
          newState = "CLIENT_ERROR";
        }
        const CLIENT_ERRORS_MAX_RETRY = 6;
        const SERVER_ERRORS_MAX_RETRY = 3;
        if (
          (newState === "CLIENT_ERROR" && retryNumber <= CLIENT_ERRORS_MAX_RETRY) ||
          (newState === "SERVER_ERROR" && retryNumber <= SERVER_ERRORS_MAX_RETRY)
        ) {
          newState = "RETRYING";
        }

        setOrderSubmitState(newState);
        if (newState === "RETRYING") {
          retry(new Error("Error posting order"));
        }
      },
      { retries: 100, factor: 1, minTimeout: RETRY_INTERVAL_SEC * 1000, maxTimeout: RETRY_INTERVAL_SEC * 1000}
    );
  }

  const buttonTexts = getButtonTexts();
  const isProcessingOrder = orderSubmitState === "SENDING";
  const retryingPostingOrder = orderSubmitState === "RETRYING";
  return (
    <>
      <FormStepper {...{ activeStep, steps }} />
      <Box padding={1}>
        <Form
          {...{ onSubmit: handleSubmit, validate }}
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
                  <div>
                    {getStepContent(activeStep, values)}
                    <Box className={"flex justify-between"}>
                      <Button
                        disabled={activeStep === 0}
                        onClick={handleBack(values)}
                      >
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
                          disabled={!schema.isValidSync(values)}
                        >
                          {buttonTexts[activeStep]}
                        </Button>
                      </Box>
                    </Box>
                  </div>
                )}
              </div>
            </form>
          )}
        />
      </Box>
      <Snackbar
        open={retryingPostingOrder}
        TransitionComponent={Slide}

      >
        <Alert severity={"warning"}>
          <Typography>Ошибка при отправке заказа.</Typography>
          <Typography>Пробую еще раз через: <CountDown countDownId={retryNumber} periodSec={RETRY_INTERVAL_SEC}/> сек</Typography>
        </Alert>
      </Snackbar>
    </>
  );
};

function mapStateToProps(state: StoreState) {
  return {
    cart: state.cartState.selectedProducts,
    total: state.cartState.total,
  };
}

export default connect(mapStateToProps, null)(Checkout);
