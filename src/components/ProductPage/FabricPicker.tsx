import { Fabric } from "@mamat14/shop-server/shop_model";
import React, { MouseEventHandler, useState } from "react";
import Image from "next/image";
import {Avatar, Box, Chip, Typography} from "@material-ui/core";
import theme from "../../theme";
import Link from "next/link";
import { makeStyles } from "@material-ui/styles";
import { useKeenSlider } from "keen-slider/react"
import "keen-slider/keen-slider.min.css"

function FabricView({
  fabric,
  selected,
  className = "",
}: {
  fabric: Fabric & { href: string };
  selected: Boolean;
  className?: string;
}) {
  return (
      <Link href={fabric.href} scroll={false} replace={true}>
          <Chip
              avatar={<Avatar>
                  <Image
                  width={30}
                  height={30}
                  src={fabric.image.src}
                  alt={fabric.image.alt}
                  />
              </Avatar>}
              clickable={true}
              variant={"outlined"}
              label={fabric.displayName}
          />
      </Link>
  );
}

const useStyles = makeStyles({
  fabricNode: {
    margin: theme.spacing(0.25),
  },
});

export default function FabricPicker({
  cur,
  fabrics,
}: {
  cur: string;
  fabrics: (Fabric & { href: string })[];
}) {
  const ordered = fabrics.sort((a, b) => a.id.localeCompare(b.id));
  const selectedSize = fabrics.find((f) => f.id == cur);
  const classes = useStyles();
    const [sliderRef] = useKeenSlider({
        slidesPerView: 3,
        mode: "free-snap",
        spacing: 3,
        loop: false,
    });
  return (
    <div>
      <Typography variant={"h5"} component={"h3"} display={"inline"}>
        Цвет: {selectedSize.displayName}
      </Typography>

      <div ref={sliderRef} className="keen-slider">
        {ordered.map((fabric) => (
            <div className={"keen-slider__slide"}>
          <FabricView
            key={fabric.id}
            fabric={fabric}
            selected={fabric.id === cur}
            className={classes.fabricNode}
          />
            </div>
        ))}
      </div>
    </div>
  );
}
