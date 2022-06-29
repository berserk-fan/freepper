import React, { memo, MouseEventHandler } from "react";
import Button from "@mui/material/Button";
import MobileStepper from "@mui/material/MobileStepper";
import makeStyles from "@mui/styles/makeStyles";
import KeyboardArrowRight from "@mui/icons-material/KeyboardArrowRight";
import KeyboardArrowLeft from "@mui/icons-material/KeyboardArrowLeft";
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
        <Button
          size="small"
          color="secondary"
          variant="outlined"
          onClick={handleNext}
          disabled={isNextDisabled}
          endIcon={<KeyboardArrowRight />}
        >
          {buttonTexts[activeStep]}
        </Button>
      }
      backButton={
        <Button
          size="small"
          color="secondary"
          variant="outlined"
          onClick={handleBack}
          disabled={activeStep === 0}
          startIcon={<KeyboardArrowLeft />}
        >
          Назад
        </Button>
      }
    />
  );
}

export default memo(CustomMobileStepper);
