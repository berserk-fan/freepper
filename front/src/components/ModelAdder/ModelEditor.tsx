import React from "react";
import { Select } from "mui-rff";
import { Form } from "react-final-form";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Divider from "@mui/material/Divider";
import Typography from "@mui/material/Typography/Typography";
import { MenuItem } from "@mui/material";
import Button from "@mui/material/Button/Button";
import MuiTextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import CloseIcon from "@mui/icons-material/Close";
import { Category } from "apis/category.pb";
import Grid from "@mui/material/Grid";
import ModelUpdater from "./ModelUpdater";
import ProductPropsEditor from "./ProductPropsEditor";
import { ImageListController } from "./ImageListEditor";
import { useCategories } from "../../commons/swrHooks";
import ModelSelector from "./ModelSelector";
import ModelCreator from "./ModelCreator";
import SwrFallback from "../Swr/SwrFallback";
import LayoutWithHeaderAndFooter from "../Layout/LayoutWithHeaderAndFooter";

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

export default function ModelEditor() {
  const categories = useCategories();
  const [categoryName, setCategoryName] = React.useState<string | null>(null);
  const [modelName, setModelName] = React.useState<string | null>(null);
  const [imageListControllerDialog, setImageListControllerDialog] =
    React.useState(false);
  const dropModel = React.useCallback(() => setModelName(null), []);
  const closeImageListControllerDialog = React.useCallback(() => {
    setImageListControllerDialog(false);
  }, []);
  const openImageListControllerDialog = React.useCallback(() => {
    setImageListControllerDialog(true);
  }, []);

  return (
    <SwrFallback
      name="Categories"
      swrData={categories}
      main={() => (
        <Container>
          <Box minHeight="100vh" marginTop={4} padding={1} bgcolor="white">
            <Grid container spacing={1}>
              <Grid item xs={3}>
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
                <Typography variant="h4">Model Selector</Typography>
                <Form
                  onSubmit={(x) => setCategoryName(x.category_name)}
                  initialValues={{ category_name: categories.data[0].name }}
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
                          <MenuItem key={category.name} value={category.name}>
                            {category.displayName}
                          </MenuItem>
                        ))}
                      </Select>
                      <Button type="submit">Submit</Button>
                      <Button onClick={dropModel}>Reload model</Button>
                    </form>
                  )}
                />

                <Box width="100%" marginY={1}>
                  <Divider variant="fullWidth" />
                </Box>

                {categoryName && (
                  <ModelSelector
                    categoryName={categoryName}
                    onSelect={setModelName}
                  />
                )}

                <Box width="100%" marginY={1}>
                  <Divider variant="fullWidth" />
                </Box>

                {modelName ? (
                  <ModelUpdater modelName={modelName} />
                ) : (
                  <ModelCreator categoryName={categoryName} />
                )}

                <Box width="100%" marginY={1}>
                  <Divider variant="fullWidth" />
                </Box>

                <ImageListDialog
                  open={imageListControllerDialog}
                  onClose={closeImageListControllerDialog}
                />

                {modelName && <ProductPropsEditor modelName={modelName} />}
              </Grid>
            </Grid>
          </Box>
        </Container>
      )}
    />
  );
}
