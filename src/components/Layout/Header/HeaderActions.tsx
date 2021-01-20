import Link from "next/link";
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import React from "react";
import { pages } from "./Header";
import { makeStyles } from "@material-ui/styles";
import theme from "../../../theme";
import { Box } from "@material-ui/core";

const useStyles = makeStyles({
  root: {
    color: theme.palette.primary.contrastText,
  },
});

export default function HeaderActions(props) {
  const classes = useStyles();
  return (
    <ButtonGroup {...props} color="primary" aria-label="page tabs">
      {Object.values(pages).map(({ id, path, name, icon }) => (
        <Box key={id} display={"inline"} marginX={1}>
          <Link href={path}>
            <Button classes={classes} startIcon={icon} variant="outlined">
              {name}
            </Button>
          </Link>
        </Box>
      ))}
    </ButtonGroup>
  );
}
