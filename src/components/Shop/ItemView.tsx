import React, { memo, useState } from "react";
import { fade, makeStyles } from "@material-ui/core/styles";
import Card from "@material-ui/core/Card";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import { BriefProduct } from "../../types";
import {
  Box,
  Collapse,
  Divider,
  IconButton,
  Paper,
  Popover,
  Popper,
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
import Price from "./Price";
import Spacing from "../Commons/Spacing";
import { ExpandLess, ExpandMore } from "@material-ui/icons";
import PopupState, {
  bindHover,
  bindPopover,
  bindPopper,
  bindToggle,
  bindTrigger,
} from "material-ui-popup-state";

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
  media: {
    width: "100%",
    "&::after": {
      content: "",
      display: "block",
      "padding-bottom": "100%",
    },
  },
  previewText: {
    "line-height": "1.5em",
    height: "3em",
    overflow: "hidden",
    textOverflow: "ellipsis",
    width: "100%",
  },
});

function ItemView({
  product,
  className = "",
  productRef
}: {
  product: Product;
  className?: string;
  productRef: string;
}) {
  const classes = useStyles();
  const { id, displayName, description, images, price } = product;

  return (
    <Box className={`overflow-hidden ${className}`}>
      <div>
        <Slider
          slides={images.map((image) => (
            <Box
              className={`flex ${classes.media} overflow-hidden items-center`}
            >
              <Image
                width={500}
                height={500}
                src={image.src}
                alt={displayName}
                objectFit={"cover"}
              />
            </Box>
          ))}
        />

        <Box marginY={0.5} marginX={1} className={"flex items-center"}>
          <Box className={"flex flex-col"}>
            <Typography variant="subtitle1">{displayName}</Typography>
            <Box className={"flex"}>
              <Typography display={"inline"} variant={"body2"}>
                от <Price price={price} />
              </Typography>
            </Box>
          </Box>
          <Box style={{ marginLeft: "auto" }}>
            <Link href={productRef}>
              <Button color={"secondary"} variant={"outlined"}>
                Подробнее
              </Button>
            </Link>
          </Box>
        </Box>
      </div>
    </Box>
  );
}

export default memo(ItemView);
