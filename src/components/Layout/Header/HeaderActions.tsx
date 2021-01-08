import Link from "next/link";
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import React from "react";
import {pages} from "./Header";
import {useRouter} from "next/router";

export default function HeaderActions(props) {
  const router = useRouter();
  const currentPath = router.pathname;

  return (
      <ButtonGroup {...props} color="primary" aria-label="page tabs">
          { pages.map(({path, name, icon}) => (
              <Link href={path}>
                  <Button startIcon={icon}>{name}</Button>
              </Link>
          ))
          }
    </ButtonGroup>
  );
}
