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
import { DialogTitle, MenuItem } from "@mui/material";
import { Form } from "react-final-form";
import { Select, TextField } from "mui-rff";
import ButtonGroup from "@mui/material/ButtonGroup";
import InputAdornment from "@mui/material/InputAdornment";
import grpcClient from "../../commons/shopClient";
import { indexProducts } from "../../commons/utils";
import { useImageLists, useModel, useProducts } from "../../commons/swrHooks";
import SwrFallback from "../Swr/SwrFallback";

export type Matrix = {
  top: Parameter[];
  side: Parameter[];
};

export interface SetProductPropsdDialog {
  imageListsSorted: ImageList[];
  open: boolean;
  onSubmit: (value: { price: number; imageListIdx: number }) => void;
  onClose: () => void;
}

function SetProductPropsDialog(props: SetProductPropsdDialog) {
  const { onSubmit, open, imageListsSorted, onClose } = props;

  return (
    <Dialog open={open}>
      <Box margin={1}>
        <DialogTitle>Create product def</DialogTitle>
        <Box padding={2}>
          <Form
            initialValues={{}}
            onSubmit={onSubmit}
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
                <Select
                  name="imageListIdx"
                  label="Select image list"
                  formControlProps={{ margin: "normal" }}
                  variant="outlined"
                  color="secondary"
                  required
                >
                  {imageListsSorted.map((x, idx) => (
                    <MenuItem key={x.name} value={idx}>
                      {x.displayName}
                    </MenuItem>
                  ))}
                </Select>
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

export default function ProductPropsEditor({
  modelName,
}: {
  modelName: string;
}) {
  const model = useModel(modelName);
  const products = useProducts(`${modelName}`);
  const imageLists = useImageLists();

  const cells = React.useMemo<Matrix>(() => {
    if (model.data && model.data.parameterLists.length === 2) {
      return {
        top: model.data.parameterLists[0].parameters,
        side: model.data.parameterLists[1].parameters,
      };
    }
    return {
      top: [],
      side: [],
    };
  }, [model.data]);

  const [dialogParams, setDialogParams] = React.useState<Parameter[]>([]);

  const imageListsSorted = React.useMemo(() => {
    if (imageLists.data) {
      return imageLists.data.sort((a, b) =>
        a.displayName.localeCompare(b.displayName),
      );
    }
    return [];
  }, [imageLists.data]);

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
    ({ price, imageListIdx }: { price: number; imageListIdx: number }) => {
      onProductSpec({
        name: "",
        uid: "",
        displayName: "",
        modelId: "",
        parameterIds: dialogParams.map((x) => x.uid),
        price: { standard: { amount: price } },
        imageList: imageListsSorted[imageListIdx],
      });
      onClose();
    },
    [onProductSpec, onClose, imageListsSorted, dialogParams],
  );

  function setDialogParams1(x: Parameter, y: Parameter) {
    setDialogParams((prev) => (prev.length === 0 ? [x, y] : []));
  }

  async function deleteProduct(name: string) {
    await grpcClient().deleteProduct({ name });
    await products.mutate();
  }

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
            imageListsSorted={imageListsSorted}
          />
          <TableContainer component={Paper}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>***</TableCell>
                  {cells.top.map((x) => (
                    <TableCell key={x.uid}>{x.displayName}</TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {cells.side.map((y) => (
                  <TableRow key={y.uid}>
                    <TableCell>{y.displayName}</TableCell>
                    {cells.top.map((x) => {
                      const value =
                        indexed[
                          [x.uid, y.uid]
                            .sort((a, b) => a.localeCompare(b))
                            .join()
                        ];

                      return (
                        <TableCell key={x.uid + y.uid}>
                          {value ? (
                            <>
                              {value.price.standard?.amount}
                              ---
                              {value.imageList.displayName}
                              <Button
                                variant="outlined"
                                color="secondary"
                                onClick={() => deleteProduct(value.name)}
                              >
                                Remove
                              </Button>
                            </>
                          ) : (
                            <Button
                              variant="outlined"
                              color="secondary"
                              onClick={() => setDialogParams1(x, y)}
                            >
                              Add
                            </Button>
                          )}
                        </TableCell>
                      );
                    })}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}
    />
  );
}
