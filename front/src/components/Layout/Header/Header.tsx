import React from "react";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import makeStyles from "@mui/styles/makeStyles";
import HeaderMenu from "./HeaderMenu";
import HeaderCart from "./HeaderCart";
import HeaderLogo from "./HeaderLogo";
import HeaderActions from "./HeaderActions";
import { CustomAppBar } from "./CustomAppBar";

const useStyles = makeStyles((theme) => ({
  title: {
    marginLeft: "auto",
    [theme.breakpoints.up("md")]: {
      marginLeft: "0",
    },
  },
  toolbar: {
    position: "relative",
    display: "flex",
    justifyContent: "center",
    height: "100px",
  },
  menu: {
    [theme.breakpoints.up("md")]: {
      display: "none",
    },
  },
}));

export default function Header() {
  const classes = useStyles();
  return (
    <CustomAppBar>
      <Toolbar className={classes.toolbar}>
        <Box className={classes.menu}>
          <HeaderMenu />
        </Box>
        <HeaderLogo className={`${classes.title} cursor-default uppercase`} />
        <HeaderActions />
        <HeaderCart />
      </Toolbar>
    </CustomAppBar>
  );
}
