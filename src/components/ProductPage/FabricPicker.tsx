import { Fabric } from "@mamat14/shop-server/shop_model";
import React, { MouseEventHandler, useState } from "react";
import Image from "next/image";
import { Box, Typography } from "@material-ui/core";
import theme from "../../theme";
import Link from "next/link";
import { borderColor } from "@material-ui/system";
import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles({
  fabricBlock: {
    borderColor: theme.palette.success.main,
  },
});

function FabricView({
  fabric,
  selected,
}: {
  fabric: Fabric & { href: string };
  selected: Boolean;
}) {
  const classes = useStyles();
  const borderSize = selected ? 2 : 0;
  return (
    <span>
      <Box
        className={`flex overflow-hidden items-center ${classes.fabricBlock} cursor-pointer`}
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

export default function FabricPicker({
  cur,
  fabrics,
}: {
  cur: string;
  fabrics: (Fabric & { href: string })[];
}) {
  const ordered = fabrics.sort((a, b) => a.id.localeCompare(b.id));
  const selectedSize = fabrics.find(f => f.id == cur);
  return (
    <div>
      <Typography variant={"subtitle1"} display={"inline"}>
        Цвет: {selectedSize.displayName}
      </Typography>
      <div className={"flex justify-start items-center gap-1"}>
        {ordered.map((fabric) => (
          <FabricView
            key={fabric.id}
            fabric={fabric}
            selected={fabric.id === cur}
          />
        ))}
      </div>
    </div>
  );
}
