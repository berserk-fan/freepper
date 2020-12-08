import { NextApiRequest, NextApiResponse } from 'next'
import { connect, disconnect } from "../../../mongo/db"

export type Credentials = {
  email: string
  password: string
}

async function login(cred: Credentials): Promise<boolean> {
  const db = connect();
  const authResult = await db.UserModel.authorize(cred.email, cred.password)
  disconnect()
  return authResult
}

export default async function loginHandler(req: NextApiRequest, res: NextApiResponse<void>) {
  const creds: Credentials = JSON.parse(req.body)
  const loginResult = await login(creds)
  if(loginResult) {
    res.end("Ok")
  } else {
    res.end("Unauthorized")
  }
}
