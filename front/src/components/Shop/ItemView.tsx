import React, { ReactElement } from "react";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import Box from "@material-ui/core/Box";
import dynamic from "next/dynamic";
import { Model } from "apis/model.pb";
import Price from "./Price";

const SliderImageList = dynamic(() => import("../Slider/SliderImageList"));

export default function ItemView({
  model,
  className = "",
  priority,
}: {
  model: Model;
  className?: string;
  priority: boolean;
}) {
  const { displayName, imageList, minimalPrice } = model;
  const wrapper = (x: ReactElement) => <Link href={`/${model.name}`}>{x}</Link>;
  return (
    <Box className={`mx-auto ${className}`} maxWidth="500px">
      <SliderImageList
        images={imageList.images}
        priority={priority}
        wrapper={wrapper}
      />
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
