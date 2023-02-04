import { DialogTitle } from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import React from "react";
import { Model } from "apis/model.pb";
import Dialog from "@mui/material/Dialog";
import { useSWRConfig } from "swr";
import grpcClient from "../../commons/shopClient";
import { useModel } from "../../commons/swrHooks";
import ModelForm from "./ModelForm";
import SwrFallback from "../Swr/SwrFallback";

interface DeleteDialogProps {
  displayName: string;
  open: boolean;
  onSubmit: () => void;
  onClose: () => void;
}

function DeleteDialog(props: DeleteDialogProps) {
  const { displayName, onSubmit, open, onClose } = props;

  return (
    <Dialog open={open}>
      <Box margin={1}>
        <DialogTitle>
          Are you sure you want to delete a model {displayName}
        </DialogTitle>
        <Button color="secondary" onClick={onSubmit}>
          Yes
        </Button>
        <Button color="secondary" onClick={onClose}>
          No
        </Button>
      </Box>
    </Dialog>
  );
}

export default function ModelUpdater1({ modelName }: { modelName: string }) {
  const model = useModel(modelName);
  const { mutate } = useSWRConfig();
  const [deleteDialog, setDeleteDialog] = React.useState(false);

  const onSubmit1 = React.useCallback((m: Model) => {
    grpcClient()
      .updateModel({
        model: m,
        updateMask: ["*"],
      })
      .then((res) => alert(`model updated:${JSON.stringify(res)}`))
      .catch((err) => alert(`model update failed: ${err}`));
  }, []);

  const deleteModel = React.useCallback(async () => {
    try {
      await grpcClient().deleteModel({ name: model.data.name });
      await mutate(modelName.slice(0, modelName.lastIndexOf("/")));
    } catch (e) {
      alert(
        `failed to delete model ${model.data.displayName}. Reason: ${e.message}`,
      );
    }
  }, [model?.data?.name]);

  const openDeleteDialog = React.useCallback(() => {
    setDeleteDialog(true);
  }, []);

  const closeDeleteDialog = React.useCallback(() => {
    setDeleteDialog(false);
  }, []);

  const deleteDialogSubmit = React.useCallback(() => {
    deleteModel().then(() => model.mutate());
    closeDeleteDialog();
  }, [deleteModel]);

  const button = (
    <Button
      size="large"
      color="warning"
      variant="outlined"
      onClick={openDeleteDialog}
    >
      Delete
    </Button>
  );

  return (
    <SwrFallback
      name={modelName}
      swrData={model}
      main={() => (
        <>
          <DeleteDialog
            displayName={model.data.displayName}
            open={deleteDialog}
            onSubmit={deleteDialogSubmit}
            onClose={closeDeleteDialog}
          />
          <ModelForm
            model={model.data}
            onSubmit={onSubmit1}
            additionalButtons={[button]}
          />
        </>
      )}
    />
  );
}
