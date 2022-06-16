import { Category } from "apis/category.pb";
import React, { useState } from "react";
import { TextField, Select, Autocomplete } from "mui-rff";
import Grid from "@material-ui/core/Grid";
import { Form } from "react-final-form";
import Container from "@material-ui/core/Container";
import Box from "@material-ui/core/Box";
import Divider from "@material-ui/core/Divider";
import Typography from "@material-ui/core/Typography/Typography";
import { DialogTitle, MenuItem, Radio as MuiRadio } from "@material-ui/core";
import { Model } from "apis/model.pb";
import Button from "@material-ui/core/Button/Button";
import { Product } from "apis/product.pb";
import Table from "@material-ui/core/Table";
import MuiTextField from "@material-ui/core/TextField";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Dialog from "@material-ui/core/Dialog";
import Paper from "@material-ui/core/Paper";
import { Parameter, ParameterList } from "apis/parameter.pb";
import { ImageList } from "apis/image_list.pb";
import CloseIcon from "@material-ui/icons/Close";
import parameterLists1 from "../../commons/parameterLists.json";
import grpcClient from "../../commons/shop-node";
import { indexProducts } from "../../commons/utils";
import Markdown from "../../components/Markdown/Renderers";
import SliderWithThumbs from "../../components/SliderWithThumbs";
import { SIZES } from "../../components/Shop/definitions";

type Matrix = {
  top: Parameter[];
  side: Parameter[];
};

function alreadyCreated(m: Model) {
  return !!m.name;
}

