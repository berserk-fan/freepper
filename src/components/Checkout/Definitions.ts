import { makeValidateSync } from "mui-rff";
import { mixed, number, object, ObjectSchema, string } from "yup";
import { DeliveryOption, DeliveryProvider, Order, PaymentOption } from "../../order-model";
import { CartState } from "../Cart/Cart";

export const buttonTexts = [
  "К проверке заказа",
  "К оплате заказа",
  "Отправить заказ",
];
export type OrderForm = Partial<{
  city: string;
  warehouseNumber: number;
  deliveryProvider: DeliveryProvider;
  name: string;
  deliveryOption: DeliveryOption;
  phone: string;
  paymentOption: PaymentOption;
}>;
export const schema: ObjectSchema<OrderForm> = object({
  paymentOption: mixed().oneOf([PaymentOption.COD]).default(PaymentOption.COD),
  city: string()
    .required("Введите город, пожалуйста")
    .max(50, "Слишком длинный город"),
  warehouseNumber: number()
    .required("Введите номер отделения, пожалуйста")
    .positive("Номер отделения должен быть больше нуля."),
  deliveryProvider: mixed()
    .oneOf([DeliveryProvider.NOVAYA_POCHTA])
    .required()
    .default(DeliveryProvider.NOVAYA_POCHTA),
  name: string()
    .required("Введите имя, пожалуйста")
    .min(5, "Cлишком короткое имя")
    .max(100, "Слишком длинное имя"),
  deliveryOption: mixed().oneOf([
    DeliveryOption.COURIER,
    DeliveryOption.TO_WAREHOUSE,
  ]),
  phone: string()
    .required("Введите номер телефона, пожалуйста")
    .min(6, "Слишком короткий номер телефона")
    .max(100, "Слишком длинный номер телефона"),
});
export const validate = makeValidateSync(schema);
export const steps = ["Доставка", "Проверка", "Оплата"];
export const initialValues: Partial<OrderForm> = {
  paymentOption: PaymentOption.COD,
  deliveryProvider: DeliveryProvider.NOVAYA_POCHTA,
  deliveryOption: DeliveryOption.TO_WAREHOUSE,
};

export function toOrder(cart: CartState, orderForm: OrderForm) {
  const order: Order = {
    paymentOption: PaymentOption.COD,
    cart: cart.selectedProducts,
    total: cart.total,
    deliveryDetails: {
      provider: orderForm.deliveryProvider,
      option: orderForm.deliveryOption,
      address: orderForm.city + orderForm.warehouseNumber,
      phone: orderForm.phone,
      fullName: orderForm.name,
    },
  };
  return order;
}

export function getDeliveryOptionName(option: DeliveryOption) {
  switch (option) {
    case DeliveryOption.COURIER:
      return "Курьер";
    case DeliveryOption.TO_WAREHOUSE:
      return "В отделение";
    default:
      return "Недоступно";
  }
}

export function getDeliveryProviderName(provider: DeliveryProvider) {
  if (provider === DeliveryProvider.NOVAYA_POCHTA) {
    return "Новая почта";
  } 
  return "Неизвестно";
  
}
