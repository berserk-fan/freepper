import React, { memo, MouseEventHandler } from "react";
import Button from "@material-ui/core/Button";
import MobileStepper from "@material-ui/core/MobileStepper/MobileStepper";
import makeStyles from "@material-ui/core/styles/makeStyles";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import { buttonTexts } from "./Definitions";

const useStyles = makeStyles((theme) => ({
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

function CustomMobileStepper({
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

export default memo(CustomMobileStepper);
