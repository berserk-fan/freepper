import React from "react";
import {SvgIcon} from "@material-ui/core";
import PedBed from "./pet-bed.svg"
export default function PetBedIcon(props) {
    return <SvgIcon fontSize={"large"} viewBox={"0 0 33 33"} {...props}>{<PedBed/>}</SvgIcon>
}