function ImageListController() {
  const [imageLists, setImageLists] = React.useState<ImageList[]>([]);
  React.useEffect(() => {
    grpcClient()
      .listImageLists({
        parent: "imageLists",
        pageSize: 1000,
      })
      .then((x) => setImageLists(x.imageLists));
  }, []);

  const indexed = React.useMemo(
    () => Object.fromEntries(imageLists.map((x) => [x.name, x])),
    [imageLists],
  );

  const autocompleteData = React.useMemo(
    () => imageLists.map((x) => ({ label: x.displayName, value: x.name })),
    [imageLists],
  );

  const [activeImageSrc, setActiveImageSrc] = React.useState("");

  const [currentList, setCurrentList] = React.useState(
    ImageList.fromPartial({}),
  );

  React.useEffect(() => {
    if (imageLists.length > 0 && !currentList.name) {
      setCurrentList(imageLists[0]);
    }
  }, [imageLists]);

  if (!currentList.name) {
    return <Typography>No image lists!(</Typography>;
  }

  function deleteImage() {
    setCurrentList((prev) => ({
      ...prev,
      images: prev.images.filter((x) => x.src !== activeImageSrc),
    }));
  }

  async function submitNewImageList() {
    const imageList = await grpcClient().updateImageList({
      imageList: currentList,
      updateMask: ["*"],
    });
    setCurrentList(imageList);
    alert(`ImageList updated: ${JSON.stringify(imageList)}`);
    const newLists = await grpcClient().listImageLists({
      parent: "imageLists",
      pageSize: 1000,
    });
    setImageLists(newLists.imageLists);
  }

  return (
    <Box maxWidth="500px">
      <Typography variant="subtitle1">Name: {currentList.name}</Typography>
      <Typography variant="h6">{currentList.displayName}</Typography>
      <Form
        onSubmit={(vals) => setCurrentList(indexed[vals.name])}
        initialValues={{ name: imageLists[0].name }}
        render={({ handleSubmit }) => (
          <Autocomplete
            label="Select an images"
            name="name"
            required
            color="secondary"
            options={autocompleteData}
            getOptionValue={(option) => option.value}
            getOptionLabel={(option) => option.label}
            onSelect={handleSubmit}
            renderOption={(option, { selected }) => (
              <>
                <MuiRadio style={{ marginRight: 8 }} checked={selected} />
                {option.label}
              </>
            )}
          />
        )}
      />
      <Box marginY={2}>
        <Divider />
        <Button variant="outlined" onClick={submitNewImageList}>
          Submit Image List
        </Button>
        <Button variant="outlined" onClick={deleteImage}>
          Delete Image
        </Button>
      </Box>
      <Box maxHeight="500px" maxWidth="500px">
        <SliderWithThumbs
          images={currentList.images}
          thumbs={currentList.images}
          sizes={SIZES}
          onChange={setActiveImageSrc}
        />
      </Box>
    </Box>
  );
}

export interface SetProductProdsdDialog {
  imageLists: ImageList[];
  parameters: Parameter[];
  open: boolean;
  onSubmit: (value: Partial<Product>) => void;
  onClose: () => void;
}

const parameterLists: ParameterList[] = parameterLists1;

function SetProductPropsDialog(props: SetProductProdsdDialog) {
  const { onSubmit, open, parameters, imageLists, onClose } = props;

  const imageListsSorted = React.useMemo(
    () => imageLists.sort((a, b) => a.displayName.localeCompare(b.displayName)),
    [imageLists],
  );
  const internalOnSubmit = React.useCallback(
    ({ price, imageListIdx }: { price: number; imageListIdx: number }) => {
      onSubmit({
        parameterIds: parameters.map((x) => x.uid),
        price: { standard: { amount: price } },
        imageList: imageListsSorted[imageListIdx],
      });
      onClose();
    },
    [onSubmit, onClose, imageListsSorted, parameters],
  );

  return (
    <Dialog open={open}>
      <Box margin={1}>
        <DialogTitle>Create product def</DialogTitle>
        <Box padding={2}>
          <Form
            initialValues={{}}
            onSubmit={internalOnSubmit}
            render={({ handleSubmit, values }) => (
              <form onSubmit={handleSubmit}>
                <TextField
                  name="price"
                  label="Enter product price"
                  variant="outlined"
                  color="secondary"
                  required
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
                <pre>{JSON.stringify(values)}</pre>
                <Button type="submit" variant="contained" fullWidth>
                  Submit
                </Button>
                <Button fullWidth variant="outlined" onClick={() => onClose()}>
                  Close
                </Button>
              </form>
            )}
          />
        </Box>
      </Box>
    </Dialog>
  );
}

function ImageListDialog({
  open,
  onClose,
}: {
  open: boolean;
  onClose: () => void;
}) {
  return (
    <Dialog open={open}>
      <Box margin={1} width="700px" height="100vh">
        <Box marginTop={1} marginLeft={1}>
          <Button onClick={onClose} variant="outlined">
            <CloseIcon />
          </Button>
        </Box>
        <ImageListController />
      </Box>
    </Dialog>
  );
}

export interface DeleteDialogProps {
  m: Model;
  open: boolean;
  onSubmit: () => void;
  onClose: () => void;
}

function DeleteDialog(props: DeleteDialogProps) {
  const { m, onSubmit, open, onClose } = props;

  return (
    <Dialog open={open}>
      <Box margin={1}>
        <DialogTitle>
          Are you sure you want to delete a model {m.displayName}
        </DialogTitle>
        <Button onClick={onSubmit}>Yes</Button>
        <Button onClick={onClose}>No</Button>
      </Box>
    </Dialog>
  );
}

export default function ModelAdder() {
  const [categories, setCategories] = React.useState<Category[]>([]);
  const [models, setModels] = React.useState<Model[]>([]);
  const [categoryName, setCategoryName] = React.useState<string | null>(null);
  const [modelBase, setModelBase] = React.useState<Model>(
    Model.fromPartial({}),
  );
  const [products, setProducts] = React.useState<Product[]>([]);
  const [indexed, setIndexed] = React.useState<Record<string, Product>>({});

  React.useEffect(() => {
    setIndexed(indexProducts(products));
  }, [products]);
  const [dialogParams, setDialogParams] = React.useState<Parameter[]>([]);
  const [deleteDialog, setDeleteDialog] = React.useState<boolean>(false);
  const [imageListControllerDialog, setImageListControllerDialog] =
    React.useState<boolean>(false);
  const [imageLists, setImageLists] = React.useState<ImageList[]>([]);

  const fetchProducts = React.useCallback(() => {
    grpcClient()
      .listProducts({
        parent: `${modelBase.name}/products`,
        pageSize: 1000,
      })
      .then((x) => setProducts(x.products));
  }, [modelBase]);

  const onProductSpec = React.useCallback(
    async (p: Product) => {
      await grpcClient().createProduct({
        parent: `${modelBase.name}/products`,
        product: p,
      });
      await fetchProducts();
    },
    [modelBase],
  );

  React.useEffect(() => {
    grpcClient()
      .listImageLists({
        parent: "imageLists",
        pageSize: 1000,
      })
      .then((x) => setImageLists(x.imageLists));
  }, []);

  React.useEffect(() => {
    grpcClient()
      .listCategories({
        parent: "categories",
        pageSize: 25,
      })
      .then((x) => setCategories(x.categories));
  }, []);

  const fetchModels = React.useCallback((catName) => {
    grpcClient()
      .listModels({
        parent: `${catName}/models`,
        pageSize: 100,
      })
      .then((x) => setModels(x.models));
  }, []);

  React.useEffect(() => {
    if (categoryName) {
      fetchModels(categoryName);
    }
  }, [categoryName]);

  React.useEffect(() => {
    if (alreadyCreated(modelBase)) {
      fetchProducts();
    }
  }, [modelBase]);

  function setupModel(modelName: string) {
    setModelBase(models.find((x) => x.name === modelName));
  }

  let cells: Matrix;
  if (modelBase.parameterLists.length === 2) {
    cells = {
      top: modelBase.parameterLists[0].parameters,
      side: modelBase.parameterLists[1].parameters,
    };
  } else {
    cells = {
      top: [],
      side: [],
    };
  }

  function setDialogarams(x: Parameter, y: Parameter) {
    setDialogParams((prev) => (prev.length === 0 ? [x, y] : []));
  }

  async function deleteProduct(name: string) {
    await grpcClient().deleteProduct({ name });
    await fetchProducts();
  }

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
      if (m.name) {
        grpcClient()
          .updateModel({
            model: newModel,
            updateMask: ["*"],
          })
          .then((res) => alert(`model updated:${JSON.stringify(res)}`))
          .catch((err) => alert(`model update failed: ${err}`));
      } else {
        grpcClient()
          .createModel({
            model: newModel,
            parent: `${categoryName}/models`,
          })
          .then((res) => {
            alert(`model created: ${JSON.stringify(res)}`);
            setModelBase(res);
            fetchModels(categoryName);
          })
          .catch((err) => alert(`model create failed: ${err}`));
      }
    },
    [categoryName],
  );

  const dropModelBase = React.useCallback(() => {
    setModelBase(Model.fromPartial({}));
  }, []);

  const deleteModel = React.useCallback(
    (m: Model) =>
      grpcClient()
        .deleteModel({ name: m.name })
        .then(() => dropModelBase()),
    [],
  );

  const closeDeleteDialog = React.useCallback(() => {
    setDeleteDialog(false);
  }, []);

  const openDeleteDialog = React.useCallback(() => {
    setDeleteDialog(true);
  }, []);

  const closeImageListControllerDialog = React.useCallback(() => {
    setImageListControllerDialog(false);
  }, []);

  const openImageListControllerDialog = React.useCallback(() => {
    setImageListControllerDialog(true);
  }, []);

  const deleteDialogSubmit = React.useCallback(() => {
    deleteModel(modelBase).then(() => fetchModels(categoryName));
    closeDeleteDialog();
  }, [modelBase, categoryName]);

  return (
    <Container>
      <Box display="flex">
        <Box
          height="100%"
          display="flex"
          flexDirection="column"
          justifyContent="space-between"
          marginX={2}
        >
          <Box flexDirection="column">
            <Typography variant="h6">Notes</Typography>
            <MuiTextField
              minRows={20}
              variant="outlined"
              color="secondary"
              multiline
            />
          </Box>

          <Box>
            <Button
              variant="contained"
              size="large"
              color="secondary"
              onClick={openImageListControllerDialog}
            >
              Images
            </Button>
          </Box>
        </Box>
        <Box marginX={2}>
          <Typography variant="h4">Model Selector</Typography>
          <Form
            onSubmit={(x) => setCategoryName(x.category_name)}
            initialValues={{ category_name: categories[0]?.name }}
            render={({ handleSubmit }) => (
              <form onSubmit={handleSubmit} noValidate>
                <Select
                  key="category_name"
                  name="category_name"
                  label="Category Id"
                  variant="outlined"
                  color="secondary"
                >
                  {categories.map((category) => (
                    <MenuItem key={category.name} value={category.name}>
                      {category.displayName}
                    </MenuItem>
                  ))}
                </Select>
                <Button type="submit">Submit</Button>
                <Button onClick={dropModelBase}>Reload model</Button>
              </form>
            )}
          />

          <Box width="100%" marginY={1}>
            <Divider variant="fullWidth" />
          </Box>

          {models.length === 0 ? (
            <></>
          ) : (
            <Form
              onSubmit={(values) => setupModel(values.model_name)}
              initialValues={{ model_name: models[0].name }}
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
                    {models.map((model) => (
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

          <Box width="100%" marginY={1}>
            <Divider variant="fullWidth" />
          </Box>

          <Form
            onSubmit={submitModel}
            initialValues={modelBase}
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
                        <Box
                          borderRadius="5px"
                          border={1}
                          borderColor="grey"
                          minWidth="500px"
                        >
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
                <Button type="submit">Submit</Button>
                {!alreadyCreated(modelBase) ? (
                  <></>
                ) : (
                  <Button onClick={openDeleteDialog}>Delete</Button>
                )}
              </form>
            )}
          />

          <Box width="100%" marginY={1}>
            <Divider variant="fullWidth" />
          </Box>

          <ImageListDialog
            open={imageListControllerDialog}
            onClose={closeImageListControllerDialog}
          />
          <DeleteDialog
            m={modelBase}
            open={deleteDialog}
            onSubmit={deleteDialogSubmit}
            onClose={closeDeleteDialog}
          />
          <SetProductPropsDialog
            open={dialogParams.length !== 0}
            onSubmit={onProductSpec}
            imageLists={imageLists}
            parameters={dialogParams}
            onClose={() => setDialogParams([])}
          />
          {!alreadyCreated(modelBase) ? (
            <></>
          ) : (
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
                                  onClick={() => deleteProduct(value.name)}
                                >
                                  Remove
                                </Button>
                              </>
                            ) : (
                              <Button onClick={() => setDialogarams(x, y)}>
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
          )}
        </Box>
      </Box>
    </Container>
  );
}
