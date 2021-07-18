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

  async function postForm(orderForm: OrderForm): Promise<void> {
    const order = toOrder(cart, orderForm);
    return customFetch("/api/orders", {
      method: "POST",
      body: JSON.stringify(order),
    });
  }

  function retryPostForm() {
    reset();
    postForm(formState).catch(console.error);
  }

  const handleNext = (newFormState: OrderForm) => {
    setFormState(newFormState);
    if (isLastStep) {
      postForm(newFormState).catch(console.error);
    } else {
      setActiveStep((p) => p + 1);
    }
  };

  const handleBack = (newFormState: OrderForm) => () => {
    setFormState(newFormState);
    setActiveStep((p) => p - 1);
  };

  function isNextDisabled<FormValues>(values: FormValues) {
    return !schema.isValidSync(values) || (wasSubmitted && isLastStep);
  }

  useEffect(() => {
    if (orderSubmitState === "OK") {
      router.push(pages["checkout-success"].path);
    }
  }, [orderSubmitState]);

  return (
    <Box marginX="16px">
      <Form
        {...{ onSubmit: handleNext, validate }}
        initialValues={formState}
        render={({ handleSubmit, values }) => (
          <form noValidate>
            <ShowOnMobile>
              <Box className="flex flex-col justify-center w-full">
                <StepContent step={activeStep} orderData={values} />
                <CustomMobileStepper
                  handleBack={handleBack(values)}
                  activeStep={activeStep}
                  isNextDisabled={isNextDisabled(values)}
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
                    nextDisabled={isNextDisabled(values)}
                    onNext={handleSubmit}
                    onBack={handleBack(values)}
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
        onRetry={retryPostForm}
      />
    </Box>
  );
}

const mapStateToProps = (state: StoreState) => ({ cart: state.cartState });
const mapDispatchToProps = (dispatch) => ({
  clearCart: () => dispatch(clearCartAction()),
});
export default connect(mapStateToProps, mapDispatchToProps)(Checkout);
