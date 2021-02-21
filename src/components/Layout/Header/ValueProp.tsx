import React from "react";
import { Theme, Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles((theme: Theme) => ({
  container: {
    borderStyle: "solid",
    borderBottomStyle: "solid",
    borderTop: 1,
    borderBottom: 1,
    paddingTop: theme.spacing(1),
    paddingBottom: theme.spacing(1),
  },
  valueText: {
    fontSize: "0.65rem",
    [theme.breakpoints.up("sm")]: {
      fontSize: "0.75rem",
    },
    [theme.breakpoints.up("md")]: {
      fontSize: "0.8rem",
    },
    color: theme.palette.text.disabled,
    textTransform: "uppercase",
    fontWeight: theme.typography.fontWeightLight,
  },
  boldSpan: {
    color: theme.palette.text.secondary,
    fontWeight: theme.typography.fontWeightBold,
  },
}));

export default function ValueProp() {
  const classes = useStyles();
  return (
    <div className={classes.container}>
      <Typography
        align='center'
        className={classes.valueText}
        variant='subtitle2'
        color='textSecondary'
      >
        <span className={classes.boldSpan}>Бесплатная доставка </span>
        по Украине, Бесплатные возвраты
      </Typography>
    </div>
  );
}
