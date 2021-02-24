import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import { Box } from "@material-ui/core";
import Image from "next/image";
import Slider from "./Slider";
import Price from "./Price";
import { TmpGroupedProduct } from "../../../configs/tmpProducts";
import { SIZES } from "./ShopDefinitions";

const useStyles = makeStyles({
  media: {
    width: "100%",
    "&::after": {
      content: '""',
      display: "block",
      "padding-bottom": "100%",
    },
  },
  mediaChild: {
    position: "absolute",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
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
    <Box className={`mx-auto ${className}`} maxWidth="500px">
      <Box className={classes.media} position="relative">
        <Box className={classes.mediaChild}>
          <Slider
            onChange={useSlideId}
            slides={images.map((image, idx) => (
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
        </Box>
      </Box>
      <Box marginY={0.5} marginX={1} className="flex items-center">
        <Box className="flex flex-col">
          <Typography variant="subtitle1">{displayName}</Typography>
          <Box className="flex">
            <Typography display="inline" variant="body2">
              от <Price price={price} />
            </Typography>
          </Box>
        </Box>
        <Box style={{ marginLeft: "auto" }}>
          <Link href={productHref(images[slideId].name)}>
            <Button color="secondary" variant="outlined">
              Подробнее
            </Button>
          </Link>
        </Box>
      </Box>
    </Box>
  );
}
