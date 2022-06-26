import React from "react";
import { ImageList } from "apis/image_list.pb";
import { TextField, Autocomplete } from "mui-rff";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Form } from "react-final-form";
import Button from "@mui/material/Button";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import MuiRadio from "@mui/material/Radio";
import ListItemText from "@mui/material/ListItemText";
import { Image as MyImage } from "apis/image.pb";
import DeleteIcon from "@mui/icons-material/Delete";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ArrowUpward from "@mui/icons-material/ArrowUpward";
import ArrowDownward from "@mui/icons-material/ArrowDownward";
import IconButton from "@mui/material/IconButton";
import ButtonGroup from "@mui/material/ButtonGroup";
import Tab from "@mui/material/Tab";
import Tabs from "@mui/material/Tabs";
import Divider from "@mui/material/Divider";

import { FormApi } from "final-form";
import { Dialog } from "@mui/material";
import Spacing from "../Commons/Spacing";
import grpcClient from "../../commons/shopClient";
import { useImageLists } from "../../commons/swrHooks";
import SwrFallback from "../Swr/SwrFallback";
import { MyAvatar } from "../Commons/MyAvatar";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      {...other}
    >
      {value === index && children}
    </div>
  );
}

function ImageListCreator() {
  const createImage = React.useCallback(
    async (
      imageList: ImageList,
      form: FormApi<ImageList, Partial<ImageList>>,
    ) => {
      try {
        const res = await grpcClient().createImageList({
          parent: "imageLists",
          imageList: { ...imageList, images: [] },
        });
        form.reset({});
        alert(`Created image sucessfully. ${JSON.stringify(res)}`);
      } catch (e) {
        alert(`Error creating image list: ${e.message}`);
        throw e;
      }
    },
    [],
  );

  return (
    <Form<ImageList>
      onSubmit={createImage}
      render={({ handleSubmit }) => (
        <form onSubmit={handleSubmit}>
          <TextField
            label="Image Display Name"
            helperText="Enter a display name of the image list. In English"
            name="displayName"
            variant="outlined"
            color="secondary"
            required
          />
          <Button variant="contained" color="secondary" type="submit">
            Create image list
          </Button>
        </form>
      )}
    />
  );
}

function ImageControls({
  image,
  onArrowUp,
  onArrowDown,
  onDelete,
}: {
  image: MyImage;
  onArrowUp: (name: string) => void;
  onArrowDown: (name: string) => void;
  onDelete: (name: string) => void;
}) {
  return (
    <ListItem>
      <ListItemAvatar>
        <Box margin={0.25}>
          <MyAvatar variant="huge" image={image} />
        </Box>
      </ListItemAvatar>
      <ListItemText primary={image.src} secondary={image.alt} />
      <ButtonGroup>
        <IconButton size="large" onClick={() => onArrowUp(image.name)}>
          <ArrowUpward />
        </IconButton>
        <IconButton size="large" onClick={() => onArrowDown(image.name)}>
          <ArrowDownward />
        </IconButton>
        <IconButton
          size="large"
          color="warning"
          onClick={() => onDelete(image.name)}
        >
          <DeleteIcon />
        </IconButton>
      </ButtonGroup>
    </ListItem>
  );
}

