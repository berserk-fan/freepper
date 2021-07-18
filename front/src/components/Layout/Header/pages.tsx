import { OverridableComponent } from "@material-ui/core/OverridableComponent";
import { SvgIconTypeMap } from "@material-ui/core/SvgIcon/SvgIcon";
import ShopIcon from "../../Icons/ShopIcon";
import HouseIcon from "../../Icons/HouseIcon";
import PetBedIcon from "../../Icons/PetBedIcon";
import Collar from "../../Icons/Collar";
import GroupIcon from "../../Icons/GroupIcon";

type Pages =
  | "home"
  | "about"
  | "delivery-and-payment-info"
  | "returns-policy"
  | "cooperation"
  | "public-offer"
  | "privacy-policy"
  | "attributions"
  | "ammo"
  | "beds"
  | "checkout"
  | "checkout-success";

type PageGroup = {
  id: string;
  name: string;
  icon?: OverridableComponent<SvgIconTypeMap>;
  children: Page[];
};

export type Page = {
  id: string;
  name: string;
  path: string;
  Icon?: OverridableComponent<SvgIconTypeMap>;
};
export const pages: Record<Pages, Page> = {
  home: { id: "home", path: "/", name: "Домой", Icon: HouseIcon },
  beds: {
    id: "beds",
    path: "/categories/beds/products",
    name: "Лежанки",
    Icon: PetBedIcon,
  },
  ammo: {
    id: "ammo",
    path: "/categories/ammo/products",
    name: "Аммуниция",
    Icon: Collar,
  },
  about: {
    id: "about",
    path: "/about",
    name: "О наc",
    Icon: GroupIcon,
  },
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
  "privacy-policy": {
    id: "privacy-policy",
    path: "/privacy-policy",
    name: "Политика конфеденциальности",
  },
  attributions: {
    id: "attributions",
    path: "/attributions",
    name: "Атрибуции",
  },
  checkout: {
    id: "checkout",
    path: "/checkout",
    name: "Оформелние заказа",
  },
  "checkout-success": {
    id: "checkout-success",
    path: "/checkout/success",
    name: "Заказ успешен",
  },
};

export const shopPageGroup: PageGroup = {
  id: "shop-page-group",
  name: "Магазин",
  icon: ShopIcon,
  children: [pages.beds, pages.ammo],
};

type ModelPages =
  | "lukoshko-s"
  | "lukoshko-m"
  | "chemodan"
  | "kvadro-soft"
  | "kvadro-strong";

export const modelPages: Record<ModelPages, Page> = {
  "kvadro-soft": {
    id: "kvadro-soft",
    path: "#",
    name: "Квадро Софт",
  },
  "kvadro-strong": {
    id: "kvadro-strong",
    path: "#",
    name: "Квадро Стронг",
  },
  chemodan: {
    id: "chemodan",
    path: "#",
    name: "Чемодан",
  },
  "lukoshko-s": {
    id: "lukoshko-s",
    path: "#",
    name: "Лукошко Эйфель",
  },
  "lukoshko-m": {
    id: "lukoshko-m",
    path: "#",
    name: "Лукошко",
  },
};
