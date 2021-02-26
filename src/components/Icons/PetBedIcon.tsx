import React, {memo} from "react";
import { SvgIcon } from "@material-ui/core";
import PedBed from "./svg/pet-bed.svg";

function PetBedIcon(props) {
  return (
    <SvgIcon fontSize="large" viewBox="0 0 32 32" {...props}>
      <PedBed />
    </SvgIcon>
  );
}

export default memo(PetBedIcon)
