import { NextApiRequest, NextApiResponse } from "next";
import { Order } from "../../order-model";
import { CartProduct } from "../checkout";
import nodemailer from "nodemailer";
import { renderJSONPlusCss } from "../../utils/utils";

function pick<T, K extends keyof T>(obj: T, ...keys: K[]): Pick<T, K> {
  const ret: any = {};
  keys.forEach((key) => {
    ret[key] = obj[key];
  });
  return ret;
}

function prepareCart(cart: Record<string, CartProduct>): any {
  const val = Object.entries(cart).map(([id, product]) => {
    const fabric = product.details.dogBed.fabrics.find(
      (f) => f.id == product.details.dogBed.fabricId
    );
    const prepFabric = fabric.displayName;
    const size = product.details.dogBed.sizes.find(
      (s) => s.id == product.details.dogBed.sizeId
    );
    const prepSize = size.displayName;
    return [
      id,
      {
        ...pick(product, "displayName", "count"),
        ...{
          price: product.price.price,
          size: prepSize,
          fabric: prepFabric,
        },
      },
    ];
  });
  return Object.fromEntries(val);
}

function prepareOrder(order: Order): any {
  return {
    ...order,
    ...{
      cart: prepareCart(order.cart),
    },
  };
}

export function getEmailContent(order: Order): string {
  const orderP = prepareOrder(order);
  return renderJSONPlusCss(orderP);
}

const transporter = nodemailer.createTransport({
  host: "smtp.zoho.eu",
  secure: true,
  port: 465,
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS,
  },
});

export default async function postOrderHandler(
  req: NextApiRequest,
  res: NextApiResponse<void>
) {
  setTimeout(() => res.status(500).end(), 10000);
  return;
  const order: Order = JSON.parse(req.body);
  const emailContent = getEmailContent(order);
  const mailOptions = {
    from: "lika@pogladit-mozhno.com",
    to: "lika.gefest@gmail.com",
    subject: `Новый заказ от ${order.deliveryDetails.fullName}.`,
    html: emailContent,
  };

  try {
    await transporter.sendMail(mailOptions);
    res.status(201).end();
  } catch (err) {
    res.status(500).end();
  }
}
