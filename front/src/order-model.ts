import { CartProduct } from "./store";

export enum PaymentOption {
  COD = "COD",
}

export enum DeliveryOption {
  COURIER = "COURIER",
  TO_WAREHOUSE = "TO_WAREHOUSE",
}

export enum DeliveryProvider {
  NOVAYA_POCHTA = "NOVAYA_POCHTA",
}

export interface DeliveryDetails {
  provider: DeliveryProvider;
  option: DeliveryOption;
  fullName: string;
  address: string;
  phone: string;
}

export interface Order {
  deliveryDetails: DeliveryDetails;
  paymentOption: PaymentOption;
  cart: Record<string, CartProduct>;
  total: number;
}
