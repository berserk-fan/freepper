import { makeStyles } from "@material-ui/styles";
import theme from "../../theme";
import { StoreState } from "../../store";
import React, { memo } from "react";
import { Box, Button, Typography } from "@material-ui/core";
import CartItem from "./CartItem";
import { connect } from "react-redux";
import { CartProduct } from "../../pages/checkout";
import Link from "next/link";

export type CartState = {
  size: number;
  total: number;
  selectedProducts: Record<string, CartProduct>;
};

const useStyles = makeStyles({
  textWrapper: {
    width: "100%",
    display: "flex",
    justifyContent: "space-between",
    paddingLeft: theme.spacing(1),
    paddingTop: theme.spacing(1),
    paddingBottom: theme.spacing(1),
    [theme.breakpoints.up("sm")]: {
      width: "auto",
      marginRight: theme.spacing(1),
    },
  },
  mainButtonContainer: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
    flexGrow: 1,
    flexDirection: "column",
    borderWidth: 2,
    borderColor: theme.palette.primary.main,
    backgroundColor: theme.palette.primary.light,
    padding: theme.spacing(1),
    [theme.breakpoints.up("sm")]: {
      flexDirection: "row",
      padding: theme.spacing(3),
      flexGrow: 0,
      flexShrink: 1,
    },
  },
  mainButton: {
    width: "100%",
    flexGrow: 1,
    [theme.breakpoints.up("sm")]: {
      width: "auto",
      flexGrow: 0,
      flexShrink: 1,
    },
  },
  prePriceText: {
    [theme.breakpoints.up("sm")]: {
      display: "none",
    },
  },
});

function Cart({ cartState: { selectedProducts } }: { cartState: CartState }) {
  const classes = useStyles();
  const productsList = Object.values(selectedProducts);
  const totalPrice = productsList.reduce(
    (a, b) => a + b.count * b.price.price,
    0
  );
  return (
    <div>
      <Box marginTop={2}>
        {productsList.length === 0 ? (
          <Typography variant={"h2"}>Корзина пуста</Typography>
        ) : (
          productsList.map((product) => (
            <Box key={product.id} marginY={1}>
              <CartItem product={product} />
            </Box>
          ))
        )}
      </Box>
      <Box marginTop={2} className={`flex justify-end items-center`}>
        <div className={`rounded ${classes.mainButtonContainer}`}>
          <div className={classes.textWrapper}>
            <Typography variant={"h5"} classes={{ root: classes.prePriceText }}>
              Итого
            </Typography>
            <Typography variant="h5">{totalPrice}₴</Typography>
          </div>
          <Link href={"/checkout"}>
          <Button
            classes={{ root: classes.mainButton }}
            color={"primary"}
            variant="contained"
            size="large"
          >
            Оформить заказ
          </Button>
          </Link>
        </div>
      </Box>
    </div>
  );
}

function mapStateToProps(store: StoreState) {
  return {
    cartState: store.cartState,
  };
}

export default connect(mapStateToProps, null)(memo(Cart));
