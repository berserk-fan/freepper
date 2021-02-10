import NovayaPochtaIcon from "./Nova_Poshta_2014_logo.svg";
import {SvgIcon} from "@material-ui/core";
import React from "react";
import {makeStyles} from "@material-ui/styles";

const useStyles = makeStyles({
    largeIcon: {
        width: "4.2rem",
        height: "1.5rem",
    },
});

export default function NovayaPochtaIcon() {
    const classes = useStyles();
    return <SvgIcon className={classes.largeIcon} viewBox={"0 0 210 75"}>
        <NovayaPochtaIcon />
    </SvgIcon>
}
