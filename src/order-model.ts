import { CartProduct } from "./pages/checkout";

export enum PaymentOption {
  COD = "COD",
  PREPAYMENT = "PREPAYMENT",
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
  cart: CartProduct[];
  total: number;
}
