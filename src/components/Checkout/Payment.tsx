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
  const paymentOptions = [PaymentOption.COD];
  return (
    <Box maxWidth={"md"} className={"flex flex-col gap-4"} paddingBottom={1}>
      <Typography align="center" variant={"h3"}>
        Оплата
        <Typography variant={"subtitle1"}>
          Сейчас только наложенный платеж😭
        </Typography>
      </Typography>
      <Radios
        required
        label={"Способ оплаты"}
        name={pathName1({} as OrderForm, "paymentOption")}
        data={paymentOptions.map((o) => ({
          label: paymentOptionToLabel(o),
          value: o,
        }))}
      />
    </Box>
  );
}
