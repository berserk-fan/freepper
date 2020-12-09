import {User as DBUser} from  "../../../mongo/user_types";

export type User = {
  name: string
  email: string
  password: string
}

export function toDomainUser(user: DBUser): User {
  return {name: user.name, email: user.email, password: user.password}
}
