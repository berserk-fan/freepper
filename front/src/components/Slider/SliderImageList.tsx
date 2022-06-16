import Box from "@material-ui/core/Box";
import Image from "next/image";
import React, { ReactElement, useState } from "react";
import makeStyles from "@material-ui/core/styles/makeStyles";
import { Image as MyImage } from "apis/image_list.pb";
import { SIZES } from "../Shop/definitions";
import Slider from "./Slider";

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
  fullHeight: {
    height: "100%",
  },
});

export default function SliderImageList({
  images,
  priority,
  wrapper = (x: ReactElement) => x,
  slideClassName = "",
}: {
  images: MyImage[];
  priority: boolean;
  wrapper?: (x: ReactElement) => ReactElement;
  slideClassName?: string;
}) {
  const classes = useStyles();
  const [isShowingArrows, setIsShowingArrows] = useState(false);

  return (
    <Box className={classes.media} position="relative">
      <Box className={classes.mediaChild}>
        <Slider
          className={classes.fullHeight}
          slideClassName={slideClassName}
          isShowingArrows={isShowingArrows}
          slides={images.map((image, idx) => (
            <Box key={image.src} className={classes.media}>
              {wrapper(
                <Image
                  priority={(idx === 0 && priority) || idx > 1}
                  src={image.src}
                  alt={image.alt}
                  layout="fill"
                  sizes={SIZES}
                  onLoad={() => setIsShowingArrows(true)}
                  objectFit="cover"
                />,
              )}
            </Box>
          ))}
        />
      </Box>
    </Box>
  );
}
