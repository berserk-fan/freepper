import { UserDocument, UserModel } from "./user_types";

export async function register(
  this: UserModel,
  email: string,
  name: string,
  password: string
): Promise<boolean> {
  const entryCount = await this.count({'email': email})
  if (entryCount == 1) {
    return false;
  } else {
    await this.create({'name': name, 'email': email, 'password': password})
    return true
  }
}

export async function authorize(
  this: UserModel,
  email: string,
  password: string
): Promise<boolean> {
  const entryCount = await this.count({'email': email, 'password': password})
  return entryCount == 0
}
