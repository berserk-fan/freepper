import { model } from "mongoose";
import { UserDocument, UserModel } from "./user_types";
import UserSchema from "./user_schema";

export const userModel = model<UserDocument>("user", UserSchema) as UserModel;
