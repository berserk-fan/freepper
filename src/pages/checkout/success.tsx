import React from "react";
import Link from "next/link";
import { pages } from "components/Layout/Header/pages";
import Box from "@material-ui/core/Box/Box";
import Container from "@material-ui/core/Container";
import Typography from "@material-ui/core/Typography/Typography";
import Button from "@material-ui/core/Button/Button";
import CheckoutHeader from "../../components/Layout/Header/CheckoutHeader";

export default function Success() {
  return (
    <Box aria-label="checkout-success-page">
      <CheckoutHeader />
      <Container maxWidth="sm" component="main">
        <Box mt="-100px" height="100vh" className="center flex-col">
          <Typography gutterBottom variant="h4">
            Заказ успешно отправлен.
          </Typography>
          <Link href={pages.home.path}>
            <a>
              <Button variant="contained" size="large" color="secondary">
                На главную
              </Button>
            </a>
          </Link>
        </Box>
      </Container>
    </Box>
  );
}
