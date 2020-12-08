import { Document, Model } from "mongoose";

export interface IUser {
  email: string;
  password: string;
}

export interface IUserDocument extends IUser, Document {
}

export interface IUserModel extends Model<IUserDocument> {
  register: (
    this: IUserModel,
    email: string,
    password: string
  ) => Promise<boolean>;
  authorize: (
    this: IUserModel,
    email: string,
    password: string
  ) => Promise<boolean>;
}
