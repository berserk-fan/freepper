import {model, modelNames, models} from "mongoose";
import { UserDocument, UserModel } from "./user_types";
import UserSchema from "./user_schema";

export const userModel = modelNames().find(_ => _ === "user")
    ? models["user"] as UserModel
    : model<UserDocument>("user", UserSchema) as UserModel;
