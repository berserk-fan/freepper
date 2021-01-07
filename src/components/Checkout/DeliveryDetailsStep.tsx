import {
  Box,
  Collapse,
  Container,
  MenuItem,
  SvgIcon,
  Typography,
} from "@material-ui/core";
import React from "react";
import { Radios, Select, TextField } from "mui-rff";
import { DeliveryOption, DeliveryProvider, Order } from "../../order-model";
import { PhoneNumber } from "../Inputs/PhoneNumber";
import { pathName, pathName1 } from "../../utils";
import { Field } from "react-final-form";
import Address from "../Inputs/Address";
import { OrderForm } from "./CheckoutForm";
import NovayaPochtaIcon from "./Nova_Poshta_2014_logo.svg";
import { makeStyles } from "@material-ui/styles";

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
    <Box maxWidth={"md"} className={"flex flex-col gap-4"}>
      <Typography variant={"h3"} align={"center"}>
        Данные для доставки
      </Typography>
      <TextField
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
        required={true}
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
      <Select
        required
        name={pathName1({} as OrderForm, "deliveryOption")}
        fullWidth
        labelId="select-devilery-option-label"
        id="select-devilery-option"
        label="Способ доставки"
        variant="filled"
      >
        {deliveryOptions.map((option) => (
          <MenuItem key={option} value={option}>
            {getDeliveryOptionName(option)}
          </MenuItem>
        ))}
      </Select>
      <Collapse in={!!orderForm?.deliveryOption}>
        <Field
          name={pathName1({} as OrderForm, "address")}
          render={(props) => (
            <Address
              required
              label={getAddressLabel(orderForm?.deliveryOption)}
              {...props}
            />
          )}
        />
      </Collapse>
    </Box>
  );
}
