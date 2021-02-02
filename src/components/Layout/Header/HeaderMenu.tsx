import {
  Box,
  Collapse,
  Drawer,
  Fade,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";
import React, {useState} from "react";
import { makeStyles } from "@material-ui/core/styles";
import theme from "../../../theme";
import Link from "next/link";
import { useRouter } from "next/router";
import CloseIcon from "@material-ui/icons/Close";
import { Page, pages, shopPageGroup } from "./Header";
import ContactUs from "../../Checkout/ContactUs";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import ContactUsSnackBar from "../../Commons/ContactUsSnackBar";
import ContactsIcon from '../../Icons/ContactsIcon';

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
  nestedList: {
    paddingLeft: theme.spacing(4),
  },
});

export default function HeaderMenu() {
  const classes = useStyles();
  const router = useRouter();
  const [curPath, _] = router.asPath.split("?");
  const [drawerOpen, setDrawerTo] = React.useState(false);
  const [shopGroupOpen, setShopGroupOpen] = React.useState(true);
  const toggleShopGroupOpen = () => {
    setShopGroupOpen(!shopGroupOpen);
  };
  const toggleDrawer = (open) => (event) => {
    if (
      event.type === "keydown" &&
      (event.key === "Tab" || event.key === "Shift")
    ) {
      return;
    }

    setDrawerTo(open);
  };
  const handleListClick = (path) => (event) => {
    if (path == curPath) {
      toggleDrawer(false)(event);
    }
  };
  const [contactsOpen, setContactsOpen] = useState(false);

  const sideBarOpenTime = 250;
  function pageRepresentation(
    page: Page,
    className: string = "",
    fontSizeOverride?: string
  ): React.ReactNode {
    const { name, path, icon } = page;
    const styleProp = fontSizeOverride ? { fontSize: fontSizeOverride } : {};
    return (
      <Link key={name + path} href={path}>
        <ListItem
          className={className}
          button
          selected={curPath === path}
          onClick={handleListClick(path)}
        >
          <ListItemIcon>
            {React.createElement(icon, { fontSize: "large", style: styleProp })}
          </ListItemIcon>
          <ListItemText primary={name}>{name}</ListItemText>
        </ListItem>
      </Link>
    );
  }

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
        <Box
          height={"100vh"}
          className={"flex flex-col justify-between overflow-y-scroll"}
          pb={5}
        >
          <List component="nav" aria-label="shop navigation">
            {pageRepresentation(pages.home)}
            <ListItem button onClick={toggleShopGroupOpen}>
              <ListItemIcon>
                {React.createElement(shopPageGroup.icon)}
              </ListItemIcon>
              <ListItemText primary={shopPageGroup.name}>
                {shopPageGroup.name}
              </ListItemText>
              {shopGroupOpen ? <ExpandLess /> : <ExpandMore />}
            </ListItem>
            <Collapse in={shopGroupOpen} timeout="auto">
              <List component="div" disablePadding>
                {shopPageGroup.children.map((page) =>
                  pageRepresentation(page, classes.nestedList, "28px")
                )}
              </List>
            </Collapse>
          </List>
          {pageRepresentation(pages.about)}
          <Box marginTop={"auto"} aria-label={"contact-us-form"}>
            <ListItem
                button
                onClick={() => setContactsOpen(true)}
            >
              <ListItemIcon>
                <ContactsIcon fontSize={"large"}/>
              </ListItemIcon>
              <ListItemText>Контакты</ListItemText>
            </ListItem>
          </Box>
        </Box>
        <ContactUsSnackBar open={contactsOpen} close={() => setContactsOpen(false)}/>
      </Drawer>
    </>
  );
}
