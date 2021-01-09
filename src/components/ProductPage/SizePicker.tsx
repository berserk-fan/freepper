import { Size } from "@mamat14/shop-server/shop_model";
import { Typography } from "@material-ui/core";
import React from "react";
import Box from "@material-ui/core/Box";
import Link from "next/link";
import { makeStyles } from "@material-ui/styles";
import theme from "../../theme";

const useStyles = makeStyles({
  sizeNode: {
    margin: theme.spacing(0.25),
  },
});

function SizeView({
  size,
  selected,
  className = "",
}: {
  size: Size & { href: string };
  selected: boolean;
  className?: string;
}) {
  const borderWidth = selected ? 2 : 1;
  const borderColor = selected
    ? theme.palette.success.main
    : theme.palette.grey["800"];
  return (
    <span>
      <Box
        style={{ borderColor, borderWidth }}
        className={`flex overflow-hidden items-center select-none cursor-pointer ${className}`}
        width={"30px"}
        height={"30px"}
      >
        <Link href={size.href} scroll={false} replace={true}>
          <Typography
            className={"w-full"}
            variant={"button"}
            display={"inline"}
            align={"center"}
          >
            {size.displayName}
          </Typography>
        </Link>
      </Box>
    </span>
  );
}

export default function SizePicker({
  cur,
  sizes,
}: {
  cur: string;
  sizes: (Size & { href: string })[];
}) {
  const ordered = sizes.sort((a, b) =>
    a.displayName.localeCompare(b.displayName)
  );
  const selectedSize = sizes.find((s) => s.id == cur);
  const classes = useStyles();
  return (
    <div>
      <Typography variant={"subtitle1"} display={"inline"}>
        Размер: {selectedSize.description}
      </Typography>
      <Box className={"flex justify-start items-center"}>
        {ordered.map((size) => (
          <SizeView
            className={classes.sizeNode}
            key={size.id}
            size={size}
            selected={size.id === cur}
          />
        ))}
      </Box>
    </div>
  );
}
