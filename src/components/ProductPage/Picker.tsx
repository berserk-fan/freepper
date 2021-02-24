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

export default function Picker<T>({
  selectedId,
  items,
  icon,
}: {
  selectedId: string;
  items: Selectable<T>[];
  icon?: FunctionComponent<{ item: T }>;
}) {
  const classes = useStyles();
  return (
    <Box className="flex overflow-x-auto">
      {items.map((item) => (
        <Link key={item.href} href={item.href} scroll={false} replace>
          <Chip
            className={classes.fabricNode}
            avatar={
              icon ? (
                <Avatar>{React.createElement(icon, { item })}</Avatar>
              ) : undefined
            }
            clickable
            color={selectedId === item.id ? "secondary" : "default"}
            variant="outlined"
            label={item.displayName}
          />
        </Link>
      ))}
    </Box>
  );
}
