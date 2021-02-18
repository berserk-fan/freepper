import Link from "next/link";
import React from "react";
import { Page, pages, shopPageGroup } from "./Header";
import { Box, Typography } from "@material-ui/core";
import { NavItem, NavMenu } from "@mui-treasury/components/menu/navigation";

export default function HeaderActions(props) {
  const bigHeaderPages: Page[] = [
    pages.home,
    ...shopPageGroup.children,
    pages.about,
  ];
  return (
    <NavMenu {...props} aria-label="page tabs">
      {bigHeaderPages.map(({ id, path, name, icon }) => (
        <NavItem key={id}>
          <Link href={path}>
            <Box className={"flex justify-between items-center"}>
              {React.createElement(icon)}
              <Typography style={{ paddingLeft: "8px" }} variant={"button"}>
                {name}
              </Typography>
            </Box>
          </Link>
        </NavItem>
      ))}
    </NavMenu>
  );
}
