import { NextApiRequest, NextApiResponse } from 'next'
import { disconnect } from "../../../mongo/db"
import {dbClient} from "../../../mongo/run_db"


interface RegistrationData {
  email: string;
  password: string;
}

async function register(data: RegistrationData): Promise<boolean> {
  const isCreated = await dbClient.UserModel.register(data.email, data.password)
  return isCreated
}

export default async function registerHandler(req: NextApiRequest, res: NextApiResponse<void>) {
  const data: RegistrationData = req.body
  const isRegistered = await register(data)
  if(isRegistered) {
    res.end("Ok")
  } else {
    res.end("Registration error")
  }
}