export function ImageListEditor() {
  const imageLists1 = useImageLists();
  const indexed = React.useMemo(
    () =>
      imageLists1.data
        ? Object.fromEntries(imageLists1.data.map((x) => [x.name, x]))
        : {},
    [imageLists1.data],
  );

  const autocompleteData: { label: string; value: string }[] = React.useMemo(
    () =>
      imageLists1.data
        ? imageLists1.data.map((x) => ({
            label: x.displayName,
            value: x.name,
          }))
        : [],
    [imageLists1.data],
  );

  const [currentList, setCurrentList] = React.useState<ImageList | null>(null);

  // React.useEffect(() => {
  //   if (!currentList && imageLists1?.data?.length > 0) {
  //     setCurrentList(imageLists1.data[0]);
  //   }
  // }, [imageLists1.data]);

  const deleteImage = React.useCallback((name: string) => {
    setCurrentList((prev) => ({
      ...prev,
      images: prev.images.filter((x) => x.name !== name),
    }));
  }, []);

  const liftImage = React.useCallback((name: string) => {
    setCurrentList((prev) => {
      const images = [...prev.images];
      const curIdx = images.findIndex((x) => x.name === name);
      const newIdx = Math.max(curIdx - 1, 0);
      const tmp = images[newIdx];
      images[newIdx] = images[curIdx];
      images[curIdx] = tmp;
      return { ...prev, images };
    });
  }, []);

  const dumpImage = React.useCallback((name: string) => {
    setCurrentList((prev) => {
      const images = [...prev.images];
      const curIdx = images.findIndex((x) => x.name === name);
      const newIdx = Math.min(curIdx + 1, images.length - 1);
      const tmp = images[newIdx];
      images[newIdx] = images[curIdx];
      images[curIdx] = tmp;
      return { ...prev, images };
    });
  }, []);

  const submitNewImageList = React.useCallback(async () => {
    const imageList = await grpcClient().updateImageList({
      imageList: currentList,
      updateMask: ["*"],
    });
    setCurrentList(imageList);
    alert(`ImageList updated: ${JSON.stringify(imageList)}`);
  }, [currentList]);


  const [deleteDialogOpen, setDeleteDialogOpen] = React.useState(false);
  const openDeleteDialog = () => setDeleteDialogOpen(true);
  const closeDeleteDialog = () => setDeleteDialogOpen(false);


  const deleteImageList = React.useCallback(async () => {
    try {
      closeDeleteDialog(); f
      await grpcClient().deleteImageList({
        name: currentList.name,
      });
      alert(`ImageList was deleted`);
      setCurrentList(null);
    } catch (e) {
      alert(`Failed to delete image list. Reason ${e.message}`);
    }
  }, [currentList]);

  const [curTab, setCurTab] = React.useState(0);

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setCurTab(newValue);
  };

  // @ts-ignore
  return (
    <SwrFallback
      name="ImageLists"
      swrData={imageLists1}
      main={() => (
        <Box minWidth="500px">
          <Tabs textColor="secondary" value={curTab} onChange={handleChange}>
            <Tab label="OPEN UPDATE TAB" />
            <Tab label="OPEN CREATE TAB" />
          </Tabs>
          <Box marginY={2}>
            <Divider />
          </Box>
          <TabPanel value={curTab} index={0}>
            <Form
              onSubmit={(vals) => setCurrentList(indexed[vals.name] || null)}
              initialValues={{ name: imageLists1.data[0].name }}
              render={({ handleSubmit }) => (
                <Autocomplete
                  label="Select an images"
                  name="name"
                  required
                  color="secondary"
                  options={autocompleteData}
                  getOptionValue={(option) => option.value}
                  getOptionLabel={(option) => (option as any).label}
                  onSelect={handleSubmit}
                  renderOption={(props, option, { selected }) => (
                    <li {...props}>
                      <MuiRadio style={{ marginRight: 8 }} checked={selected} />
                      {option.label}
                    </li>
                  )}
                />
              )}
            />
            {currentList && (
              <>
                <Dialog open={deleteDialogOpen} onClose={closeDeleteDialog}>
                  <Box margin={2}>
                    <Spacing spacing={1} direction="column">
                      <Typography>
                        Are you sure you want to delete an imageList `
                        {currentList.displayName}`.
                      </Typography>
                      <Button
                        fullWidth
                        onClick={deleteImageList}
                        color="secondary"
                      >
                        Yes
                      </Button>
                      <Button
                        fullWidth
                        onClick={closeDeleteDialog}
                        color="secondary"
                      >
                        No
                      </Button>
                    </Spacing>
                  </Box>
                </Dialog>
                <Box>
                  <Typography variant="h6">
                    {currentList.displayName}
                  </Typography>
                  <Typography variant="subtitle1">
                    Name: {currentList.name}
                  </Typography>
                </Box>
                <Box marginY={2} className="flex justify-between">
                  <Button
                    variant="contained"
                    color="secondary"
                    size="large"
                    onClick={submitNewImageList}
                  >
                    Submit image list
                  </Button>
                  <Button
                    variant="contained"
                    color="warning"
                    size="large"
                    onClick={openDeleteDialog}
                  >
                    Delete image list
                  </Button>
                </Box>
                <Box height="500px" width="500px">
                  <List>
                    {currentList.images.map((image) => (
                      <ImageControls
                        key={image.name}
                        image={image}
                        onDelete={deleteImage}
                        onArrowUp={liftImage}
                        onArrowDown={dumpImage}
                      />
                    ))}
                  </List>
                </Box>
              </>
            )}
          </TabPanel>
          <TabPanel value={curTab} index={1}>
            <ImageListCreator />
          </TabPanel>
        </Box>
      )}
    />
  );
}
