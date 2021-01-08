import { Size } from "@mamat14/shop-server/shop_model";
import { Typography } from "@material-ui/core";
import React from "react";
import Box from "@material-ui/core/Box";
import Link from "next/link";
import { makeStyles } from "@material-ui/styles";
import theme from "../../theme";

const useStyles = makeStyles({
    sizeBlock: ({ selected, borderWidth }: { selected: boolean; borderWidth: number }) => ({
      borderColor: selected
        ? theme.palette.success.main
        : theme.palette.grey["800"],
      borderWidth: `${borderWidth}px`,
    }),
  }
);

function SizeView({
  size,
  selected,
}: {
  size: Size & { href: string };
  selected: boolean;
}) {
  const borderWidth = selected ? 2 : 1;
  const classes = useStyles({ selected, borderWidth });
  return (
    <span>
      <Box
        className={`flex overflow-hidden items-center ${classes.sizeBlock} select-none cursor-pointer`}
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
  const selectedSize = sizes.find(s => s.id == cur);
  return (
    <div>
      <Typography variant={"subtitle1"} display={"inline"}>
        Размер: {selectedSize.description}
      </Typography>
      <div className={"flex justify-start items-center gap-1"}>
        {ordered.map((size) => (
          <SizeView key={size.id} size={size} selected={size.id === cur} />
        ))}
      </div>
    </div>
  );
}
