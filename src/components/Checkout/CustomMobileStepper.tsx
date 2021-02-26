import React, { MouseEventHandler } from "react";
import { MobileStepper, Theme } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import { KeyboardArrowLeft, KeyboardArrowRight } from "@material-ui/icons";
import { makeStyles } from "@material-ui/styles";
import { buttonTexts } from "./Definitions";

const useStyles = makeStyles((theme: Theme) => ({
  dotActive: {
    backgroundColor: theme.palette.secondary.dark,
  },
}));

type MobileForm = {
  handleBack: MouseEventHandler;
  activeStep: number;
  isNextDisabled: boolean;
  handleNext: MouseEventHandler;
  maxSteps: number;
};

export default function CustomMobileStepper({
  handleBack,
  handleNext,
  activeStep,
  isNextDisabled,
  maxSteps,
}: MobileForm) {
  const classes = useStyles();
  return (
    <MobileStepper
      classes={classes}
      variant="dots"
      steps={maxSteps}
      position="static"
      activeStep={activeStep}
      nextButton={
        <Button size="small" onClick={handleNext} disabled={isNextDisabled}>
          {buttonTexts[activeStep]}
          <KeyboardArrowRight />
        </Button>
      }
      backButton={
        <Button size="small" onClick={handleBack} disabled={activeStep === 0}>
          <KeyboardArrowLeft />
          Назад
        </Button>
      }
    />
  );
}
