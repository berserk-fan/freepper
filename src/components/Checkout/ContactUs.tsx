import {
  Box,
  Button,
  Collapse,
  Link,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography,
} from "@material-ui/core";
import React from "react";
import PhoneIcon from "@material-ui/icons/Phone";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import TelegramIcon from "@material-ui/icons/Telegram";
import { makeStyles } from "@material-ui/styles";
import theme from "../../theme";
import Detail from "../Commons/Detail";
import PhoneNumber from "../Commons/PhoneNumber";

const useStyles = makeStyles({
  nested: {
    paddingLeft: theme.spacing(4),
  },
});

export default function ContactUs() {
  const classes = useStyles();
  const [telOpen, setTelOpen] = React.useState(true);
  const telToggle = (ev) => {
    ev.preventDefault();
    setTelOpen(!telOpen);
  };
  const telegramChat = "https://t.me/pogladit_mozhno";
  return (
    <List dense component="nav" aria-label="main mailbox folders">
      <ListItem button onClick={telToggle}>
        <ListItemIcon>
          <PhoneIcon />
        </ListItemIcon>
        <ListItemText primary="Телефон" />
        {telOpen ? <ExpandLess /> : <ExpandMore />}
      </ListItem>
      <Collapse in={telOpen} timeout="auto">
        <List component="div" disablePadding>
          <PhoneNumber phone={"+380950717564"} className={classes.nested} />
          <PhoneNumber phone={"+380671111111"} className={classes.nested} />
        </List>
      </Collapse>
      <ListItem>
        <ListItemIcon>
          <TelegramIcon />
        </ListItemIcon>
        <ListItemText>
          <Typography component={"span"} display={"inline"}>
            Telegram
          </Typography>
          <Detail text={"Логин: pogladit_mozhno"} />
        </ListItemText>
      </ListItem>
      <List component="div" disablePadding>
        <ListItem className={classes.nested} dense>
          <Button
            size={"small"}
            variant={"outlined"}
            href={telegramChat}
            target={"_blank"}
            rel={"noopener"}
          >
            Начать Telegram чат
          </Button>
        </ListItem>
      </List>
    </List>
  );
}
