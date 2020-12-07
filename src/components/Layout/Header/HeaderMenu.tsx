import {
  Box,
  Drawer,
  Fade,
  fade,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";
import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import theme from "../../../theme";
import Link from "next/link";
import { useRouter } from "next/router";
import HomeIcon from "@material-ui/icons/Home";
import StorefrontIcon from "@material-ui/icons/Storefront";
import InfoOutlinedIcon from "@material-ui/icons/InfoOutlined";
import CloseIcon from "@material-ui/icons/Close";

const useStyles = makeStyles({
  list: {
    width: 250,
  },
  fullList: {
    width: "auto",
  },
  menuButton: {
    height: 50,
  },
  drawer: {
    width: "70vw",
    maxWidth: "300px",
    overflow: "visible",
  },
  closeMenuButton: {
    position: "absolute",
    top: 5,
    right: -65,
    background: theme.palette.background.paper,
    borderRadius: "50%",
  },
});

export default function HeaderMenu() {
  const classes = useStyles();
  const router = useRouter();

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

  const pages: [string, string, React.ReactNode][] = [
    ["/", "Домой", <HomeIcon />],
    ["/shop", "Магазин", <StorefrontIcon />],
    ["/about", "О наc", <InfoOutlinedIcon />],
  ];
  const sideBarOpenTime = 250;

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
      <Drawer
        className={"relative"}
        classes={{ paper: classes.drawer }}
        open={drawerOpen}
        onClose={toggleDrawer(false)}
        transitionDuration={sideBarOpenTime}
      >
        <Fade
          in={drawerOpen}
          style={{
            transitionDelay: drawerOpen ? `${sideBarOpenTime / 2}ms` : `0ms`,
          }}
        >
          <Box
            component={"span"}
            className={classes.closeMenuButton}
            onClick={toggleDrawer(false)}
          >
            <IconButton>
              <CloseIcon fontSize={"large"} />
            </IconButton>
          </Box>
        </Fade>
        <List component="nav" aria-label="home shop about">
          {pages.map(([path, name, icon]) => {
            return (
              <Link key={name + path} href={path}>
                <ListItem button selected={router.pathname === path}>
                  <ListItemIcon>{icon}</ListItemIcon>
                  <ListItemText primary={name}>{name}</ListItemText>
                </ListItem>
              </Link>
            );
          })}
        </List>
      </Drawer>
    </>
  );
}
