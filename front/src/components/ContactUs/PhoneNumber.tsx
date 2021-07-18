import Box from "@material-ui/core/Box/Box";
import Link from "@material-ui/core/Link";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
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
