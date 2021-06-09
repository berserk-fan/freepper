import { NextApiRequest, NextApiResponse } from "next";
import * as fs from "fs";
import { toRozetkaXml } from "../../rozetka";
import { categories, shopProducts } from "../../configs/Data";

export default async function postOrderHandler(
  req: NextApiRequest,
  res: NextApiResponse<void>,
) {
  const xml = toRozetkaXml(categories, shopProducts);

  fs.writeFile("/Users/dmytriim/rozetka.xml", xml, (err) => {
    if (err) {
      console.error(err);
    }
    // file written successfully
  });

  res.end();
}
