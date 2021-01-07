import { NextApiRequest, NextApiResponse } from "next";
import { Order } from "../../order-model";
import { CartProduct } from "../checkout";

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
    return " " + renderPrimitive(obj);
  }
  for (let [key, value] of Object.entries(obj)) {
    retValue +=
      "<div class='tree'>" +
      indentation.repeat(depth) +
      `<span class="key">${key}</span>:`;
    retValue += renderJSON(value, depth + 1);
    retValue += "</div>";
  }
  return retValue;
}

export function getEmailContent(order: Order): string {
  const orderP = prepareOrder(order);
  const yaml = renderJSON(orderP);
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
            white-space: -pre-wrap;
            white-space: -o-pre-wrap;
            white-space: pre-wrap; 
            word-wrap: break-word; /* Internet Explorer 5.5+ */  
        }
        </style>
        <code>${yaml}</code>
        
    `;
}

export default function postOrderHandler(
  req: NextApiRequest,
  res: NextApiResponse<void>
) {
  const order: Order = JSON.parse(req.body);
  const emailContent = getEmailContent(order);
  console.log(emailContent);
  res.end("Ok");
}
