import {NextApiRequest, NextApiResponse} from "next";
import {toRozetkaXml} from "../../rozetka";
import {categories, shopProducts} from "../../configs/Data";
import * as fs from "fs";

export default async function postOrderHandler(
    req: NextApiRequest,
    res: NextApiResponse<void>,
) {
    const xml = toRozetkaXml(categories, shopProducts);

    fs.writeFile('/Users/dmytriim/rozetka.xml', xml, err => {
        if (err) {
            console.error(err);
            return
        }
        //file written successfully
    });

    res.end()
}
