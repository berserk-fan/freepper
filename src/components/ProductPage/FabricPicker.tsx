import { Fabric } from "@mamat14/shop-server/shop_model";
import React, { MouseEventHandler, useState } from "react";
import Image from "next/image";
import { Box, Typography } from "@material-ui/core";
import theme from "../../theme";
import Link from "next/link";
import { makeStyles } from "@material-ui/styles";

function FabricView({
  fabric,
  selected,
  className = "",
}: {
  fabric: Fabric & { href: string };
  selected: Boolean;
  className?: string;
}) {
  const borderSize = selected ? 2 : 0;
  return (
    <span>
      <Box
        style={{ borderColor: theme.palette.success.main }}
        className={`flex overflow-hidden items-center cursor-pointer ${className}`}
        border={borderSize}
        borderRadius={25}
      >
        <Link href={fabric.href} scroll={false} replace={true}>
          <Image
            width={30}
            height={30}
            src={fabric.image.src}
            alt={fabric.image.alt}
          />
        </Link>
      </Box>
    </span>
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
  return (
    <div>
      <Typography variant={"subtitle1"} display={"inline"}>
        Цвет: {selectedSize.displayName}
      </Typography>
      <div className={"flex justify-start items-center"}>
        {ordered.map((fabric) => (
          <FabricView
            key={fabric.id}
            fabric={fabric}
            selected={fabric.id === cur}
            className={classes.fabricNode}
          />
        ))}
      </div>
    </div>
  );
}
