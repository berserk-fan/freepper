import { Document, Model } from "mongoose";

export interface User {
  name: string
  email: string
  password: string
}

export interface UserDocument extends User, Document {
}

export interface UserModel extends Model<UserDocument> {
  register: (
    this: UserModel,
    email: string,
    password: string
  ) => Promise<boolean>;
  authorize: (
    this: UserModel,
    email: string,
    password: string
  ) => Promise<boolean>;
}
