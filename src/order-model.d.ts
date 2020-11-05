import { CartProduct } from "./pages/checkout";

interface LiqPay {
  type: "LiqPay";
}

type PaymentProvider = LiqPay;

interface COD {
  type: "COD";
}

interface Prepayment {
  type: "PREPAYMENT";
}

type PaymentOption = COD | Prepayment;

interface CourierShipping {
  type: "COURIER";
  address: string;
}

interface WarehouseShipping {
  type: "TO_WAREHOUSE";
  warehouseAddress: string;
}

type DeliveryOption = CourierShipping | WarehouseShipping;
interface NovayaPochta {
  type: "NOVAYA_POCHTA";
}

type DeliveryProvider = NovayaPochta;

interface DeliveryDetails {
  provider: DeliveryProvider;
  option: DeliveryOption;
  fullName: string;
  phone?: string;
  email?: string;
  address: string
}

interface PaymentDetails {
  provider: PaymentProvider;
  option: PaymentOption;
}

interface Order {
  deliveryDetails: DeliveryDetails;
  paymentDetails: PaymentDetails;
  cart: CartProduct[];
  total: number;
}

type OrderState = Partial<{
  deliveryDetails: Partial<DeliveryDetails>;
  paymentDetails: Partial<PaymentDetails>;
  cart: Partial<CartProduct[]>;
  total: Partial<number>;
}>
