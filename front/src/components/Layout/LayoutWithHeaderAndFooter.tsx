import React from "react";
import Skeleton from "@mui/lab/Skeleton";
import dynamic from "next/dynamic";
import Box from "@mui/material/Box";

const Header = dynamic(() => import("./Header/Header"), {
  ssr: false,
  loading: () => (
    <Skeleton
      component="div"
      variant="rectangular"
      width="100vw"
      height="100px"
    />
  ),
});

const ValueProp = dynamic(() => import("./Header/ValueProp"));
const Footer = dynamic(() => import("./Footer/Footer"));
const BreadCrumbs = dynamic(() => import("./Breadcrumbs/BreadCrumbs"));

export default function LayoutWithHeaderAndFooter({
  children,
  showValueProp = false,
  disableBreadcrumbs = false,
  breadcrumbsOverrides = {},
}) {
  return (
    <>
      <Header />
      {showValueProp && <ValueProp />}
      {!disableBreadcrumbs && (
        <Box px={1} py={1} className="w-full flex justify-center">
          <BreadCrumbs overrides={breadcrumbsOverrides} />
        </Box>
      )}
      {children}
      <Footer />
    </>
  );
}
