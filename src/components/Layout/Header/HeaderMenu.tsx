import { IconButton } from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";
import React  from "react";
import { makeStyles } from "@material-ui/core/styles";
import HeaderMobileSidebar from "./HeaderMobileSidebar";

const useStyles = makeStyles({
  menuButton: {
    height: 50,
  },
});

export default function HeaderMenu() {
  const classes = useStyles();
  const toggleDrawer = (open) => (event) => {
    if (
        event.type === "keydown" &&
        (event.key === "Tab" || event.key === "Shift")
    ) {
      return;
    }

    setDrawerTo(open);
  };
  const [drawerOpen, setDrawerTo] = React.useState(false);

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
      {/* TODO: code split */}
      <HeaderMobileSidebar open={drawerOpen} toggle={toggleDrawer}/>
    </>
  );
}
