import Header from "./Header/Header";
import ValueProp from "./Header/ValueProp";
import CheckoutHeader from "./Header/CheckoutHeader";
import React from "react";

export default function LayoutWithHeader({ children, value = false }) {
  return (
    <>
      <Header />
      {value ? <ValueProp /> : false}
      {children}
    </>
  );
}
