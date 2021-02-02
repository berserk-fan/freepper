import React, { MouseEventHandler, useEffect, useState } from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import dynamic from "next/dynamic";
import DeliveryDetailsStep from "./DeliveryDetailsStep";
import {
  Box,
  MobileStepper,
  Paper,
  Typography,
  useMediaQuery,
} from "@material-ui/core";
import { Form } from "react-final-form";
import { mixed, number, object, ObjectSchema, string } from "yup";
import { makeValidateSync } from "mui-rff";
import {
  DeliveryOption,
  DeliveryProvider,
  Order,
  PaymentOption,
} from "../../order-model";
import FormStepper from "./FormStepper";
import { CartProduct } from "../../pages/checkout";
import { clearCartAction, StoreState } from "../../store";
import { connect } from "react-redux";
import theme from "../../theme";
import MakeRequestWrapper from "../Commons/MakeRequestWrapper";
import SuccessStep from "./SuccessStep";
import useErrorHandling from "../Commons/UseErrorHandling";
import ErrorSnackbars from "../Commons/ErrorSnackbars";
import CustomMobileStepper from "./CustomMobileStepper";

const SummaryStep = dynamic(() => import("./SummaryStep"));
const PaymentStep = dynamic(() => import("./Payment"));
const ContactUs = dynamic(() => import("./ContactUs"));

function getButtonTexts() {
  return ["К проверке заказа", "К оплате заказа", "Отправить заказ"];
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

function StepContent({
  step,
  orderData,
}: {
  step: number;
  orderData: OrderForm;
}) {
  console.log(step);
  switch (step) {
    case 0:
      return <DeliveryDetailsStep orderForm={orderData} />;
    case 1:
      return <SummaryStep orderForm={orderData} />;
    case 2:
      return <PaymentStep />;
    case 3:
      return <SuccessStep />;
    default:
      throw new Error("unknown step");
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
const steps = ["Доставка", "Проверка", "Оплата", "Успех"];

function Content() {
  return (
    <div>
      Не удалось отправить заказ из-за проблем с сайтом. Пожалуйста, попробуйте
      другой метод:
      <ContactUs />
    </div>
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
  const initialValues: Partial<OrderForm> = {
    paymentOption: PaymentOption.COD,
    deliveryProvider: DeliveryProvider.NOVAYA_POCHTA,
    deliveryOption: DeliveryOption.TO_WAREHOUSE,
  };
  const [activeStep, setActiveStep] = React.useState(0);
  const [formState, setFormState] = useState<OrderForm>(initialValues);
  const clientRetries = 6;
  const serverRetries = 2;
  const retryPeriod = 10;
  const [orderSubmitState, makeACall, retryNumber] = useErrorHandling(
    clearCart,
    clientRetries,
    serverRetries,
    retryPeriod
  );
  const smallScreen = useMediaQuery(theme.breakpoints.down("xs"));

  function CheckoutBox({ children }: { children: React.ReactNode }) {
    return smallScreen ? (
      <Box height={"calc()"} className={"flex flex-col justify-center w-full"}>
        {children}
      </Box>
    ) : (
      <Paper
        style={{ padding: theme.spacing(2), marginTop: theme.spacing(1) }}
        className={"overflow-hidden"}
      >
        {children}
      </Paper>
    );
  }

  function isCallStep() {
    return activeStep === steps.length - 2;
  }

  const handleNext = (newFormState: OrderForm) => {
    if (isCallStep()) {
      makeACall(() => postForm(newFormState)).catch(console.log);
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

  const buttonTexts = getButtonTexts();
  const wasSubmitted = orderSubmitState !== "NOT_SUBMITTED";

  function isNextDisabled<FormValues>(values: FormValues) {
    return !schema.isValidSync(values) || (wasSubmitted && isCallStep());
  }

  function FullScreenFormButtons<FormValues>({
    values,
    handleNext,
  }: {
    values: FormValues;
    handleNext: any;
  }) {
    return (
      <Box margin={1} className={"flex justify-between"}>
        <Button disabled={activeStep === 0} onClick={handleBack(values)}>
          Назад
        </Button>
        <MakeRequestWrapper isProcessing={orderSubmitState === "SENDING"}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleNext}
            type="submit"
            disabled={isNextDisabled(values)}
          >
            {buttonTexts[activeStep]}
          </Button>
        </MakeRequestWrapper>
      </Box>
    );
  }

  return (
    <CheckoutBox>
      {!smallScreen && <FormStepper {...{ activeStep, steps }} />}
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
            <StepContent step={activeStep} orderData={values} />
            {smallScreen ? (
              <CustomMobileStepper
                values={values}
                handleBack={handleBack(values)}
                activeStep={activeStep}
                isNextDisabled={isNextDisabled(values)}
                handleNext={handleSubmit}
                maxSteps={steps.length}
              />
            ) : (
              <FullScreenFormButtons
                values={values}
                handleNext={handleSubmit}
              />
            )}
          </form>
        )}
      />
      <ErrorSnackbars
        submitState={orderSubmitState}
        retryNumber={retryNumber}
        retryPeriodSec={retryPeriod}
        content={<Content />}
        errorMessage={<Typography>Ошибка при отправке заказа</Typography>}
        successMessage={<Typography>Заказ отправлен успешно</Typography>}
        retryingMessage={<Typography>Отправляю заказ...</Typography>}
      />
    </CheckoutBox>
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
