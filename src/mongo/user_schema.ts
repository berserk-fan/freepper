import { Schema } from "mongoose";
import { register, authorize } from "./user_statics"

const UserSchema = new Schema({
  email: String,
  password: String,
});

UserSchema.statics.register = register
UserSchema.statics.authorize = authorize

export default UserSchema;
