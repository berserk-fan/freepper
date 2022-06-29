import React from "react";
import { Radios, showErrorOnBlur, TextField } from "mui-rff";
import { Field } from "react-final-form";
import { DeliveryOption, DeliveryProvider } from "order-model";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import dynamic from "next/dynamic";
import Spacing from "../Commons/Spacing";
import { getDeliveryOptionName, OrderForm } from "./Definitions";
import NovayaPochtaIcon from "../Icons/NovayaPochtaIcon";

function pathName<T>(key1: keyof T) {
  return `${String(key1)}`;
}

const PhoneNumber = dynamic(() => import("../Inputs/PhoneNumber"), {
  ssr: false,
});

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
          color="secondary"
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
          color="secondary"
          id="select-devilery-option"
          label="Способ доставки"
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
