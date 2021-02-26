import {
  Box,
  Button,
  Collapse,
  Link,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Theme,
  Typography,
} from "@material-ui/core";
import React from "react";
import PhoneIcon from "@material-ui/icons/Phone";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import TelegramIcon from "@material-ui/icons/Telegram";
import { makeStyles } from "@material-ui/styles";
import PhoneNumber from "./PhoneNumber";
import ButtonWithDetail from "../Commons/ButtonWithDetail";

const useStyles = makeStyles((theme: Theme) => ({
  nested: {
    paddingLeft: theme.spacing(4),
  },
}));

export default function ContactUs() {
  const classes = useStyles();
  const [telOpen, setTelOpen] = React.useState(false);
  const [telegramToggle, setTelegramToggle] = React.useState(false);
  const telToggle = (ev) => {
    setTelOpen(!telOpen);
  };
  const telegramChat = "https://t.me/pogladit_mozhno";
  return (
    <Box>
      <Typography variant="caption">Нажмите на интересующий вариант</Typography>
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
            <PhoneNumber phone="+380950717564" className={classes.nested} />
            <PhoneNumber phone="+380671111111" className={classes.nested} />
          </List>
        </Collapse>
        <ListItem button onClick={() => setTelegramToggle((prev) => !prev)}>
          <ListItemIcon>
            <TelegramIcon />
          </ListItemIcon>
          <ListItemText>
            <Typography component="span" display="inline">
              Telegram
            </Typography>
          </ListItemText>
          {telegramToggle ? <ExpandLess /> : <ExpandMore />}
        </ListItem>
        <Collapse in={telegramToggle}>
          <List component="div" disablePadding>
            <ListItem className={classes.nested} dense>
              <ButtonWithDetail
                size="small"
                href={telegramChat}
                target="_blank"
                rel="noopener"
                detailText="Логин: pogladit_mozhno"
              >
                Начать Telegram чат
              </ButtonWithDetail>
            </ListItem>
          </List>
        </Collapse>
      </List>
    </Box>
  );
}
