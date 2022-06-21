import { Form } from "react-final-form";
import { Select } from "mui-rff";
import { MenuItem } from "@material-ui/core";
import Button from "@material-ui/core/Button/Button";
import React from "react";
import { useModels } from "../../commons/swrHooks";
import SwrFallback from "../Swr/SwrFallback";

export default function ModelSelector({
  categoryName,
  onSelect,
}: {
  categoryName: string;
  onSelect: (modelName: string) => void;
}) {
  const models = useModels(categoryName);

  return (
    <SwrFallback
      name="models"
      swrData={models}
      main={() => (
        <Form
          onSubmit={(values) => onSelect(values.model_name)}
          initialValues={{ model_name: models.data[0].name }}
          render={({ handleSubmit }) => (
            <form onSubmit={handleSubmit} noValidate>
              <Select
                key="model_name"
                name="model_name"
                label="Model Name"
                formControlProps={{ margin: "normal" }}
                variant="outlined"
                color="secondary"
              >
                {models.data.map((model) => (
                  <MenuItem key={model.uid} value={model.name}>
                    {model.displayName}
                  </MenuItem>
                ))}
              </Select>
              <Button type="submit">Submit</Button>
            </form>
          )}
        />
      )}
    />
  );
}
