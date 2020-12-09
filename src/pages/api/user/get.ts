import { NextApiRequest, NextApiResponse } from 'next'
import {dbClient} from "../../../mongo/run_db"
import {User, toDomainUser} from "./domain"


export default async function getHandler(req: NextApiRequest, res: NextApiResponse<User>) {
  const email: string = req.body.email
  let dbUser = await dbClient.userModel.findOne({'email': email})
  if (dbUser) {
    let user = toDomainUser(dbUser)
    res.json(user)
    res.end("ok")
  } else {
    res.status(404)
    res.end("Not found")
  }
}
