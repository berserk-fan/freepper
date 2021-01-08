import { Product } from "@mamat14/shop-server/shop_model";
import {
  Box,
  Button,
  Card,
  CardContent,
  IconButton,
  Popover,
  Typography,
} from "@material-ui/core";
import Image from "next/image";
import PopupStateComponent, {
  bindPopover,
  bindTrigger,
} from "material-ui-popup-state";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import DeleteIcon from "@material-ui/icons/Delete";
import CloseIcon from "@material-ui/icons/Close";
import React, { useState } from "react";
import { makeStyles } from "@material-ui/styles";
import AddCircleOutlineIcon from "@material-ui/icons/AddCircleOutline";
import RemoveCircleOutlineIcon from "@material-ui/icons/RemoveCircleOutline";
import { CartState } from "./Cart";
import { connect } from "react-redux";
import theme from "../../theme";
import { CartProduct } from "../../pages/checkout";
import {
  deleteProductAction,
  setProductCountAction,
  StoreState,
} from "../../store";

const useStyles = makeStyles({
  root: {
    height: 148,
    display: "flex",
  },
  imageContainer: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    minWidth: 106,
    maxWidth: 148,
    height: 148,
    flexShrink: 2,
    overflow: "hidden",
  },
  image: {
    height: 148,
  },
  dataContainer: {
    minWidth: 140,
    flexShrink: 1,
  },
});

const cartItem = function CartItem({
  product,
  setProductCount,
  deleteProduct,
}: {
  product: CartProduct;
  setProductCount: (id: string, x: number) => void;
  deleteProduct: (id) => void;
}) {
  const { displayName, price, images, id, count } = product;
  const image = images[0];
  const classes = useStyles();

  function QuantityControls() {
    return (
      <>
        <IconButton
          size={"small"}
          disabled={count <= 1}
          onClick={() => setProductCount(id, count - 1)}
        >
          <RemoveCircleOutlineIcon fontSize={"large"} />
        </IconButton>
        <Box fontFamily={"Monospace"}>
          <Typography variant={"h6"} classes={{ root: "select-none" }}>
            {count}
          </Typography>
        </Box>
        <IconButton
          size={"small"}
          onClick={() => setProductCount(id, count + 1)}
        >
          <AddCircleOutlineIcon fontSize={"large"} />
        </IconButton>
      </>
    );
  }

  return (
    <Card variant={"outlined"} className={classes.root}>
      <div className={`${classes.imageContainer}`}>
        <div className={`${classes.image}`}>
          <Image width={148} height={148} src={image.src} alt={image.alt} />
        </div>
      </div>
      <div
        className={`flex flex-col justify-between flex-grow ${classes.dataContainer}`}
      >
        <CardContent>
          <div className="flex flex-col justify-between">
            <Typography noWrap align={"right"} variant={"h6"}>
              {displayName}
            </Typography>
            <Typography
              noWrap
              color={"textSecondary"}
              align={"right"}
              variant={"h6"}
            >
              {price.price} ₴
            </Typography>
          </div>
        </CardContent>
        <Box
          marginBottom={1}
          paddingLeft={1}
          paddingRight={2}
          className={"flex justify-between items-center"}
        >
          <Box marginLeft={1}>{getAdditionalInfo(product)}</Box>
          <Box marginLeft={1} className={"flex place-items-center"}>
            {QuantityControls()}
            {ActionsPopover(id, deleteProduct)}
          </Box>
        </Box>
      </div>
    </Card>
  );
};

function mapDispatchToProps(dispatch) {
  return {
    setProductCount: (a1, a2) => dispatch(setProductCountAction(a1, a2)),
    deleteProduct: (a) => dispatch(deleteProductAction(a)),
  };
}

export default connect(null, mapDispatchToProps)(cartItem);

function getAdditionalInfo({ details }: Product) {
  switch (details.$case) {
    case "dogBed":
      const size = details.dogBed.sizes.find(
        (s) => s.id == details.dogBed.sizeId
      );
      return (
        <div>
          <Typography noWrap display={"inline"} variant={"caption"}>
            Размер:
          </Typography>
          <Typography noWrap display={"inline"} variant={"h6"}>
            {" " + size.displayName}
          </Typography>
        </div>
      );
  }
}

export function ActionsPopover(
  productId: string,
  deleteProduct: (id: string) => void
) {
  return (
    <PopupStateComponent variant="popover" popupId="cart-action-popover">
      {(popupState) => (
        <div>
          <IconButton
            size={"small"}
            color={"primary"}
            {...bindTrigger(popupState)}
          >
            <MoreVertIcon />
          </IconButton>
          <Popover
            {...bindPopover(popupState)}
            anchorOrigin={{
              vertical: "top",
              horizontal: "left",
            }}
            transformOrigin={{
              vertical: "top",
              horizontal: "right",
            }}
          >
            <div className={"flex flex-col"}>
              <Button
                onClick={() => {
                  deleteProduct(productId);
                  popupState.close();
                }}
                fullWidth
                startIcon={<DeleteIcon />}
              >
                <Typography>Удалить из корзины</Typography>
              </Button>
              <Button
                onClick={popupState.close}
                fullWidth
                startIcon={<CloseIcon />}
              >
                <Typography>Закрыть</Typography>
              </Button>
            </div>
          </Popover>
        </div>
      )}
    </PopupStateComponent>
  );
}
