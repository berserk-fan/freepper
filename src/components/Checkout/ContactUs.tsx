import {
  Box,
  Collapse,
  Link,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  SvgIcon,
  Typography,
} from "@material-ui/core";
import React from "react";
import PhoneIcon from "@material-ui/icons/Phone";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import TelegramIcon from "@material-ui/icons/Telegram";
import { makeStyles } from "@material-ui/styles";
import theme from "../../theme";
import KyivStarIcon from "../Icons/KyivstarIcon";
import LifecellIcon from "../Icons/LifecellIcon";
import VodafoneIcon from "../Icons/VodafoneIcon";
import Image from "next/image";

const useStyles = makeStyles({
  nested: {
    paddingLeft: theme.spacing(4),
  },
});

export default function ContactUs() {
  const classes = useStyles();
  const [open, setOpen] = React.useState(true);
  const handleClick = () => {
    setOpen(!open);
  };
  const telegramChat = "https://t.me/pogladit_mozhno";

  return (
    <List component="nav" aria-label="main mailbox folders">
      <ListItem button onClick={handleClick}>
        <ListItemIcon>
          <PhoneIcon />
        </ListItemIcon>
        <ListItemText primary="Телефон" />
        {open ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={open} timeout="auto">
        <List component="div" disablePadding>
          <ListItem button className={classes.nested}>
            <ListItemIcon>
              <Box width={"25px"} height={"25px"}>
                <KyivStarIcon />
              </Box>
            </ListItemIcon>
            <ListItemText>
              <Link href="tel:+380671111111">+380671111111</Link>
            </ListItemText>
          </ListItem>
          <ListItem button className={classes.nested}>
            <ListItemIcon>
              <Box width={"25px"} height={"25px"}>
                <VodafoneIcon />
              </Box>
            </ListItemIcon>
            <ListItemText>
              <Link href="tel:+380661111111">+380661111111</Link>
            </ListItemText>
          </ListItem>
          <ListItem button className={classes.nested}>
            <ListItemIcon>
              <Box width={"25px"} height={"25px"}>
                <LifecellIcon />
              </Box>
            </ListItemIcon>
            <ListItemText>
              <Link href="tel:+380631111111">+380631111111</Link>
            </ListItemText>
          </ListItem>
        </List>
      </Collapse>
      <ListItem button>
        <ListItemIcon>
          <TelegramIcon />
        </ListItemIcon>
        <ListItemText onClick={() => window.open(telegramChat)}>
          <Typography component={"span"} display={"inline"}>
            Telegram
          </Typography>
          <Link href={telegramChat}>@pogladit_mozhno</Link>
        </ListItemText>
      </ListItem>
      <ListItem button>
        <ListItemIcon>
          <TelegramIcon />
        </ListItemIcon>
      </ListItem>
    </List>
  );
}
