import { NextApiRequest, NextApiResponse } from 'next'
import {dbClient} from "../../../mongo/run_db"

export type Credentials = {
  email: string
  password: string
}

async function login(cred: Credentials): Promise<boolean> {
  const authResult = await dbClient.userModel.authorize(cred.email, cred.password)
  return authResult
}

export default async function loginHandler(req: NextApiRequest, res: NextApiResponse<void>) {
  const creds: Credentials = req.body;
  const loginResult = await login(creds);
  if(loginResult) {
    res.end("Ok")
  } else {
    res.status(404);
    res.end("Not found")
  }
}
