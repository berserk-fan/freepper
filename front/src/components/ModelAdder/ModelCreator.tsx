import { Model } from "apis/model.pb";
import React from "react";
import grpcClient from "../../commons/shopClient";
import ModelForm from "./ModelForm";
import { useToken } from "../../commons/swrHooks";

function onSubmit(categoryName: string, m: Model, token: string) {
  return grpcClient()
    .createModel({
      model: m,
      parent: `${categoryName}/models`,
    })
    .then((res) => {
      alert(`model created: ${JSON.stringify(res)}`);
    })
    .catch((err) => alert(`model create failed: ${err}`));
}

export default function ModelCreator({
  categoryName,
}: {
  categoryName: string;
}) {
  const token = useToken();
  return (
    <ModelForm
      model={null}
      onSubmit={(m) => onSubmit(categoryName, m, token)}
    />
  );
}
