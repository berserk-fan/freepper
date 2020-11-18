import { NextApiRequest, NextApiResponse } from 'next'
import {Order} from "../../order-model";


function getEmailContent(order: Order): string {
    return `
        <div>Hello world</div>
    `
}

export default function postOrderHandler(req: NextApiRequest, res: NextApiResponse<void>) {
    const order: Order = JSON.parse(req.body)
    const emailContent = getEmailContent(order)
    console.log(emailContent)
    res.end("Ok")
}
