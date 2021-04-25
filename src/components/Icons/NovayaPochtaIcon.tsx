import makeStyles from "@material-ui/core/styles/makeStyles";
import SvgIcon from "@material-ui/core/SvgIcon";
import React, { memo } from "react";
import NovayaPochta from "./svg/Nova_Poshta_2014_logo.svg";

const useStyles = makeStyles({
  root: {
    width: "4.2rem",
    height: "1.5rem",
  },
});

function NovayaPochtaIcon() {
  const classes = useStyles();
  return (
    <SvgIcon classes={classes} viewBox="0 0 210 75">
      <NovayaPochta />
    </SvgIcon>
  );
}

export default memo(NovayaPochtaIcon);
