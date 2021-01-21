import {
  Box,
  Collapse,
  Container,
  Grid,
  MenuItem,
  SvgIcon,
  Typography,
} from "@material-ui/core";
import React from "react";
import { Radios, Select, TextField } from "mui-rff";
import { DeliveryOption, DeliveryProvider, Order } from "../../order-model";
import { PhoneNumber } from "../Inputs/PhoneNumber";
import { pathName1 } from "../../utils";
import { Field } from "react-final-form";
import { OrderForm } from "./CheckoutForm";
import NovayaPochtaIcon from "./Nova_Poshta_2014_logo.svg";
import { makeStyles } from "@material-ui/styles";
import Spacing from "../Commons/Spacing";

export function getDeliveryOptionName(option: DeliveryOption) {
  switch (option) {
    case DeliveryOption.COURIER:
      return "Курьер";
    case DeliveryOption.TO_WAREHOUSE:
      return "В отделение";
    default:
      return "Недоступно";
  }
}

export function getDeliveryProviderName(provider: DeliveryProvider) {
  switch (provider) {
    case DeliveryProvider.NOVAYA_POCHTA:
      return "Новая почта";
    default:
      return "Неизвестно";
  }
}

function getAddressLabel(deliveryOption: DeliveryOption) {
  switch (deliveryOption) {
    case DeliveryOption.COURIER:
      return "Адрес для курьера";
    case DeliveryOption.TO_WAREHOUSE:
      return "Адрес или номер отделения";
    default:
      return "Адрес для выбраного способа доставки";
  }
}

const useStyles = makeStyles({
  largeIcon: {
    width: "4.2rem",
    height: "1.5rem",
  },
});

export default function DeliveryDetailsForm({
  orderForm,
}: {
  orderForm: OrderForm;
}) {
  const classes = useStyles();
  const deliveryOptions = [DeliveryOption.TO_WAREHOUSE];

  return (
    <Box maxWidth={"md"} className={"flex flex-col"}>
      <Typography variant={"h3"} align={"center"} gutterBottom>
        Данные для доставки
      </Typography>
      <Spacing spacing={1} childClassName={"w-full"}>
        <TextField
          color={"secondary"}
          name={pathName1({} as OrderForm, "name")}
          required
          fullWidth
          id="full-name-input"
          label="Полное имя"
          variant="filled"
          type="text"
          autoComplete={"name"}
        />
        <Field
          id={"phone-input"}
          name={pathName1({} as OrderForm, "phone")}
          placeholder={"Номер телефона"}
          component={PhoneNumber}
        />
        <Radios
          label="Служба доставки"
          name={pathName1({} as OrderForm, "deliveryProvider")}
          required
          data={[
            {
              label: (
                <SvgIcon className={classes.largeIcon} viewBox={"0 0 210 75"}>
                  <NovayaPochtaIcon />
                </SvgIcon>
              ),
              value: DeliveryProvider.NOVAYA_POCHTA,
            },
          ]}
        />
        <Radios
          required
          name={pathName1({} as OrderForm, "deliveryOption")}
          id="select-devilery-option"
          label="Способ доставки"
          color={"secondary"}
          data={deliveryOptions.map((option) => ({
            label: <Typography>{getDeliveryOptionName(option)}</Typography>,
            value: option,
          }))}
        />

        <TextField
          color={"secondary"}
          name={pathName1({} as OrderForm, "city")}
          required
          fullWidth
          id="address-input"
          label="Город"
          variant="filled"
          type="text"
          autoComplete={"city"}
        />
        <TextField
          color={"secondary"}
          name={pathName1({} as OrderForm, "warehouseNumber")}
          required
          fullWidth
          id="warehouse-number-input"
          label="Номер отделения"
          variant="filled"
          type="number"
          autoComplete={"warehouseNumber"}
        />
      </Spacing>
    </Box>
  );
}
