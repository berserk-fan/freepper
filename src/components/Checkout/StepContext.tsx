import dynamic from "next/dynamic";
import React from "react";
import { OrderForm } from "./Definitions";
import { SummaryProps } from "./SummaryStep";
import DeliveryDetailsStep from "./DeliveryDetailsStep";

const SummaryStep = dynamic<SummaryProps>(() => import("./SummaryStep"));
const PaymentStep = dynamic(() => import("./Payment"));

export function StepContent({
  step,
  orderData,
}: {
  step: number;
  orderData: OrderForm;
}) {
  switch (step) {
    case 0:
      return <DeliveryDetailsStep />;
    case 1:
      return <SummaryStep orderForm={orderData} />;
    case 2:
      return <PaymentStep />;
    default:
      throw new Error("unknown step");
  }
}
