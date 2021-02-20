import React from "react";
const Header = dynamic(() => import("./Header/Header"), {
    ssr: false,
    loading: () => <Skeleton component={"div"} variant="rect" width={"100vw"} height={"100px"}/>
});
import ValueProp from "./Header/ValueProp";
const Footer = dynamic(() => import("./Footer/Footer"));
const BreadCrumbs = dynamic(() => import("./Breadcrumbs/BreadCrumbs"), {
    loading: () => <Skeleton component={"div"} variant="rect" width={"300px"} height={"30px"}/>,
    ssr: false
});

import {Skeleton} from "@material-ui/lab";
import dynamic from "next/dynamic";
import {Box} from "@material-ui/core";

export default function LayoutWithHeaderAndFooter({ children, showValueProp = false, disableBreadcrumbs = false, breadcrumbsOverrides = {} }) {
  return (
    <>
      <Header/>
      {showValueProp && <ValueProp />}
      {!disableBreadcrumbs && <Box px={1} py={"3px"} className={"w-full flex justify-center"}><BreadCrumbs overrides={breadcrumbsOverrides}/></Box>}
      {children}
      <Footer />
    </>
  );
}
