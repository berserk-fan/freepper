import React, { MouseEventHandler } from "react";
import { Box } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import MakeRequestWrapper from "../Commons/MakeRequestWrapper";
import { buttonTexts } from "./Definitions";

export default function FullScreenButtons({
  onBack,
  onNext,
  nextDisabled,
  processing,
  activeStep,
  backDisabled,
}: {
  onBack: MouseEventHandler;
  nextDisabled: boolean;
  backDisabled: boolean;
  onNext: MouseEventHandler;
  processing: boolean;
  activeStep: number;
}) {
  return (
    <Box margin={1} className={"flex justify-between"}>
      <Button disabled={backDisabled} onClick={onBack}>
        Назад
      </Button>
      <MakeRequestWrapper isProcessing={processing}>
        <Button
          variant="contained"
          color="primary"
          onClick={onNext}
          type="submit"
          disabled={nextDisabled}
        >
          {buttonTexts[activeStep]}
        </Button>
      </MakeRequestWrapper>
    </Box>
  );
}
