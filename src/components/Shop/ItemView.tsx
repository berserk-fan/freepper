import React, {memo, useEffect, useState} from "react";
import { fade, makeStyles } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import { Box } from "@material-ui/core";
import ShoppingCartTwoToneIcon from "@material-ui/icons/ShoppingCartTwoTone";
import { withStyles } from "@material-ui/styles";
import theme from "../../theme";
import Image from "next/image";
import Slider from "./Slider";
import { ToggleButton } from "@material-ui/lab";
import Price from "./Price";
import { TmpGroupedProduct } from "../../../configs/tmpProducts";
import {SIZES} from "./ShopDefinitions";

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
      content: "\"\"",
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

export default function ItemView({
  product,
  className = "",
  categoryName,
  priority,
}: {
  product: TmpGroupedProduct;
  className?: string;
  categoryName: string;
  priority: boolean;
}) {
  const classes = useStyles();
  const { displayName, images, price } = product;
  const [slideId, useSlideId] = useState(0);

  function productHref(productName: string) {
    return `/${categoryName}/${productName}`;
  }

  return (
    <Box className={`mx-auto overflow-hidden ${className}`} maxWidth={"500px"}>
      <Slider onChange={useSlideId} slides={images.map((image, idx) => (
          <Box key={image.src} className={classes.media}>
            <Link href={productHref(image.name)}>
              <Image
                priority={idx === 0 && priority}
                src={image.src}
                alt={image.alt}
                layout="fill"
                sizes={SIZES}
              />
            </Link>
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
          <Link href={productHref(images[slideId].name)}>
            <Button color={"secondary"} variant={"outlined"}>
              Подробнее
            </Button>
          </Link>
        </Box>
      </Box>
    </Box>
  );
}

