import {
  Box, Button,
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
import KyivStarIcon from "../Icons/KyivstarIcon";
import VodafoneIcon from "../Icons/VodafoneIcon";
import ViberIcon from "../Icons/ViberIcon";

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
              <Link color={"textPrimary"} href="tel:+380671111111">
                +380
              </Link>
            </ListItemText>
          </ListItem>
          <ListItem button className={classes.nested}>
            <ListItemIcon>
              <Box width={"25px"} height={"25px"}>
                <VodafoneIcon />
              </Box>
            </ListItemIcon>
            <ListItemText>
              <Link color={"textPrimary"} href="tel:+380661111111">
                +380950717564
              </Link>
            </ListItemText>
          </ListItem>
        </List>
      </Collapse>
      <ListItem>
        <ListItemIcon>
          <TelegramIcon />
        </ListItemIcon>
        <ListItemText>
          <Typography component={"span"} display={"inline"}>
            Telegram @poglodit_mozhno
          </Typography>
        </ListItemText>
      </ListItem>
      <List component="div" disablePadding>
        <ListItem className={classes.nested}>
          <ListItemText>
            <Button color={"primary"} variant={"contained"} href={telegramChat} target={"_blank"} rel={"noopener"}>
              Начать Telegram чат
            </Button>
          </ListItemText>
        </ListItem>
      </List>
      <ListItem>
        <ListItemIcon>
          <Box width={"25px"} height={"25px"}>
            <ViberIcon />
          </Box>
        </ListItemIcon>
        <ListItemText>
          <Typography component={"span"} display={"inline"}>
            Viber @pogladit_mozhno
          </Typography>
        </ListItemText>
      </ListItem>
      <List component="div" disablePadding>
        <ListItem className={classes.nested}>
          <ListItemText>
            <Button color={"primary"} variant={"contained"} href={telegramChat} target={"_blank"} rel={"noopener"}>
              Начать Viber чат
            </Button>
          </ListItemText>
        </ListItem>
      </List>
    </List>
  );
}
