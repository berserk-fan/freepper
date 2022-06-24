import React, { MouseEventHandler } from "react";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import { buttonTexts } from "./Definitions";

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
    <Box margin={1} className="flex justify-between">
      <Button
        disabled={backDisabled}
        onClick={onBack}
        variant="outlined"
        color="secondary"
      >
        Назад
      </Button>
      <Button
        variant="outlined"
        color="secondary"
        onClick={onNext}
        type="submit"
        disabled={nextDisabled}
      >
        {buttonTexts[activeStep]}
      </Button>
    </Box>
  );
}
