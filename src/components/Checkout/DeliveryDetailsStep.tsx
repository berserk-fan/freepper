import React from "react";
import { Radios, showErrorOnBlur, TextField } from "mui-rff";
import { Field } from "react-final-form";
import { DeliveryOption, DeliveryProvider } from "order-model";
import Typography from "@material-ui/core/Typography/Typography";
import Box from "@material-ui/core/Box/Box";
import { PhoneNumber } from "../Inputs/PhoneNumber";
import Spacing from "../Commons/Spacing";
import { getDeliveryOptionName, OrderForm } from "./Definitions";
import NovayaPochtaIcon from "../Icons/NovayaPochtaIcon";

function pathName<T>(key1: keyof T) {
  return `${key1}`;
}

export default function DeliveryDetailsForm() {
  const deliveryOptions = [DeliveryOption.TO_WAREHOUSE];
  return (
    <Box maxWidth="md" className="flex flex-col">
      <Typography variant="h4" align="center" gutterBottom>
        Данные для доставки
      </Typography>
      <Spacing spacing={1} childClassName="w-full">
        <TextField
          color="secondary"
          name={pathName<OrderForm>("name")}
          required
          fullWidth
          id="full-name-input"
          label="Фамилия и Имя"
          variant="filled"
          type="text"
          autoComplete="name"
          showError={showErrorOnBlur}
        />
        <Field
          id="phone-input"
          name={pathName<OrderForm>("phone")}
          placeholder="Номер телефона"
          component={PhoneNumber}
        />
        <Radios
          label="Служба доставки"
          name={pathName<OrderForm>("deliveryProvider")}
          required
          data={[
            {
              label: <NovayaPochtaIcon />,
              value: DeliveryProvider.NOVAYA_POCHTA,
            },
          ]}
        />
        <Radios
          required
          name={pathName<OrderForm>("deliveryOption")}
          id="select-devilery-option"
          label="Способ доставки"
          color="secondary"
          data={deliveryOptions.map((option) => ({
            label: <Typography>{getDeliveryOptionName(option)}</Typography>,
            value: option,
          }))}
        />

        <TextField
          color="secondary"
          name={pathName<OrderForm>("city")}
          required
          fullWidth
          id="address-input"
          label="Город"
          variant="filled"
          type="text"
          autoComplete="city"
        />
        <TextField
          color="secondary"
          name={pathName<OrderForm>("warehouseNumber")}
          required
          fullWidth
          id="warehouse-number-input"
          label="Номер отделения"
          variant="filled"
          type="number"
          autoComplete="warehouseNumber"
        />
      </Spacing>
    </Box>
  );
}
