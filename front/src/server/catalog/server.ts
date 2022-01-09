import { NextApiRequest, NextApiResponse } from "next";
import { Category } from "apis/catalog";

export async function catalogApi(
  req: NextApiRequest,
  res: NextApiResponse,
): Promise<void> {
  console.log("Recieved qweqwe");
  const response: Category = {
    name: "hello",
    displayName: "world",
    id: "123",
    description: "qweqwe",
    image: {
      name: "qwe",
      src: "qwe",
      alt: "qwe",
    },
    products: [],
  };

  const qq = Category.encode(response).finish().buffer;
  const buf = Buffer.from(qq);
  console.log(buf.length);
  res.write(buf, "binary");
  res.end(null, "binary");
}
