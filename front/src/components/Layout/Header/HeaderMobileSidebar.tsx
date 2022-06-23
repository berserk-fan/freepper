import { useRouter } from "next/router";
import React, { useState } from "react";
import CloseIcon from "@mui/icons-material/Close";
import ExpandLess from "@mui/icons-material/ExpandLess";
import ExpandMore from "@mui/icons-material/ExpandMore";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Drawer from "@mui/material/Drawer";
import Fade from "@mui/material/Fade";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import List from "@mui/material/List";
import Collapse from "@mui/material/Collapse";
import Link from "next/link";
import makeStyles from "@mui/material/styles/makeStyles";
import { Page, pages, shopPageGroup } from "./pages";
import ContactUsSnackBar from "../../ContactUs/ContactUsSnackBar";
import ContactsIcon from "../../Icons/ContactsIcon";

const useStyles = makeStyles((theme) => ({
  list: {
    width: 250,
  },
  fullList: {
    width: "auto",
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
}));

export default function HeaderMobileSidebar({ open, toggle }) {
  const classes = useStyles();
  const router = useRouter();
  const [curPath] = router.asPath.split("?");
  const [shopGroupOpen, setShopGroupOpen] = React.useState(true);
  const toggleShopGroupOpen = () => {
    setShopGroupOpen(!shopGroupOpen);
  };

  const handleListClick = (path) => (event) => {
    if (path === curPath) {
      toggle(false)(event);
    }
  };
  const [contactsOpen, setContactsOpen] = useState(false);
  const sideBarOpenTime = 250;

  function pageRepresentation(
    page: Page,
    className: string = "",
    fontSizeOverride?: string,
  ): React.ReactNode {
    const { name, path, Icon } = page;
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
            <Icon fontSize="large" style={styleProp} />
          </ListItemIcon>
          <ListItemText primary={name}>{name}</ListItemText>
        </ListItem>
      </Link>
    );
  }
  return (
    <Drawer
      className="relative"
      classes={{ paper: classes.drawer }}
      open={open}
      onClose={toggle(false)}
      transitionDuration={sideBarOpenTime}
    >
      <Fade
        in={open}
        style={{
          transitionDelay: open ? `${sideBarOpenTime / 2}ms` : "0ms",
        }}
      >
        <Box
          component="span"
          className={classes.closeMenuButton}
          onClick={toggle(false)}
        >
          <IconButton>
            <CloseIcon fontSize="large" />
          </IconButton>
        </Box>
      </Fade>
      <Box
        height="100vh"
        className="flex flex-col justify-between overflow-y-scroll"
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
                pageRepresentation(page, classes.nestedList, "28px"),
              )}
            </List>
          </Collapse>
        </List>
        {pageRepresentation(pages.about)}
        <Box marginTop="auto" aria-label="contact-us-form">
          <ListItem button onClick={() => setContactsOpen(true)}>
            <ListItemIcon>
              <ContactsIcon fontSize="large" />
            </ListItemIcon>
            <ListItemText>Контакты</ListItemText>
          </ListItem>
        </Box>
      </Box>
      <ContactUsSnackBar
        open={contactsOpen}
        close={() => setContactsOpen(false)}
      />
    </Drawer>
  );
}
