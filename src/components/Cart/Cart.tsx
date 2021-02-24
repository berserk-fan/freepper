import { makeStyles } from "@material-ui/styles";
import React, { memo } from "react";
import { Box, Button, Theme, Typography } from "@material-ui/core";
import { connect } from "react-redux";
import Link from "next/link";
import CartItem from "./CartItem";
import { CartProduct } from "../../pages/checkout";
import { StoreState } from "../../store";

export type CartState = {
  cartSize: number;
  total: number;
  selectedProducts: Record<string, CartProduct>;
};

const useStyles = makeStyles((theme: Theme) => ({
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
}));

function NonEmptyCart({ productsList, total }) {
  const classes = useStyles();
  return (
    <>
      {productsList.map((product) => (
        <Box key={product.id} marginY={1}>
          <CartItem product={product} />
        </Box>
      ))}
      <Box marginTop={2} className="flex justify-end items-center">
        <div className={`rounded ${classes.mainButtonContainer}`}>
          <div className={classes.textWrapper}>
            <Typography variant="h5" classes={{ root: classes.prePriceText }}>
              Итого
            </Typography>
            <Typography variant="h5">
              {total}
              ₴
            </Typography>
          </div>
          <Link href="/checkout">
            <Button
              classes={{ root: classes.mainButton }}
              color="primary"
              variant="contained"
              size="large"
            >
              Оформить заказ
            </Button>
          </Link>
        </div>
      </Box>
    </>
  );
}

function Cart({
  cartState: { total, selectedProducts, cartSize },
}: {
  cartState: CartState;
}) {
  const productsList = Object.values(selectedProducts);
  return (
    <div>
      <Box marginTop={2} minHeight="360px">
        {cartSize === 0 ? (
          <Box>
            <Typography variant="h3" align="center">
              Здесь пока ничего нет
            </Typography>
          </Box>
        ) : (
          <NonEmptyCart productsList={productsList} total={total} />
        )}
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
