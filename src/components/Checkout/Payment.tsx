import Typography from "@material-ui/core/Typography/Typography";
import Box from "@material-ui/core/Box/Box";
import React from "react";

export default function PaymentStep() {
  return (
    <Box maxWidth="md" className="flex flex-col" paddingBottom={1}>
      <Typography align="center" variant="h3">
        Оплата
      </Typography>
      <Typography align="center" variant="subtitle1">
        Сейчас только оплата при получении
      </Typography>
    </Box>
  );
}
