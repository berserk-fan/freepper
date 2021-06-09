import { NextApiRequest, NextApiResponse } from "next";
import { toRozetkaXml } from "../../rozetka";
import { categories, shopProducts } from "../../configs/Data";

export default async function postOrderHandler(
  req: NextApiRequest,
  res: NextApiResponse<string>,
) {
  if(req.headers.authorization != "aezakmiaezakmiaezakmiaezakmi") {
     return res.status(403).end();
  }
  return res.send(toRozetkaXml(categories, shopProducts));
}
