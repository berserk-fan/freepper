import {
  Box,
  Collapse,
  Container,
  MenuItem,
  SvgIcon,
  Typography,
} from "@material-ui/core";
import React, { useEffect } from "react";
import { Radios, Select, TextField } from "mui-rff";
import { DeliveryOption, DeliveryProvider, Order } from "../../order-model";
import { PhoneNumber } from "../Inputs/PhoneNumber";
import { pathName } from "../../utils";
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
      return "Упс. Мы уточним ее по телефону";
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

export default function DeliveryDetailsForm({ order }: { order: OrderForm }) {
  const classes = useStyles();
  const deliveryOptions = [DeliveryOption.TO_WAREHOUSE, DeliveryOption.COURIER];
  useEffect(() => {}, [order]);

  return (
    <Container>
      <Box maxWidth={"md"} className={"flex flex-col gap-4"}>
        <Typography variant={"h6"}>Введите информацию о заказе</Typography>
        <TextField
          name={pathName({} as Order, "deliveryDetails", "fullName")}
          required
          fullWidth
          id="full-name-input"
          label="Полное имя"
          variant="filled"
          type="text"
        />
        <Field
          id={"phone-input"}
          name={pathName({} as Order, "deliveryDetails", "phone")}
          placeholder={"Номер телефона"}
          component={PhoneNumber}
        />
        <Radios
          label="Служба доставки"
          name={pathName({} as Order, "deliveryDetails", "provider")}
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
          name={pathName({} as Order, "deliveryDetails", "option")}
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
        <Collapse in={!!order?.deliveryDetails?.option}>
          <Address
            required={true}
            label={getAddressLabel(order?.deliveryDetails?.option)}
            name={pathName({} as Order, "deliveryDetails", "address")}
          />
        </Collapse>
      </Box>
    </Container>
  );
}
