import React from "react";
import Breadcrumbs from "@mui/material/Breadcrumbs";
import { useRouter } from "next/router";
import Link from "next/link";
import { Typography } from "@mui/material";
import { pathNeeded, prefixes, toName, toPath } from "./utils";

// overrides: /collection/element_125 + overrides == {element_125: "My Element"} = /collection/My Element
export default function BreadCrumbs({
  overrides = {},
}: {
  overrides?: Record<string, string>;
}) {
  const router = useRouter();
  const [path] = router.asPath.split("?");
  const sections = path === "/" ? [""] : path.split("/");
  const paths = prefixes(sections).filter(pathNeeded);
  return (
    <Breadcrumbs aria-label="breadcrumb">
      {paths.map((pathArr) => {
        const pathAsString = toPath(pathArr);
        const name =
          overrides[pathArr[pathArr.length - 1]] ||
          toName(pathAsString, pathArr);
        return (
          <Link key={pathAsString} href={pathAsString}>
            <a>
              <Typography component="span" variant="h5">
                {name}
              </Typography>
            </a>
          </Link>
        );
      })}
    </Breadcrumbs>
  );
}
