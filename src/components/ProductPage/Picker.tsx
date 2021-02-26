import React, { FunctionComponent } from "react";
import { Avatar, Box, Chip } from "@material-ui/core";
import Link from "next/link";
import { makeStyles } from "@material-ui/styles";

type Selectable<T> = T & {
  href: string;
  id: string;
  displayName: React.ReactNode;
};
const useStyles = makeStyles({
  fabricNode: {
    margin: "2px",
  },
});

function MyChipNoMemo<T>({ icon, item, selected }: { icon: React.FunctionComponent<{ item: T }>, item: T & { href: string; id: string; displayName: React.ReactNode }, selected: boolean }) {
  const classes = useStyles();
  return (
    <Chip
      className={classes.fabricNode}
      avatar={
        icon ? (
          <Avatar>{React.createElement(icon, { item })}</Avatar>
        ) : undefined
      }
      clickable
      color={selected ? "secondary" : "default"}
      variant="outlined"
      label={item.displayName}
    />
  );
}

const MyChip = React.memo(MyChipNoMemo, (prev, cur) => prev.item.id === cur.item.id && prev.selected === cur.selected);

function Picker<T>({
  selectedId,
  items,
  icon,
}: {
  selectedId: string;
  items: Selectable<T>[];
  icon?: FunctionComponent<{ item: T }>;
}) {
  return (
    <Box className="flex overflow-x-auto">
      {items.map((item) => (
        <Link key={item.id} href={item.href} scroll={false}>
          <a>
            <MyChip
              key={item.id}
              icon={icon}
              item={item}
              selected={selectedId === item.id}
            />
          </a>
        </Link>,
      )}
    </Box>
  );
}

export default Picker;
