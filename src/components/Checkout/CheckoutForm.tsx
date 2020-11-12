import React, { useState } from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import SummaryStep from "./SummaryStep";
import DeliveryDetailsStep from "./DeliveryDetailsStep";
import { CartProduct } from "../../pages/checkout";
import { Box, Container, Paper, useMediaQuery } from "@material-ui/core";
import PaymentStep from "./Payment";
import { Form } from "react-final-form";
import { mixed, object, number, ObjectSchema, string } from "yup";
import { makeValidate, makeValidateSync } from "mui-rff";
import {
  DeliveryDetails,
  DeliveryOption,
  DeliveryProvider,
  Order,
  PaymentOption,
} from "../../order-model";
import FormStepper from "./FormStepper";
import theme from "../../theme";
import CheckoutHeader from "../Layout/Header/CheckoutHeader";

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
    "Оплатить заказ",
  ];
}

export type OrderForm = Omit<Order, "deliveryDetails" | "cart" | "total"> & {
  deliveryDetails?: Partial<DeliveryDetails>;
};

function getStepContent(
  step: number,
  orderData: OrderForm
) {
  switch (step) {
    case 0:
      return <DeliveryDetailsStep order={orderData} />;
    case 1:
      return <SummaryStep orderForm={orderData} />;
    case 2:
      return <PaymentStep />;
    default:
      return "Unknown step";
  }
}

const deliveryDetailsSchema: ObjectSchema<DeliveryDetails> = object({
  address: string()
    .required("Пожалуйста, введите адрес")
    .max(500, "Слишком длинный адрес"),
  provider: mixed()
    .oneOf([DeliveryProvider.NOVAYA_POCHTA])
    .required()
    .default(DeliveryProvider.NOVAYA_POCHTA),
  fullName: string()
    .required("Пожалуйста, введите имя")
    .min(5, "Cлишком короткое имя")
    .max(500, "Слишком длинное имя"),
  phone: string()
    .required("Пожалуйста, введите номер телефона")
    .min(6, "Слишком короткий номер телефона")
    .max(500, "Слишком длинный номер телефона"),
  email: string(),
  option: mixed().oneOf([DeliveryOption.COURIER, DeliveryOption.TO_WAREHOUSE]),
});

const schema: ObjectSchema<OrderForm> = object({
  deliveryDetails: deliveryDetailsSchema,
  paymentOption: mixed().oneOf([PaymentOption.COD]).default(PaymentOption.COD),
});

const validate = makeValidateSync(schema);

export default function Checkout() {
  const classes = useStyles();
  const steps = ["Доставка", "Проверка", "Оплата"];
  const initialValues = {
    deliveryDetails: { provider: DeliveryProvider.NOVAYA_POCHTA },
    paymentOption: PaymentOption.COD,
  };
  const [activeStep, setActiveStep] = React.useState(0);
  const [formState, setFormState] = useState<OrderForm>(initialValues);
  const handleNext = (formState: OrderForm) => {
    return () => {
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
      setFormState(formState);
    };
  };

  const handleBack = (formState: OrderForm) => {
    return () => {
      setActiveStep((prevActiveStep) => prevActiveStep - 1);
      setFormState(formState);
    };
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  function onSubmit() {}

  const buttonTexts = getButtonTexts();
  return (
    <>
      <FormStepper {...{ activeStep, steps }} />
      <Form
        {...{ onSubmit, validate }}
        initialValues={formState}
        render={({
          handleSubmit,
          values,
        }: {
          handleSubmit: any;
          values: OrderForm;
        }) => (
          <form onSubmit={handleSubmit} noValidate>
            <div>
              {activeStep === steps.length ? (
                <div>
                  <Button onClick={handleReset} className={classes.button}>
                    Reset
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
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={handleNext(values)}
                      disabled={(() => {
                        console.log(validate(values));
                        return !schema.isValidSync(values);
                      })()}
                    >
                      {buttonTexts[activeStep]}
                    </Button>
                  </Box>
                </div>
              )}
            </div>
          </form>
        )}
      />
    </>
  );
}
