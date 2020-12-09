import * as Mongoose from "mongoose";
import {userModel} from "./user_model";

export let database: Mongoose.Connection;

export const connect = () => {
  const uri = "mongodb+srv://app351904:lxc6lspWNmzFW8PA@dc1.o1p2z.mongodb.net/test?retryWrites=true&w=majority"
  if (database) {
    return;
  }
  Mongoose.connect(uri, {
    useNewUrlParser: true,
    useFindAndModify: true,
    useUnifiedTopology: true,
    useCreateIndex: true,
  });
  database = Mongoose.connection;
  database.once("open", async () => {
    console.log("Connected to database");
  });
  database.on("error", () => {
    console.log("Error connecting to database");
  });

  return {
    userModel,
  };
};

export const disconnect = () => {
  if (!database) {
    return;
  }
  Mongoose.disconnect();
};
