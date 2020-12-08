import { model } from "mongoose";
import { IUserDocument, IUserModel } from "./user_types";
import UserSchema from "./user_schema";

export const UserModel = model<IUserDocument>("user", UserSchema) as IUserModel;
