import Link from "next/link";
import React, { memo } from "react";
import { Box, Typography } from "@material-ui/core";
import { NavItem, NavMenu } from "@mui-treasury/components/menu/navigation";
import { Page, pages, shopPageGroup } from "./pages";

function HeaderActions(props) {
  const bigHeaderPages: Page[] = [
    pages.home,
    ...shopPageGroup.children,
    pages.about,
  ];
  return (
    <NavMenu {...props} aria-label="page tabs">
      {bigHeaderPages.map(({ id, path, name, Icon }) => (
        <NavItem key={id}>
          <Link href={path}>
            <Box className="flex justify-between items-center">
              <Icon />
              <Typography style={{ paddingLeft: "8px" }} variant="button">
                {name}
              </Typography>
            </Box>
          </Link>
        </NavItem>
      ))}
    </NavMenu>
  );
}

export default memo(HeaderActions);
