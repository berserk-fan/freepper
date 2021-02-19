import Header from "./Header/Header";
import React from "react";
const ValueProp = dynamic(() => import("./Header/ValueProp"));
const Footer = dynamic(() => import("./Footer/Footer"));
const BreadCrumbs = dynamic(() => import("./Breadcrumbs/BreadCrumbs"));
import {Box} from "@material-ui/core";
import dynamic from "next/dynamic";

export default function LayoutWithHeaderAndFooter({ children, showValueProp = false, disableBreadcrumbs = false, breadcrumbsOverrides = {} }) {
  return (
    <>
      <Header />
      {showValueProp && <ValueProp />}
      {!disableBreadcrumbs && <Box px={1} pt={0.5} className={"w-full flex justify-center"}><BreadCrumbs overrides={breadcrumbsOverrides}/></Box>}
      {children}
      <Footer />
    </>
  );
}
