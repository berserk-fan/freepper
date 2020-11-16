import React, { memo } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import Divider from "@material-ui/core/Divider";
import { Box, Typography, useMediaQuery } from "@material-ui/core";
import { CartProduct } from "../../pages/checkout";
import theme from "../../theme";
import { OrderForm } from "./CheckoutForm";
import {
  getDeliveryOptionName,
  getDeliveryProviderName,
} from "./DeliveryDetailsStep";
import Image from "next/image";
import { StoreState } from "../../store";
import { CartState } from "../Cart/Cart";
import { connect } from "react-redux";

function subtotal(cartProducts: CartProduct[]) {
  return cartProducts.reduce(
    (sum, cartProduct) => sum + cartProduct.price.price * cartProduct.count,
    0
  );
}

type Column<T> = {
  name: string;
  extractor: (product: T) => string;
};

const strcmp = (a, b) => (a < b ? -1 : a > b ? 1 : 0);

const formSummaryColumns: Column<OrderForm>[] = [
  {
    name: "Имя",
    extractor: (t: OrderForm) => t?.name || "Отсутствует",
  },
  {
    name: "Телефон",
    extractor: (t: OrderForm) => t?.phone || "Отсутствует",
  },
  {
    name: "Способ доставки",
    extractor: (t: OrderForm) =>
      getDeliveryOptionName(t?.deliveryOption) || "Отсутствует",
  },
  {
    name: "Служба доставки",
    extractor: (t: OrderForm) =>
      getDeliveryProviderName(t?.deliveryProvider) || "Отсутствует",
  },
  {
    name: "Адрес",
    extractor: (t: OrderForm) => {
      const address = t?.address;
      if (!address) {
        return "Отсутствует";
      }
      if (address.match(/[0-9]+/)) {
        return `Отделение номер ${address}`;
      } else {
        return address;
      }
    },
  },
];

function FormSummaryTable({ orderForm }: { orderForm: OrderForm }) {
  return (
    <TableContainer component={Paper}>
      <Table size={"small"}>
        <TableHead>
          <TableRow>
            <TableCell colSpan={4}>
              <Typography variant={"h4"}>Доставка</Typography>
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {formSummaryColumns.map((col) => (
            <TableRow key={col.name}>
              <TableCell
                style={{
                  paddingLeft: theme.spacing(1),
                  paddingRight: theme.spacing(0.5),
                }}
                colSpan={1}
              >
                {col.name}
              </TableCell>
              <TableCell style={{ paddingLeft: theme.spacing(1) }} colSpan={3}>
                {col.extractor(orderForm)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

function Summary({
  cartState,
  orderForm,
}: {
  cartState: CartState;
  orderForm: OrderForm;
}) {
  const cartProducts = Object.values(cartState.selectedProducts);
  const invoiceShipping = 0;
  const invoiceSubtotal = cartState.total;
  const invoiceTotal = invoiceShipping + invoiceSubtotal;
  const columns: Column<CartProduct>[] = [
    {
      name: "Цена",
      extractor: (p: CartProduct) => p.price.price.toString() + " ₴",
    },
    {
      name: "Количество",
      extractor: (p: CartProduct) => p.count.toString(),
    },
    {
      name: "Сумма",
      extractor: (p: CartProduct) =>
        (p.count * p.price.price).toString() + " ₴",
    },
  ];

  const fullWidth = useMediaQuery(theme.breakpoints.up("sm"));
  return (
    <>
      <Typography align="center" variant={"h3"}>
        Проверьте заказ
      </Typography>
      <Paper>
        <Box marginTop={1}>
          <Box className={"flex flex-col"}>
            <Box
              paddingX={2}
              paddingY={1}
              className={"flex justify-between items-center"}
            >
              <Typography variant={"h4"}>Корзина</Typography>
              <Typography variant={"h5"}>
                <Typography variant={"body1"} display="inline">
                  на сумму:{" "}
                </Typography>
                {invoiceTotal} ₴
              </Typography>
            </Box>
            <Divider />
            {cartProducts.map((product) => (
              <>
                <Box
                  margin={1}
                  className={
                    "flex gap-4 " +
                    (fullWidth ? "flex-row justify-between" : "flex-col")
                  }
                >
                  <Box
                    width={fullWidth ? "60%" : "auto"}
                    className={"flex justify-start items-center"}
                  >
                    <Image
                      className={"rounded"}
                      width={72}
                      height={72}
                      src={product.image.src}
                      alt={product.image.alt}
                    />
                    <Box paddingLeft={1}>
                      <Typography variant={"h6"}>
                        {product.displayName}
                      </Typography>
                    </Box>
                  </Box>
                  <Box
                    width={fullWidth ? "60%" : "auto"}
                    className={"flex flex-row no-wrap gap-12 justify-center"}
                  >
                    {columns.map((col) => (
                      <div className={"flex flex-col justify-center"}>
                        <div>
                          <Typography
                            color={"textSecondary"}
                            variant={"caption"}
                          >
                            {col.name}
                          </Typography>
                        </div>
                        <div>
                          <Typography align={"center"}>
                            {col.extractor(product)}
                          </Typography>
                        </div>
                      </div>
                    ))}
                  </Box>
                </Box>
                <Divider />
              </>
            ))}
          </Box>
        </Box>
      </Paper>
      <Box marginTop={1}>
        <FormSummaryTable orderForm={orderForm} />
      </Box>
    </>
  );
}
function mapStateToProps(state: StoreState) {
  return {
    cartState: state.cartState,
  };
}

export default connect(mapStateToProps, null)(memo(Summary));
