import { Box, Link, ListItem, ListItemText } from "@material-ui/core";
import React from "react";

export default function PhoneNumber({
  className = "",
  phone,
}: {
  className?: string;
  phone: string;
}) {
  return (
    <ListItem button className={className}>
      <ListItemText>
        <Link color="textPrimary" href={`tel:${phone}`}>
          <Box fontFamily="Monospace">{phone}</Box>
        </Link>
      </ListItemText>
    </ListItem>
  );
}
