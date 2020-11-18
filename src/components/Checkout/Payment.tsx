import { Box, Divider, Typography } from "@material-ui/core";
import React from "react";
import { Radios, TextField } from "mui-rff";
import { pathName1 } from "../../utils";
import { PaymentOption } from "../../order-model";
import { OrderForm } from "./CheckoutForm";

function paymentOptionToLabel(option: PaymentOption) {
  switch (option) {
    case PaymentOption.COD:
      return <Typography variant={"h6"}>Наложенный платеж</Typography>;
    default:
      return "Неизвестно";
  }
}

export default function PaymentStep() {
  return (
    <Box maxWidth={"md"} className={"flex flex-col"} paddingBottom={1}>
      <Typography align="center" variant={"h3"}>
        Оплата
      </Typography>
      <Typography align="center" variant={"subtitle1"}>
        Сейчас только оплата при получении
      </Typography>
    </Box>
  );
}
