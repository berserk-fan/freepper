import Link from "next/link";
import React, { memo } from "react";
import Box from "@material-ui/core/Box";
import Typography from "@material-ui/core/Typography";
import makeStyles from "@material-ui/core/styles/makeStyles";
import { Page, pages, shopPageGroup } from "./pages";

const useStyles = makeStyles(({ palette, spacing, breakpoints }) => ({
  item: {
    flexShrink: 0,
    display: "flex",
    alignItems: "center",
    color: palette.text.secondary,
    borderRadius: 4,
    padding: spacing(1, 2),
    cursor: "pointer",
    textDecoration: "none",
    transition: "0.2s ease-out",
    "&:hover, &:focus": {
      color: palette.text.primary,
      backgroundColor: palette.action.selected,
    },
  },
  mainButtonGroup: {
    position: "absolute",
    width: 570,
    marginLeft: spacing(2),
    marginRight: "auto",
    left: 120,
    right: 0,
    textAlign: "center",
    justifyContent: "center",
    display: "none",
    [breakpoints.up("md")]: {
      marginLeft: "auto",
      display: "flex",
      alignItems: "stretch",
    },
  },
}));

function HeaderActions() {
  const classes = useStyles();
  const bigHeaderPages: Page[] = [
    pages.home,
    ...shopPageGroup.children,
    pages.about,
  ];
  return (
    <Box
      className={classes.mainButtonGroup}
      display="flex"
      aria-label="page tabs"
    >
      {bigHeaderPages.map(({ id, path, name, Icon }) => (
        <Link key={id} href={path}>
          <a className={classes.item}>
            <Box className="flex justify-between items-center">
              <Icon />
              <Typography style={{ paddingLeft: "8px" }} variant="button">
                {name}
              </Typography>
            </Box>
          </a>
        </Link>
      ))}
    </Box>
  );
}

export default memo(HeaderActions);
