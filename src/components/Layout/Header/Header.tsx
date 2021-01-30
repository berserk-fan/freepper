import React, {FunctionComponent} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Toolbar from "@material-ui/core/Toolbar";
import { Box } from "@material-ui/core";
import theme from "../../../theme";
import HeaderMenu from "./HeaderMenu";
import HeaderCart from "./HeaderCart";
import HeaderLogo from "./HeaderLogo";
import HeaderActions from "./HeaderActions";
import { CustomAppBar } from "./CustomAppBar";
import InfoOutlinedIcon from "@material-ui/icons/InfoOutlined";
import PetBedIcon from "../../Icons/PetBedIcon";
import Collar from "../../Icons/Collar";
import HouseIcon from "../../Icons/HouseIcon";
import ShopIcon from "../../Icons/ShopIcon";
import {OverridableComponent} from "@material-ui/core/OverridableComponent";
import {SvgIconTypeMap} from "@material-ui/core/SvgIcon/SvgIcon";

const useStyles = makeStyles({
  title: {
    marginLeft: "auto",
    [theme.breakpoints.up("md")]: {
      marginLeft: "0",
    },
  },
  mainButtonGroup: {
    position: "absolute",
    width: 540,
    marginLeft: theme.spacing(2),
    marginRight: "auto",
    left: 120,
    right: 0,
    textAlign: "center",
    justifyContent: "center",
    display: "none",
    [theme.breakpoints.up("md")]: {
      marginLeft: "auto",
      display: "flex",
      alignItems: "stretch"
    },
  },
  toolbar: {
    position: "relative",
    display: "flex",
    justifyContent: "center",
  },
  menu: {
    [theme.breakpoints.up("md")]: {
      display: "none",
    },
  },
});

type Pages = "home" | "about";
type SupportPages =
  | "delivery-and-payment-info"
  | "returns-policy"
  | "cooperation"
  | "public-offer";

type PageGroup = {
  id: string;
  name: string;
  icon?: OverridableComponent<SvgIconTypeMap>;
  children: Page[]
}

export const shopPageGroup: PageGroup = {
  id: 'shop-page-group',
  name: 'Магазин',
  icon: ShopIcon,
  children: [
    {
      id: "beds",
      path: "/categories/beds/products",
      name: "Лежанки",
      icon: PetBedIcon,
    },
    {
      id: "ammo",
      path: "/categories/beds/products",
      name: "Аммуниция",
      icon: Collar
    }
  ]
};

export type Page = {
  id: string;
  name: string;
  path: string;
  icon?: OverridableComponent<SvgIconTypeMap>;
};

export const pages: Record<Pages, Page> = {
  home: { id: "home", path: "/", name: "Домой", icon: HouseIcon },
  about: {
    id: "about",
    path: "/about",
    name: "О наc",
    icon: InfoOutlinedIcon,
  },
};

export const supportPages: Record<SupportPages, Page> = {
  "delivery-and-payment-info": {
    id: "delivery-and-payment-info",
    path: "/delivery-and-payment-info",
    name: "Доставка и оплата",
  },
  "returns-policy": {
    id: "returns-policy",
    path: "/returns-policy",
    name: "Обмен и возврат",
  },
  cooperation: {
    id: "cooperation",
    path: "/cooperation",
    name: "Сотрудничество",
  },
  "public-offer": {
    id: "public-offer",
    path: "/public-offer",
    name: "Публичная офферта",
  },
};

export default function Header() {
  const classes = useStyles();
  return (
    <CustomAppBar>
      <Toolbar className={classes.toolbar}>
        <Box className={classes.menu}>
          <HeaderMenu />
        </Box>
        <Box className={`${classes.title} cursor-default uppercase`}>
          <HeaderLogo />
        </Box>
        <HeaderActions className={`${classes.mainButtonGroup}`} />
        <HeaderCart />
      </Toolbar>
    </CustomAppBar>
  );
}
