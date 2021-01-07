import React, { useState } from "react";
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
  const [handling, setHandling] = useState(false);

  const handleSubmit = (newFormState: OrderForm) => {
    const isLastStep = activeStep === steps.length - 1;
    if (isLastStep) {
      return onSubmit(newFormState);
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

  function onSubmit(orderForm: OrderForm) {
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

    setHandling(true);
    fetch("/api/orders", {
      method: "POST",
      body: JSON.stringify(order),
    }).then((resp) => {
      setHandling(false);
    });
  }

  const buttonTexts = getButtonTexts();
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
                            in={handling}
                            style={{
                              transitionDelay: handling ? "800ms" : "0ms",
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
