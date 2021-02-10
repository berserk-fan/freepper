import React, {useState} from "react";
import {Box, Paper} from "@material-ui/core";
import {Form} from "react-final-form";
import FormStepper from "./FormStepper";
import {clearCartAction, StoreState} from "../../store";
import {connect} from "react-redux";
import useErrorHandling from "../Commons/UseErrorHandling";
import CustomMobileStepper from "./CustomMobileStepper";
import OrderFallback from "./OrderFallback";
import {initialValues, OrderForm, schema, steps, toOrder, validate} from "./Definitions";
import {StepContent} from "./StepContext";
import HideOnMobile from "../Commons/HideOnMobile";
import ShowOnMobile from "../Commons/ShowOnMobile";
import FullScreenButtons from "./FullScreenButtons";
import {CartState} from "../Cart/Cart";

type CheckoutProps = {
  cart: CartState;
  clearCart: () => void;
};

const Checkout = ({cart, clearCart,}: CheckoutProps) => {
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

  const handleBack = (newFormState: OrderForm) => {
    return () => {
      setFormState(newFormState);
      setActiveStep((p) => p - 1);
    };
  };

  async function postForm(orderForm: OrderForm): Promise<void> {
    const order = toOrder(cart, orderForm);
    return customFetch("/api/orders", {
      method: "POST",
      body: JSON.stringify(order),
    });
  }

  function isNextDisabled<FormValues>(values: FormValues) {
    return !schema.isValidSync(values) || (wasSubmitted && isLastStep);
  }

  return (
    <Box>
      <Form
        {...{ onSubmit: handleNext, validate }}
        initialValues={formState}
        render={({ handleSubmit, values }) => (
          <form noValidate>
            <ShowOnMobile>
              <Box className={"flex flex-col justify-center w-full"}>
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
              <Paper className={"overflow-hidden"}>
                <Box p={2} m={1}>
                  <FormStepper {...{ activeStep, steps }} />
                  <StepContent step={activeStep} orderData={values} />
                  <FullScreenButtons
                      activeStep={activeStep}
                      backDisabled={activeStep === 0}
                      nextDisabled={isNextDisabled(values)}
                      onNext={handleSubmit}
                      onBack={handleBack(values)}
                      processing={orderSubmitState === "SENDING"}
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
};

const mapStateToProps = (state: StoreState) => ({cart: state.cartState,});
const mapDispatchToProps = dispatch => ({clearCart: () => dispatch(clearCartAction()),});
export default connect(mapStateToProps, mapDispatchToProps)(Checkout);
