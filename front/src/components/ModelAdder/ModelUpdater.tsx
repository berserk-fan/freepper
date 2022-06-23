import { DialogTitle } from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button/Button";
import React from "react";
import { Model } from "apis/model.pb";
import Dialog from "@mui/material/Dialog";
import grpcClient from "../../commons/shopClient";
import { useModel } from "../../commons/swrHooks";
import ModelForm from "./ModelForm";
import SwrFallback from "../Swr/SwrFallback";

function onSubmit1(m: Model) {
  grpcClient()
    .updateModel({
      model: m,
      updateMask: ["*"],
    })
    .then((res) => alert(`model updated:${JSON.stringify(res)}`))
    .catch((err) => alert(`model update failed: ${err}`));
}

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
        <Button onClick={onSubmit}>Yes</Button>
        <Button onClick={onClose}>No</Button>
      </Box>
    </Dialog>
  );
}

export default function ModelUpdater1({ modelName }: { modelName: string }) {
  const model = useModel(modelName);
  const [deleteDialog, setDeleteDialog] = React.useState(false);

  const deleteModel = React.useCallback(
    () => grpcClient().deleteModel({ name: model.data.name }),
    [model?.data?.name],
  );

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

  const button = <Button onClick={openDeleteDialog}>Delete</Button>;

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
