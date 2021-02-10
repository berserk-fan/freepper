import Header from "./Header/Header";
import ValueProp from "./Header/ValueProp";
import CheckoutHeader from "./Header/CheckoutHeader";
import React from "react";
import Footer2 from "./Footer/Footer";

export default function LayoutWithHeaderAndFooter({ children, value = false }) {
  return (
    <>
      <Header />
      {value ? <ValueProp /> : false}
      {children}
      <Footer2 />
    </>
  );
}
