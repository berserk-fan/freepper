import React, { MouseEventHandler } from "react";
import { MobileStepper } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import theme from "../../theme";
import { KeyboardArrowLeft, KeyboardArrowRight } from "@material-ui/icons";
import { buttonTexts } from "./Definitions";
import MakeRequestWrapper from "../Commons/MakeRequestWrapper";
import {makeStyles} from "@material-ui/styles";


const useStyles = makeStyles({
  dotActive: {
    backgroundColor: theme.palette.secondary.dark
  }
});


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
