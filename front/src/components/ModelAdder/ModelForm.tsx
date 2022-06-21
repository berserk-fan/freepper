import { Model } from "apis/model.pb";
import React from "react";
import { ParameterList } from "apis/parameter.pb";
import { Form } from "react-final-form";
import Grid from "@material-ui/core/Grid";
import { Select, TextField } from "mui-rff";
import { MenuItem } from "@material-ui/core";
import Box from "@material-ui/core/Box";
import Typography from "@material-ui/core/Typography/Typography";
import Divider from "@material-ui/core/Divider";
import Button from "@material-ui/core/Button/Button";
import Markdown from "../Markdown/Renderers";
import parameterLists1 from "../../commons/parameterLists.json";

const parameterLists: ParameterList[] = parameterLists1;

export default function ModelForm({
  model,
  onSubmit,
  additionalButtons = [],
}: {
  model?: Model;
  onSubmit: (m: Model) => void;
  additionalButtons?: JSX.Element[];
}) {
  const submitModel = React.useCallback(
    (m: Model & { parameterIds?: string[] }) => {
      const newModel = !m.parameterIds
        ? m
        : {
            ...m,
            parameterLists: m.parameterIds.map((x) => ({
              ...ParameterList.fromPartial({}),
              uid: x,
            })),
          };
      onSubmit(newModel);
    },
    [],
  );

  return (
    <>
      <Form
        onSubmit={submitModel}
        initialValues={model || Model.fromPartial({})}
        render={({ handleSubmit, values }) => (
          <form onSubmit={handleSubmit} noValidate>
            <Grid
              container
              direction="column"
              alignContent="stretch"
              spacing={2}
            >
              <Grid item>
                <TextField
                  label="Model Display Name"
                  name="displayName"
                  required
                  variant="outlined"
                  color="secondary"
                />
              </Grid>
              <Grid item>
                <TextField
                  label="Readable Id"
                  name="readableId"
                  required
                  variant="outlined"
                  color="secondary"
                />
              </Grid>
              <Grid item>
                <Select
                  key="parameter_ids"
                  name="parameterIds"
                  label="Parameter Ids"
                  variant="outlined"
                  color="secondary"
                  multiple
                  disabled={!!values.name}
                >
                  {parameterLists.map((x) => (
                    <MenuItem key={x.uid} value={x.uid}>
                      {x.displayName}
                    </MenuItem>
                  ))}
                </Select>
              </Grid>
              <Grid item>
                <TextField
                  key="imageList.name"
                  name="imageList.name"
                  label="Image List"
                  variant="outlined"
                  color="secondary"
                />
              </Grid>
              <Grid item>
                <Grid container spacing={1}>
                  <Grid item xs={12} md={6}>
                    <TextField
                      multiline
                      label="Model Description"
                      name="description"
                      required
                      variant="outlined"
                      color="secondary"
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <Box borderRadius="5px" border={1} borderColor="grey">
                      <Typography variant="h6">Description Preview</Typography>
                      <Divider />
                      <Markdown>{values.description}</Markdown>
                    </Box>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
            <Button type="submit">Submit</Button>
            {additionalButtons}
          </form>
        )}
      />
    </>
  );
}
