import { OrderForm } from "./Definitions";
import dynamic from "next/dynamic";
import { SummaryProps } from "./SummaryStep";
import DeliveryDetailsStep from "./DeliveryDetailsStep";
import React from "react";

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
      return <DeliveryDetailsStep orderForm={orderData} />;
    case 1:
      return SummaryStep ? (
        <SummaryStep orderForm={orderData} />
      ) : (
        <>Загрузка...</>
      );
    case 2:
      return PaymentStep ? <PaymentStep /> : <>Загрузка...</>;
    default:
      throw new Error("unknown step");
  }
}
