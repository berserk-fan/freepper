import React, {memo, useState} from "react";
import { makeStyles } from "@material-ui/core/styles";
import Card from "@material-ui/core/Card";
import CardActionArea from "@material-ui/core/CardActionArea";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import CardMedia from "@material-ui/core/CardMedia";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import { BriefProduct } from "../types";
import { Badge, Box, IconButton } from "@material-ui/core";
import ShoppingCartTwoToneIcon from "@material-ui/icons/ShoppingCartTwoTone";
import AddShoppingCartIcon from "@material-ui/icons/AddShoppingCart";
import { useSnackbar } from "notistack";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import { createStyles, withStyles } from "@material-ui/styles";
import theme from "../theme";
import { connect } from "react-redux";
import {addProductAction, deleteProductAction, SetProductCountAction, StoreState} from "../store";
import {Product} from "@mamat14/shop-server/shop_model";

const useStyles = makeStyles({
  root: {
    maxWidth: 345,
  },
  media: {
    height: 280,
  },
});

const AddedToCartBadge = withStyles(
  createStyles({
    badge: {
      right: 10,
      top: 0,
      color: theme.palette.success.light,
      backgroundColor: theme.palette.background.default,
      padding: 0,
    },
  })
)(Badge);

function ItemView({
  product,
  addProduct,
  deleteProduct,
  className,
  inCart
}: {
  product: Product;
  className?: string;
  addProduct: (product: Product) => void;
  deleteProduct: (id: string) => void;
  inCart: boolean;
}) {
  const classes = useStyles();
  const { id, displayName, description, image } = product;
  const { enqueueSnackbar } = useSnackbar();

  function handleAddedToCart() {
    let message;
    if (!inCart) {
      addProduct(product);
      message = "Добавлено в корзину";
    } else {
      deleteProduct(id);
      message = "Удалено из корзины";
    }
    enqueueSnackbar(message, { variant: "success", autoHideDuration: 1500 });
  }

  return (
    <Card className={`${classes.root} ${className || ""}`}>
      <CardActionArea>
        <CardMedia
          image={image.src}
          className={`${classes.media}`}
          title={displayName}
        />
        <CardContent>
          <Typography gutterBottom variant="h5" component="h2">
            {displayName}
          </Typography>
          <Typography variant="body2" color="textSecondary" component="p">
            {description}
          </Typography>
        </CardContent>
      </CardActionArea>
      <CardActions>
        <Box paddingX={3} className={"flex w-full justify-between"}>
          <Button size="small" color="primary">
            <Link href={`/shop/${id}`}>
              <Typography>Подробнее</Typography>
            </Link>
          </Button>
          <IconButton onClick={handleAddedToCart} size={"small"}>
            {inCart ? (
              <AddedToCartBadge
                anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
                badgeContent={<CheckCircleIcon fontSize={"small"} />}
              >
                <ShoppingCartTwoToneIcon fontSize={"large"} />
              </AddedToCartBadge>
            ) : (
              <AddShoppingCartIcon fontSize={"large"} />
            )}
          </IconButton>
        </Box>
      </CardActions>
    </Card>
  );
}

function mapStateToProps(
  state: StoreState,
  { product }: { product: BriefProduct }
) {
  return {
    inCart: !!state.cartState.selectedProducts[product.id]
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addProduct: (product) => dispatch(addProductAction(product)),
    deleteProduct: (id) => dispatch(deleteProductAction(id)),
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(memo(ItemView));
