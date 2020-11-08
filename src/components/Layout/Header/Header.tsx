import React, { useEffect, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Button from "@material-ui/core/Button";
import Link from "next/link";
import { Box } from "@material-ui/core";
import theme from "../../../theme";
import HeaderMenu from "./HeaderMenu";
import HeaderCart from "./HeaderCart";
import HeaderLogo from "./HeaderLogo";
import HeaderActions from "./HeaderActions";
import { CustomAppBar } from "./CustomAppBar";

const useStyles = makeStyles({
  title: {
    marginLeft: "auto",
    [theme.breakpoints.up("md")]: {
      marginLeft: "0",
    },
  },
  mainButtonGroup: {
    position: "absolute",
    width: 220,
    marginLeft: theme.spacing(2),
    marginRight: "auto",
    left: 0,
    right: 0,
    textAlign: "center",
    justifyContent: "center",
    display: "none",
    [theme.breakpoints.up("md")]: {
      marginLeft: "auto",
      display: "flex",
    },
  },
  toolbar: {
    position: "relative",
    display: "flex",
    justifyContent: "center",
  },
});

export default function Header() {
  const classes = useStyles();
  return (
    <CustomAppBar>
      <Toolbar className={classes.toolbar}>
        <HeaderMenu />
        <Box className={`${classes.title} cursor-default uppercase`}>
          <HeaderLogo />
        </Box>
        <HeaderActions className={`${classes.mainButtonGroup}`} />
        <HeaderCart />
      </Toolbar>
    </CustomAppBar>
  );
}
