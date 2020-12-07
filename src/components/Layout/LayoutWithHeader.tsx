import Header from "./Header/Header";
import ValueProp from "./Header/ValueProp";
import CheckoutHeader from "./Header/CheckoutHeader";
import React from "react";

export default function LayoutWithHeader({
  children,
  value = false,
  component = false,
}: {
  children?: any;
  value?: boolean;
  component?: React.ReactNode;
}) {
  return (
    <>
      <Header component={component} />
      {value ? <ValueProp /> : false}
      {children}
    </>
  );
}
