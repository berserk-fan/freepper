import React from "react";
import {SvgIcon} from "@material-ui/core";
import CollarIcon from "./collar.svg"

export default function Collar(props) {
    return <SvgIcon fontSize={"large"} viewBox={"0 0 64 64"} {...props}><CollarIcon /></SvgIcon>
}
