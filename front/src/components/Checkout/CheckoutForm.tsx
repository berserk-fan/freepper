import React, { useEffect, useState } from "react";
import { Form } from "react-final-form";
import { connect } from "react-redux";
import { useRouter } from "next/router";
import { CartState, clearCartAction, StoreState } from "store";
import Box from "@material-ui/core/Box/Box";
import Paper from "@material-ui/core/Paper/Paper";
import FormStepper from "./FormStepper";
import useErrorHandling from "../Commons/UseErrorHandling";
import CustomMobileStepper from "./CustomMobileStepper";
import OrderFallback from "./OrderFallback";
import {
  initialValues,
  OrderForm,
  schema,
  steps,
  toOrder,
  validate,
} from "./Definitions";
import { StepContent } from "./StepContext";
import HideOnMobile from "../Commons/HideOnMobile";
import ShowOnMobile from "../Commons/ShowOnMobile";
import FullScreenButtons from "./FullScreenButtons";
import { pages } from "../Layout/Header/pages";

type CheckoutProps = {
  cart: CartState;
  clearCart: () => void;
};

function retryPostForm(reset, postform, formState) {
  reset();
  postform(formState).catch(console.error);
}

function postForm(
  cart: CartState,
  orderForm: OrderForm,
  customFetch: (r: RequestInfo, i: RequestInit) => Promise<void>,
) {
  const order = toOrder(cart, orderForm);
  return customFetch("/api/orders", {
    method: "POST",
    body: JSON.stringify(order),
  });
}

function handleNext(
  setFormState: (
    value:
      | ((prevState: Partial<OrderForm>) => Partial<OrderForm>)
      | Partial<OrderForm>,
  ) => void,
  newFormState: OrderForm,
  isLastStep: boolean,
  postFormMemoized: (orderForm: OrderForm) => Promise<void>,
  setActiveStep: (value: ((prevState: number) => number) | number) => void,
) {
  setFormState(newFormState);
  if (isLastStep) {
    postFormMemoized(newFormState).catch(console.error);
  } else {
    setActiveStep((p) => p + 1);
  }
}

function handleBack(
  setFormState: (
    value:
      | ((prevState: Partial<OrderForm>) => Partial<OrderForm>)
      | Partial<OrderForm>,
  ) => void,
  newFormState: OrderForm,
  setActiveStep: (value: ((prevState: number) => number) | number) => void,
) {
  setFormState(newFormState);
  setActiveStep((p) => p - 1);
}

function isNextDisabled(
  values: any,
  wasSubmitted: boolean,
  isLastStep: boolean,
): boolean {
  return !schema.isValidSync(values) || (wasSubmitted && isLastStep);
}

function Checkout({ cart, clearCart }: CheckoutProps) {
  const router = useRouter();
  const [activeStep, setActiveStep] = React.useState(0);
  const [formState, setFormState] = useState(initialValues);
  const serverRetries = 2;
  const retryPeriod = 10;
  const {
    submitState: orderSubmitState,
    currentRetry,
    customFetch,
    cancel,
    reset,
  } = useErrorHandling(clearCart, serverRetries, retryPeriod);

  const isLastStep = activeStep === steps.length - 1;
  const wasSubmitted = orderSubmitState !== "NOT_SUBMITTED";

  const postFormMemoized = React.useCallback(
    (orderForm: OrderForm) => postForm(cart, orderForm, customFetch),
    [cart, customFetch],
  );

  const retryPostFormMemoized = React.useCallback(
    () => retryPostForm(reset, postFormMemoized, formState),
    [reset, formState, postFormMemoized],
  );

  const handleNextMemoized = React.useCallback(
    (newFormState: OrderForm) =>
      handleNext(
        setFormState,
        newFormState,
        isLastStep,
        postFormMemoized,
        setActiveStep,
      ),
    [setFormState, isLastStep, postFormMemoized, setActiveStep],
  );
  const handleBackMemoized = React.useCallback(
    (newFormState: OrderForm) => () => {
      handleBack(setFormState, newFormState, setActiveStep);
    },
    [setFormState, setActiveStep],
  );
  const isNextDisabledMemoized = React.useCallback(
    (values: any) => isNextDisabled(values, wasSubmitted, isLastStep),
    [wasSubmitted, isLastStep],
  );

  useEffect(() => {
    if (orderSubmitState === "OK") {
      router.push(pages["checkout-success"].path);
    }
  }, [orderSubmitState]);

  return (
    <Box marginX="16px">
      <Form
        {...{ onSubmit: handleNextMemoized, validate }}
        initialValues={formState}
        render={({ handleSubmit, values }) => (
          <form noValidate>
            <ShowOnMobile>
              <Box className="flex flex-col justify-center w-full">
                <StepContent step={activeStep} orderData={values} />
                <CustomMobileStepper
                  handleBack={handleBackMemoized(values)}
                  activeStep={activeStep}
                  isNextDisabled={isNextDisabledMemoized(values)}
                  handleNext={handleSubmit}
                  maxSteps={steps.length}
                />
              </Box>
            </ShowOnMobile>
            <HideOnMobile>
              <Paper className="overflow-hidden">
                <Box p={2} m={1}>
                  <FormStepper {...{ activeStep, steps }} />
                  <StepContent step={activeStep} orderData={values} />
                  <FullScreenButtons
                    activeStep={activeStep}
                    backDisabled={activeStep === 0}
                    nextDisabled={isNextDisabledMemoized(values)}
                    onNext={handleSubmit}
                    onBack={handleBackMemoized(values)}
                  />
                </Box>
              </Paper>
            </HideOnMobile>
          </form>
        )}
      />
      <OrderFallback
        orderSubmitState={orderSubmitState}
        retryNumber={currentRetry}
        retryPeriod={retryPeriod}
        onClose={reset}
        onCancel={cancel}
        onRetry={retryPostFormMemoized}
      />
    </Box>
  );
}

const mapStateToProps = (state: StoreState) => ({ cart: state.cartState });
const mapDispatchToProps = (dispatch) => ({
  clearCart: () => dispatch(clearCartAction()),
});
export default connect(mapStateToProps, mapDispatchToProps)(Checkout);
