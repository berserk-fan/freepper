import React from "react";
import { ImageList } from "apis/image_list.pb";
import { TextField } from "mui-rff";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Form } from "react-final-form";
import Button from "@mui/material/Button";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import ListItemText from "@mui/material/ListItemText";
import { Image, Image as MyImage } from "apis/image.pb";
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
import Dialog from "@mui/material/Dialog";
import AvatarGroup from "@mui/material/AvatarGroup";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import TreeItem from "@mui/lab/TreeItem";
import TreeView from "@mui/lab/TreeView";
import Spacing from "../Commons/Spacing";
import grpcClient from "../../commons/shopClient";
import { useImageLists, useImages } from "../../commons/swrHooks";
import SwrFallback from "../Swr/SwrFallback";
import { MyAvatar } from "../Commons/MyAvatar";
import { recursive } from "./ImageEditor";
import ImageListSelector from "./ImageListSelector";

function toTree(obj: any, prefix: string) {
  const next = Object.keys(obj);
  return (
    <>
      {next
        .filter((x) => x !== "values")
        .map((folderName) => (
          <TreeItem
            key={`${prefix}/${folderName}`}
            nodeId={`${prefix}/${folderName}`}
            label={folderName}
          >
            {toTree(obj[folderName], `${prefix}/${folderName}`)}
          </TreeItem>
        ))}
      {obj.values &&
        obj.values.map((image: Image) => (
          <TreeItem
            key={image.name}
            nodeId={image.name}
            label={
              <Box>
                <MyAvatar image={image} variant="big" />
                {image.name}
              </Box>
            }
          />
        ))}
    </>
  );
}

function getImages(images: { data: Image[] }, selected: Set<string>) {
  return images.data.filter((im) => selected.has(im.name));
}

function ImageAdder({ onAdd }: { onAdd: (imageNames: Image[]) => void }) {
  const images = useImages();

  const recursive1 = React.useMemo(() => {
    if (images.data) {
      return recursive(images.data.sort((a, b) => a.src.localeCompare(b.src)));
    }
    return {};
  }, [images.data]);

  const [selected, setSelected] = React.useState<Set<string>>(new Set());
  const handleNodeSelect = (ev, nodeIds: string[]) =>
    setSelected(new Set(nodeIds));
  const handleAdd = () => {
    onAdd(getImages(images, selected));
  };

  return (
    <SwrFallback
      name="images"
      swrData={images}
      main={() => (
        <Box minWidth="500px" className="select-none" margin={2}>
          <Button
            size="large"
            variant="contained"
            color="secondary"
            onClick={handleAdd}
          >
            Submit
          </Button>
          <Box className="flex justify-content">
            <Typography variant="h4">Selected: </Typography>
            <AvatarGroup>
              {getImages(images, selected).map((image) => (
                <MyAvatar key={image.name} variant="big" image={image} />
              ))}
            </AvatarGroup>
          </Box>
          <TreeView
            aria-label="file system navigator"
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpandIcon={<ChevronRightIcon />}
            multiSelect
            onNodeSelect={handleNodeSelect}
          >
            {toTree(recursive1, "")}
          </TreeView>
        </Box>
      )}
    />
  );
}

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
      closeDeleteDialog();
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

  const [addImageDialog, setAddImageDialog] = React.useState(false);
  const handleImageDialogClose = () => setAddImageDialog(false);
  const openImageDialog = () => setAddImageDialog(true);
  const addImages = (images1: Image[]) => {
    setCurrentList((prev) => {
      const images = Object.values(
        Object.fromEntries(
          [...images1, ...prev.images].map((x) => [x.name, x]),
        ),
      );
      return { ...prev, images };
    });
    handleImageDialogClose();
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
            <ImageListSelector
              imageLists={imageLists1.data}
              onSelect={setCurrentList}
            />
            {currentList && (
              <>
                <Dialog open={addImageDialog} onClose={handleImageDialogClose}>
                  <ImageAdder onAdd={addImages} />
                </Dialog>
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
                  <Button color="secondary" onClick={openImageDialog}>
                    ADD IMAGES
                  </Button>
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
