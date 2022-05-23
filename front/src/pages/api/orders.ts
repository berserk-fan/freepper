import { NextApiRequest, NextApiResponse } from "next";
import nodemailer from "nodemailer";
import { Order } from "../../order-model";
import { CartProduct } from "../../store";

function renderPrimitive(p: any) {
  switch (typeof p) {
    case "string":
      return `<span class="string">"${p}"</span>`;
    case "number":
      return `<span class="number">${p}</span>`;
    default:
      return `${p}`;
  }
}

function renderJSON(obj: any, depth: number = 0, indentation: string = "  ") {
  let retValue = "";
  if (typeof obj !== "object") {
    return ` ${renderPrimitive(obj)}`;
  }
  Object.entries(obj).forEach((key, value) => {
    retValue += `<div class='tree'>${indentation.repeat(
      depth,
    )}<span class="key">${key}</span>:`;
    retValue += renderJSON(value, depth + 1);
    retValue += "</div>";
  });
  return retValue;
}

export function renderJSONPlusCss(obj: any) {
  const yaml = renderJSON(obj);
  return `
        <style>
        code > .tree {
            margin-left: 0
        }
        .key {
            color: orange;
        }
        .number {
            color: cornflowerblue;
        }
        .string {
            color: forestgreen;
        }
        code {
            white-space: -moz-pre-wrap;
            white-space: -o-pre-wrap;
            white-space: pre-wrap; 
            word-wrap: break-word; /* Internet Explorer 5.5+ */  
        }
        </style>
        <code>${yaml}</code>
        
    `;
}

function pick<T, K extends keyof T>(obj: T, ...keys: K[]): Pick<T, K> {
  const ret: any = {};
  keys.forEach((key) => {
    ret[key] = obj[key];
  });
  return ret;
}

function prepareCart(cart: Record<string, CartProduct>): any {
  const val = Object.entries(cart).map(([id, { count, product, model }]) => [
    id,
    {
      ...{
        displayName: model.displayName,
        count,
      },
      ...{
        price: product.price,
        parameters: product.parameterIds,
      },
    },
  ]);
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
  res: NextApiResponse<void>,
) {
  const order: Order = JSON.parse(req.body);
  const emailContent = getEmailContent(order);
  const mailOptions = {
    from: "lika@pogladit-mozhno.com",
    to: "lika.gefest@gmail.com",
    subject: `Новый заказ от ${order.deliveryDetails.fullName}.`,
    html: emailContent,
  };

  console.log(process.env.EMAIL_USER, " ", process.env.EMAIL_USER);
  try {
    await transporter.sendMail(mailOptions);
    res.status(201).end();
  } catch (err) {
    console.error(err);
    res.status(500).end();
  }
}
