import React from "react";
import { Select } from "mui-rff";
import { Form } from "react-final-form";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { MenuItem } from "@mui/material";
import Button from "@mui/material/Button";
import MuiTextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import Grid from "@mui/material/Grid";
import ModelUpdater from "./ModelUpdater";
import ProductPropsEditor from "./ProductPropsEditor";
import { ImageListEditor } from "./ImageListEditor";
import { useCategories } from "../../commons/swrHooks";
import ModelSelector from "./ModelSelector";
import ModelCreator from "./ModelCreator";
import SwrFallback from "../Swr/SwrFallback";
import ImageEditor from "./ImageEditor";
import Spacing from "../Commons/Spacing";

function ImageListDialog({
  open,
  onClose,
}: {
  open: boolean;
  onClose: () => void;
}) {
  return (
    <Dialog open={open} onClose={onClose}>
      <Box margin={2} height="100vh">
        <ImageListEditor />
      </Box>
    </Dialog>
  );
}

function ImageDialog({
  open,
  onClose,
}: {
  open: boolean;
  onClose: () => void;
}) {
  return (
    <Dialog open={open} onClose={onClose}>
      <Box margin={1} minWidth="500px" minHeight="100vh">
        <ImageEditor />
      </Box>
    </Dialog>
  );
}

function getModelForm(modelName: string | null, categoryName: string | null) {
  if (modelName) {
    return <ModelUpdater modelName={modelName} />;
  }
  if (categoryName) {
    return <ModelCreator categoryName={categoryName} />;
  }
  return "Category name unspecified!";
}

export default function ModelEditor() {
  const categories = useCategories();
  const [categoryName, setCategoryName] = React.useState<string | null>(null);
  const [modelName, setModelName] = React.useState<string | null>(null);
  const [imageEditorDialog, setImageEditorDialog] = React.useState(false);
  const closeImageEditorDialog = React.useCallback(() => {
    setImageEditorDialog(false);
  }, []);
  const openImageEditorDialog = React.useCallback(() => {
    setImageEditorDialog(true);
  }, []);

  const [imageListControllerDialog, setImageListControllerDialog] =
    React.useState(false);
  const closeImageListControllerDialog = React.useCallback(() => {
    setImageListControllerDialog(false);
  }, []);
  const openImageListEditorDialog = React.useCallback(() => {
    setImageListControllerDialog(true);
  }, []);
  const dropModel = React.useCallback(() => setModelName(null), []);

  return (
    <SwrFallback
      name="Categories"
      swrData={categories}
      main={() => (
        <Container>
          <ImageListDialog
            open={imageListControllerDialog}
            onClose={closeImageListControllerDialog}
          />
          <ImageDialog
            open={imageEditorDialog}
            onClose={closeImageEditorDialog}
          />
          <Box minHeight="100vh" marginTop={4} padding={1} bgcolor="white">
            <Grid container spacing={1}>
              <Grid item xs={3}>
                <Box>
                  <Button
                    variant="outlined"
                    size="large"
                    color="secondary"
                    onClick={openImageListEditorDialog}
                  >
                    Image Lists
                  </Button>
                </Box>
                <Box>
                  <Button
                    variant="outlined"
                    size="large"
                    color="secondary"
                    onClick={openImageEditorDialog}
                  >
                    Images
                  </Button>
                </Box>
                <Box>
                  <Typography variant="h6">Notes</Typography>
                  <MuiTextField
                    fullWidth
                    minRows={20}
                    variant="outlined"
                    color="secondary"
                    multiline
                  />
                </Box>
              </Grid>
              <Grid item xs={9}>
                <Spacing spacing={2} direction="column">
                  <Box>
                    <Typography variant="h4">Category Name Selector</Typography>
                    <Form
                      onSubmit={(x) => setCategoryName(x.category_name)}
                      render={({ handleSubmit }) => (
                        <form onSubmit={handleSubmit} noValidate>
                          <Select
                            key="category_name"
                            name="category_name"
                            label="Category Id"
                            variant="outlined"
                            color="secondary"
                          >
                            {categories.data.map((category) => (
                              <MenuItem
                                key={category.name}
                                value={category.name}
                              >
                                {category.displayName}
                              </MenuItem>
                            ))}
                          </Select>
                          <Button
                            variant="outlined"
                            color="secondary"
                            type="submit"
                          >
                            Submit
                          </Button>
                          <Button
                            variant="outlined"
                            color="secondary"
                            onClick={dropModel}
                          >
                            Reload model
                          </Button>
                        </form>
                      )}
                    />
                  </Box>

                  {categoryName && (
                    <Box>
                      <Typography variant="h4">Model Selector</Typography>
                      <ModelSelector
                        categoryName={categoryName}
                        onSelect={setModelName}
                      />
                    </Box>
                  )}

                  <Box>
                    <Typography variant="h4">Model form</Typography>
                    {getModelForm(modelName, categoryName)}
                  </Box>

                  {modelName && <ProductPropsEditor modelName={modelName} />}
                </Spacing>
              </Grid>
            </Grid>
          </Box>
        </Container>
      )}
    />
  );
}
