import React, { memo, useState } from "react";
import { fade, makeStyles } from "@material-ui/core/styles";
import Card from "@material-ui/core/Card";
import CardActionArea from "@material-ui/core/CardActionArea";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import { BriefProduct } from "../../types";
import {
  Box,
  ButtonGroup,
  Collapse,
  Dialog,
  Divider,
  Fade,
  Grow,
  IconButton,
  Paper,
  Toolbar,
  useMediaQuery,
} from "@material-ui/core";
import ShoppingCartTwoToneIcon from "@material-ui/icons/ShoppingCartTwoTone";
import { withStyles } from "@material-ui/styles";
import theme from "../../theme";
import { connect } from "react-redux";
import { addProductAction, deleteProductAction, StoreState } from "../../store";
import { Product } from "@mamat14/shop-server/shop_model";
import Image from "next/image";
import "./Slider.module.css";
import Slider from "./Slider";
import { ToggleButton } from "@material-ui/lab";
import { CustomAppBar } from "../Layout/Header/CustomAppBar";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
const CartButton = withStyles({
  root: {
    fontSize: "24px",
    borderRadius: "20%",
    "&$selected": {
      color: fade(theme.palette.success.dark, 0.9),
      backgroundColor: fade(theme.palette.success.light, 0.12),
    },
    "&$selected:hover": {
      backgroundColor: fade(theme.palette.success.light, 0.2),
    },
  },
  selected: {},
})(ToggleButton);

function AddToCartButton({
  handleAddedToCart,
  inCart,
}: {
  handleAddedToCart: () => void;
  inCart: boolean;
}) {
  return (
    <CartButton onClick={handleAddedToCart} size="small" selected={inCart}>
      <ShoppingCartTwoToneIcon fontSize={"inherit"} />
    </CartButton>
  );
}

const useStyles = makeStyles({
  root: {
    maxWidth: 345,
  },
  media: {
    height: 280,
  },
});

function ItemView({
  product,
  addProduct,
  deleteProduct,
  className,
  inCart,
}: {
  product: Product;
  className?: string;
  addProduct: (product: Product) => void;
  deleteProduct: (id: string) => void;
  inCart: boolean;
}) {
  const classes = useStyles();
  const { id, displayName, description, image } = product;

  function handleAddedToCart() {
    !inCart ? addProduct(product) : deleteProduct(id);
  }

  return (
    <>
      <Card className={`${classes.root} ${className || ""}`}>
        <CardActionArea>
          <Slider
            slides={[
              <Box
                className={`flex ${classes.media} overflow-hidden items-center`}
              >
                <Image
                  width={500}
                  height={500}
                  src={image.src}
                  alt={displayName}
                />
              </Box>,
            ]}
          />
          <Link href={`shop/${id}`}>
            <CardContent>
              <Typography gutterBottom variant="h5">
                {displayName}
              </Typography>
              <Typography
                variant="subtitle2"
                color="textSecondary"
                component="p"
              >
                {description}
              </Typography>
            </CardContent>
          </Link>
        </CardActionArea>
        <Divider />
        <CardActions>
          <Box
            paddingLeft={1}
            className={"flex w-full justify-between items-center"}
          >
            <Typography variant={"h5"}>{product.price.price + " ₴"}</Typography>
            <Box className={"flex items-stretch gap-2"}>
              <Button variant={"outlined"}>Подробнее</Button>
              <AddToCartButton {...{ handleAddedToCart, inCart }} />
            </Box>
          </Box>
        </CardActions>
      </Card>
    </>
  );
}

function mapStateToProps(
  state: StoreState,
  { product }: { product: BriefProduct }
) {
  return {
    inCart: !!state.cartState.selectedProducts[product.id],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addProduct: (product) => dispatch(addProductAction(product)),
    deleteProduct: (id) => dispatch(deleteProductAction(id)),
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(memo(ItemView));
