import { NextApiRequest, NextApiResponse } from 'next'
import { connect, disconnect } from "../../../mongo/db"


type RegistrationData = {
  email: string,
  password: string
}

async function register(data: RegistrationData): Promise<boolean> {
  const db = connect()
  const isCreated = await db.UserModel.register(data.email, data.password)
  disconnect()
  return isCreated
}

export default async function registerHandler(req: NextApiRequest, res: NextApiResponse<void>) {
  const data: RegistrationData = JSON.parse(req.body)
  await register(data)
  if(register(data)) {
    res.end("Ok")
  } else {
    res.end("Registration error")
  }
}
