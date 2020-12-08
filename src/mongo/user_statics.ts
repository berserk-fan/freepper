import { IUserDocument, IUserModel } from "./user_types";

export async function register(
  this: IUserModel,
  email: string,
  password: string
): Promise<boolean> {
  const entryCount = await this.count({'email': email})
  if (entryCount == 1) {
    return false;
  } else {
    await this.create({'email': email, 'password': password})
    return true
  }
}

export async function authorize(
  this: IUserModel,
  email: string,
  password: string
): Promise<boolean> {
  const entryCount = await this.count({'email': email, 'password': password})
  return entryCount == 1
}
