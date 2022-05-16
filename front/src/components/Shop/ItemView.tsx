import React, { useState } from "react";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Box from "@material-ui/core/Box";
import dynamic from "next/dynamic";
import { Model } from "apis/model.pb";
import { Image as MyImage } from "apis/image_list.pb";
import Image from "next/image";
import Price from "./Price";
import { SIZES } from "./definitions";

const Slider = dynamic(() => import("../Slider/Slider"));
const useStyles = makeStyles({
  media: {
    width: "100%",
    cursor: "pointer",
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
  model,
  className = "",
  priority,
}: {
  model: Model;
  className?: string;
  priority: boolean;
}) {
  const classes = useStyles();
  const { displayName, imageList, minimalPrice } = model;
  let images: MyImage[];
  switch (imageList.$case) {
    case "imageListData":
      images = imageList.imageListData.images;
      break;
    default:
      throw new Error("illegal state");
  }

  const [slideId, useSlideId] = useState(0);
  const [isShowingArrows, setIsShowingArrows] = useState(false);

  return (
    <Box className={`mx-auto ${className}`} maxWidth="500px">
      <Box className={classes.media} position="relative">
        <Box className={classes.mediaChild}>
          <Slider
            onChange={useSlideId}
            isShowingArrows={isShowingArrows}
            slides={images.map((image, idx) => (
              <Box key={image.src} className={classes.media}>
                <Link href={`/${model.name}`}>
                  <Box>
                    <Image
                      priority={(idx === 0 && priority) || idx > 1}
                      src={image.src}
                      alt={image.alt}
                      layout="fill"
                      sizes={SIZES}
                      onLoad={() => setIsShowingArrows(true)}
                      objectFit="cover"
                    />
                  </Box>
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
              от <Price price={minimalPrice} />
            </Typography>
          </Box>
        </Box>
        <Box style={{ marginLeft: "auto" }}>
          <Link href={`/${model.name}`}>
            <Button color="secondary" variant="outlined">
              Подробнее
            </Button>
          </Link>
        </Box>
      </Box>
    </Box>
  );
}
