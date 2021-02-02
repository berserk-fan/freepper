import React, { MouseEventHandler } from "react";
import { MobileStepper } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import theme from "../../theme";
import { KeyboardArrowLeft, KeyboardArrowRight } from "@material-ui/icons";

export default function CustomMobileStepper<FormValues>({
  handleBack,
  handleNext,
  activeStep,
  isNextDisabled,
  maxSteps,
}: {
  values: FormValues;
  handleBack: MouseEventHandler;
  activeStep: number;
  isNextDisabled: boolean;
  handleNext: MouseEventHandler;
  maxSteps: number;
}) {
  return (
    <MobileStepper
      variant="dots"
      steps={maxSteps}
      position="static"
      activeStep={activeStep}
      nextButton={
        <Button size="small" onClick={handleNext} disabled={isNextDisabled}>
          Вперед
          {theme.direction === "rtl" ? (
            <KeyboardArrowLeft />
          ) : (
            <KeyboardArrowRight />
          )}
        </Button>
      }
      backButton={
        <Button size="small" onClick={handleBack} disabled={activeStep === 0}>
          {theme.direction === "rtl" ? (
            <KeyboardArrowRight />
          ) : (
            <KeyboardArrowLeft />
          )}
          Назад
        </Button>
      }
    />
  );
}
