import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
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
