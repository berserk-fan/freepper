import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import Button from "@mui/material/Button";
import TableContainer from "@mui/material/TableContainer";
import React from "react";
import { Parameter } from "apis/parameter.pb";
import { ImageList } from "apis/image_list.pb";
import { Product } from "apis/product.pb";
import Dialog from "@mui/material/Dialog";
import Box from "@mui/material/Box";
import DialogTitle from "@mui/material/DialogTitle";
import { Form } from "react-final-form";
import { TextField } from "mui-rff";
import ButtonGroup from "@mui/material/ButtonGroup";
import InputAdornment from "@mui/material/InputAdornment";
import grpcClient from "../../commons/shopClient";
import { indexProducts } from "../../commons/utils";
import { useImageLists, useModel, useProducts } from "../../commons/swrHooks";
import SwrFallback from "../Swr/SwrFallback";
import ImageListInput from "./ImageListInput";

export type Matrix = {
  dim1?: Parameter[];
  dim2?: Parameter[];
};

export interface SetProductPropsdDialog {
  imageLists: ImageList[];
  open: boolean;
  onSubmit: (value: { price: number; imageList: ImageList }) => void;
  onClose: () => void;
}

function SetProductPropsDialog(props: SetProductPropsdDialog) {
  const { onSubmit, open, imageLists, onClose } = props;
  const internalSubmit = ({
    price,
    imageListName,
  }: {
    price: number;
    imageListName: string;
  }) => {
    onSubmit({
      price,
      imageList: imageLists.find((x) => x.name === imageListName),
    });
  };

  return (
    <Dialog open={open}>
      <Box margin={1} minWidth="500px">
        <DialogTitle>Create product def</DialogTitle>
        <Box padding={2}>
          <Form
            initialValues={{}}
            onSubmit={internalSubmit}
            render={({ handleSubmit }) => (
              <form onSubmit={handleSubmit}>
                <TextField
                  name="price"
                  label="Enter product price"
                  variant="outlined"
                  color="secondary"
                  required
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">Гривен</InputAdornment>
                    ),
                  }}
                />
                <ImageListInput
                  name="imageListName"
                  label="Select image list"
                  color="secondary"
                  imageLists={imageLists}
                  required
                />
                <ButtonGroup orientation="vertical" fullWidth color="secondary">
                  <Button type="submit" variant="contained" fullWidth>
                    Submit
                  </Button>
                  <Button
                    fullWidth
                    variant="outlined"
                    onClick={() => onClose()}
                  >
                    Close
                  </Button>
                </ButtonGroup>
              </form>
            )}
          />
        </Box>
      </Box>
    </Dialog>
  );
}

function ProductCell({
  value,
  onDelete,
  onAdd,
}: {
  value?: Product;
  onDelete: (name: string) => void;
  onAdd: () => void;
}) {
  return (
    <TableCell>
      {value ? (
        <>
          {value.price.standard?.amount}
          ---
          {value.imageList.displayName}
          <Button
            variant="outlined"
            color="secondary"
            onClick={() => onDelete(value.name)}
          >
            Remove
          </Button>
        </>
      ) : (
        <Button variant="outlined" color="secondary" onClick={() => onAdd()}>
          Add
        </Button>
      )}
    </TableCell>
  );
}

export default function ProductPropsEditor({
  modelName,
}: {
  modelName: string;
}) {
  const model = useModel(modelName);
  const products = useProducts(`${modelName}`);
  const imageLists = useImageLists();

  const cells = React.useMemo<Matrix>(
    () => ({
      dim1: model?.data?.parameterLists[0]?.parameters,
      dim2: model?.data?.parameterLists[1]?.parameters,
    }),
    [model.data],
  );

  const [dialogParams, setDialogParams] = React.useState<Parameter[]>([]);

  const [indexed, setIndexed] = React.useState<Record<string, Product>>({});
  React.useEffect(() => {
    if (products.data) {
      setIndexed(indexProducts(products.data));
    }
  }, [products.data]);

  const onClose = React.useCallback(() => {
    setDialogParams([]);
  }, [setDialogParams]);

  const onProductSpec = React.useCallback(
    async (p: Product) => {
      await grpcClient().createProduct({
        parent: `${modelName}/products`,
        product: p,
      });
      await products.mutate();
    },
    [modelName],
  );

  const internalOnSubmit = React.useCallback(
    ({ price, imageList }: { price: number; imageList: ImageList }) => {
      onProductSpec({
        name: "",
        uid: "",
        displayName: "",
        modelId: "",
        parameterIds: dialogParams.map((x) => x.uid),
        price: { standard: { amount: price } },
        imageList,
      }).catch((err) => alert(err.message));
      onClose();
    },
    [onProductSpec, onClose, imageLists, dialogParams],
  );

  function setDialogParams1(xs: Parameter[]) {
    setDialogParams((prev) => (prev.length === 0 ? xs : []));
  }

  const deleteProduct = async (name: string) => {
    await grpcClient().deleteProduct({ name });
    await products.mutate();
  };

  return (
    <SwrFallback
      name="model or products or imagelists"
      swrData={[products, model, imageLists]}
      allowEmpty
      main={() => (
        <>
          <SetProductPropsDialog
            open={dialogParams.length !== 0}
            onSubmit={internalOnSubmit}
            onClose={() => setDialogParams([])}
            imageLists={imageLists.data}
          />
          <TableContainer component={Paper}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>***</TableCell>
                  {cells.dim1.map((x) => (
                    <TableCell key={x.uid}>{x.displayName}</TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {cells.dim2 ? (
                  cells.dim2.map((y) => (
                    <TableRow key={y.uid}>
                      <TableCell>{y.displayName}</TableCell>
                      {cells.dim1.map((x) => {
                        const value =
                          indexed[
                            [x.uid, y.uid]
                              .sort((a, b) => a.localeCompare(b))
                              .join()
                          ];

                        return (
                          <ProductCell
                            key={x.uid + y.uid}
                            onDelete={deleteProduct}
                            onAdd={() => setDialogParams1([x, y])}
                            value={value}
                          />
                        );
                      })}
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell>Values</TableCell>
                    {cells.dim1.map((x) => (
                      <ProductCell
                        key={x.uid}
                        value={indexed[x.uid]}
                        onDelete={deleteProduct}
                        onAdd={() => setDialogParams1([x])}
                      />
                    ))}
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}
    />
  );
}
