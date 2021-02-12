import React, { MouseEventHandler } from "react";
import { Box } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import MakeRequestWrapper from "../Commons/MakeRequestWrapper";
import { buttonTexts } from "./Definitions";
import {makeStyles} from "@material-ui/styles";
import theme from "../../theme";

type FullScreenButtonsProps = {
  onBack: MouseEventHandler;
  nextDisabled: boolean;
  backDisabled: boolean;
  onNext: MouseEventHandler;
  activeStep: number;
};

export default function FullScreenButtons({
  onBack,
  onNext,
  nextDisabled,
  activeStep,
  backDisabled,
}: FullScreenButtonsProps) {
  return (
    <Box margin={1} className={"flex justify-between"}>
      <Button disabled={backDisabled} onClick={onBack}>
        Назад
      </Button>
      <Button
        variant="contained"
        color="primary"
        onClick={onNext}
        type="submit"
        disabled={nextDisabled}
      >
        {buttonTexts[activeStep]}
      </Button>
    </Box>
  );
}
