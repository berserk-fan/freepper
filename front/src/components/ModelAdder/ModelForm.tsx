import { Model } from "apis/model.pb";
import React from "react";
import { ParameterList } from "apis/parameter.pb";
import { Form } from "react-final-form";
import Grid from "@mui/material/Grid";
import { Select, TextField } from "mui-rff";
import { MenuItem } from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import Button from "@mui/material/Button";
import Markdown from "../Markdown/Renderers";
import parameterLists1 from "../../commons/parameterLists.json";
import ImageListInput from "./ImageListInput";
import { useImageLists } from "../../commons/swrHooks";
import SwrFallback from "../Swr/SwrFallback";

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
  const imageLists = useImageLists();

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
    <SwrFallback
      name="images"
      swrData={imageLists}
      main={() => (
        <Form
          onSubmit={submitModel}
          initialValues={model || Model.fromPartial({})}
          render={({ handleSubmit, values }) => (
            <form onSubmit={handleSubmit} noValidate>
              <Box marginBottom={3} className="flex justify-between">
                <Button
                  size="large"
                  variant="contained"
                  color="secondary"
                  type="submit"
                >
                  Submit
                </Button>
                <Box>{additionalButtons}</Box>
              </Box>

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
                  <ImageListInput
                    key="imageList.name"
                    imageLists={imageLists.data}
                    name="imageList.name"
                    label="Image List"
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
                        <Typography variant="h6">
                          Description Preview
                        </Typography>
                        <Divider />
                        <Markdown>{values.description}</Markdown>
                      </Box>
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
            </form>
          )}
        />
      )}
    />
  );
}
