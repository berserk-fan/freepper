import React, { useState } from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import SummaryStep from "./SummaryStep";
import DeliveryDetailsStep from "./DeliveryDetailsStep";
import { CartProduct } from "../../pages/checkout";
import {Box, Container, Paper, Typography, useMediaQuery} from "@material-ui/core";
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
import Link from "next/link";

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
    "Закончить 😌",
  ];
}

export type OrderForm = Partial<{
    address: string,
    deliveryProvider: DeliveryProvider,
    name: string,
    deliveryOption: DeliveryOption,
    phone: string
}>

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
    deliveryOption: mixed().oneOf([DeliveryOption.COURIER, DeliveryOption.TO_WAREHOUSE]),
    phone: string()
        .required("Введите номер телефона, пожалуйста")
        .min(6, "Слишком короткий номер телефона")
        .max(500, "Слишком длинный номер телефона"),
});

const validate = makeValidateSync(schema);
const steps = ["Доставка", "Проверка", "Оплата"];

export default function Checkout() {
  const classes = useStyles();
  const initialValues = {
    paymentOption: PaymentOption.COD,
    deliveryProvider: DeliveryProvider.NOVAYA_POCHTA
  };
  const [activeStep, setActiveStep] = React.useState(0);
  const [formState, setFormState] = useState<OrderForm>(initialValues);
  const handleNext = (newFormState: OrderForm) => {
    return () => {
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
      setFormState(newFormState);
    };
  };

  const handleBack = (newFormState: OrderForm) => {
    return () => {
      setActiveStep((prevActiveStep) => prevActiveStep - 1);
      setFormState(newFormState);
    };
  };

  function onSubmit() {}

  const buttonTexts = getButtonTexts();
  return (
    <>
      <FormStepper {...{ activeStep, steps }} />
          <Box padding={1}>
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
                              <Button className={classes.button}>
                                  <Link href={"/"}>
                                      <Typography>
                                          На главную
                                      </Typography>
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
                                  <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={handleNext(values)}
                                        disabled={!schema.isValidSync(values)}
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
          </Box>
    </>
  );
}
