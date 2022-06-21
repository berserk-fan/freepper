import { Model } from "apis/model.pb";

export function alreadyCreated(m: Model) {
  return !!m.name;
}
