import React, { memo } from "react";
import { connect } from "react-redux";
import Link from "next/link";
import { CartState, StoreState } from "store";
import Box from "@material-ui/core/Box/Box";
import Typography from "@material-ui/core/Typography/Typography";
import Button from "@material-ui/core/Button/Button";
import makeStyles from "@material-ui/core/styles/makeStyles";
import CartItem from "./CartItem";
import { pages } from "../Layout/Header/pages";

const useStyles = makeStyles((theme) => ({
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
          <CartItem cartProduct={product} />
        </Box>
      ))}
      <Box marginTop={2} className="flex justify-end items-center">
        <div className={`rounded ${classes.mainButtonContainer}`}>
          <div className={classes.textWrapper}>
            <Typography variant="h5" classes={{ root: classes.prePriceText }}>
              Итого
            </Typography>
            <Typography variant="h5">{total}₴</Typography>
          </div>
          <Link href={pages.checkout.path}>
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
  console.log(productsList);
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
