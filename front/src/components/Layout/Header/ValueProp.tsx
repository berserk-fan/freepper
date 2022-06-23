import { Theme } from "@mui/material/styles";
import makeStyles from "@mui/material/styles/makeStyles";
import Typography from "@mui/material/Typography";
import React, { memo } from "react";

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
    fontWeight: theme.typography.fontWeightLight as Exclude<
      React.CSSProperties["fontWeight"],
      string & {}
    >,
  },
  boldSpan: {
    color: theme.palette.text.secondary,
    fontWeight: theme.typography.fontWeightBold as Exclude<
      React.CSSProperties["fontWeight"],
      string & {}
    >,
  },
}));

function ValueProp() {
  const classes = useStyles();
  return (
    <section className={classes.container}>
      <Typography
        align="center"
        className={classes.valueText}
        variant="subtitle2"
        color="textSecondary"
      >
        <span className={classes.boldSpan}>Бесплатная доставка </span>
        по Украине, Бесплатные возвраты
      </Typography>
    </section>
  );
}

export default memo(ValueProp);
