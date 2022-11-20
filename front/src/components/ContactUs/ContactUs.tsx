import React from "react";
import PhoneIcon from "@mui/icons-material/Phone";
import ExpandLess from "@mui/icons-material/ExpandLess";
import ExpandMore from "@mui/icons-material/ExpandMore";
import TelegramIcon from "@mui/icons-material/Telegram";
import makeStyles from "@mui/styles/makeStyles";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Collapse from "@mui/material/Collapse";
import ButtonWithDetail from "../Commons/ButtonWithDetail";
import PhoneNumber from "./PhoneNumber";
import constants from "../../commons/contants";

const useStyles = makeStyles((theme) => ({
  nested: {
    paddingLeft: theme.spacing(4),
  },
}));

export default function ContactUs() {
  const classes = useStyles();
  const [telOpen, setTelOpen] = React.useState(false);
  const [telegramToggle, setTelegramToggle] = React.useState(false);
  const telToggle = () => {
    setTelOpen(!telOpen);
  };
  const telegramChat = `https://t.me/${constants.channels.telegram}`;
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
          <ListItemText primary="Telegram" />
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
                detailText={`Логин: ${constants.channels.telegram}`}
                color="secondary"
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
