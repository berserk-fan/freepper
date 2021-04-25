import IconButton from "@material-ui/core/IconButton/IconButton";
import makeStyles from "@material-ui/core/styles/makeStyles";
import MenuIcon from "@material-ui/icons/Menu";
import React from "react";
import HeaderMobileSidebar from "./HeaderMobileSidebar";

const useStyles = makeStyles({
  menuButton: {
    height: 50,
  },
});

export default function HeaderMenu() {
  const classes = useStyles();
  const [drawerOpen, setDrawerTo] = React.useState(false);
  const toggleDrawer = (open) => (event) => {
    if (
      event.type === "keydown" &&
      (event.key === "Tab" || event.key === "Shift")
    ) {
      return;
    }

    setDrawerTo(open);
  };

  return (
    <>
      <IconButton
        onClick={toggleDrawer(true)}
        edge="start"
        className={classes.menuButton}
        color="inherit"
        aria-label="menu"
      >
        <MenuIcon />
      </IconButton>
      <HeaderMobileSidebar open={drawerOpen} toggle={toggleDrawer} />
    </>
  );
}
