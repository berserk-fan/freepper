import {
  Box,
  Card,
  CardContent,
  IconButton,
  Typography,
} from "@material-ui/core";
import Image from "next/image";
import React from "react";
import { makeStyles } from "@material-ui/styles";
import RemoveIcon from "@material-ui/icons/Remove";
import { connect } from "react-redux";
import AddIcon from "@material-ui/icons/Add";

import {
  CartProduct,
  deleteProductAction,
  setProductCountAction,
} from "../../store";
import ActionsPopover from "./ActionsPopover";
import AdditionalInfo from "./AdditionalInfo";

const useStyles = makeStyles({
  root: {
    height: 148,
    display: "flex",
  },
  imageContainer: {
    minWidth: 106,
    maxWidth: 148,
    height: 148,
    flexShrink: 2,
    overflow: "hidden",
  },
  image: {
    height: 148,
    minWidth: 148,
  },
  dataContainer: {
    minWidth: 140,
    flexShrink: 1,
  },
  quantityControls: {
    border: "1px solid",
    display: "inline-flex",
    padding: "4px",
    alignItems: "center",
    borderColor: "#e0e0e0",
    borderRadius: "40px",
  },
  quantityControlsIcon: {
    fontSize: "16px",
  },
  quantityControlsIconButton: {
    padding: "8px",
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
      <Box className={classes.quantityControls}>
        <IconButton
          className={classes.quantityControlsIconButton}
          disabled={count <= 1}
          onClick={() => setProductCount(id, count - 1)}
        >
          <RemoveIcon className={classes.quantityControlsIcon} />
        </IconButton>
        <Box className="select-none" fontFamily="Monospace">
          {count}
        </Box>
        <IconButton
          className={classes.quantityControlsIconButton}
          onClick={() => setProductCount(id, count + 1)}
        >
          <AddIcon className={classes.quantityControlsIcon} />
        </IconButton>
      </Box>
    );
  }

  return (
    <Card variant="outlined" className={classes.root}>
      <div className={`flex justify-center ${classes.imageContainer}`}>
        <div className={`flex ${classes.image}`}>
          <Image width={148} height={148} src={image.src} alt={image.alt} />
        </div>
      </div>
      <div
        className={`flex flex-col justify-between flex-grow ${classes.dataContainer}`}
      >
        <CardContent>
          <div className="flex flex-col justify-between">
            <Typography noWrap align="right" variant="h6">
              {displayName}
            </Typography>
            <Typography noWrap color="textSecondary" align="right" variant="h6">
              {price.price} ₴
            </Typography>
          </div>
        </CardContent>
        <Box
          marginBottom={1}
          paddingLeft={1}
          paddingRight={2}
          className="flex justify-between items-center"
        >
          <Box marginLeft={1}>
            <AdditionalInfo {...product} />
          </Box>
          <Box marginLeft={1} className="flex place-items-center">
            <QuantityControls />
            <ActionsPopover productId={id} deleteProduct={deleteProduct} />
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
