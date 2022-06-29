import TreeView from "@mui/lab/TreeView";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import TreeItem from "@mui/lab/TreeItem";
import React from "react";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import LoadingButton from "@mui/lab/LoadingButton";
import Grid from "@mui/material/Grid";
import { Image } from "apis/image.pb";
import { useSWRConfig } from "swr";
import { Form, Field } from "react-final-form";
import { TextField } from "mui-rff";
import InputAdornment from "@mui/material/InputAdornment";
import { Dialog, FormControl, InputLabel, OutlinedInput } from "@mui/material";
import Container from "@mui/material/Container";
import SwrFallback from "../Swr/SwrFallback";
import { useImages } from "../../commons/swrHooks";
import { MyAvatar } from "../Commons/MyAvatar";
import grpcClient from "../../commons/shopClient";
import Spacing from "../Commons/Spacing";

function getPrefix(src: string): string | null {
  const idx = src.lastIndexOf("/");
  if (idx === -1) {
    return "";
  }
  return src.slice(0, idx);
}

function getSuffix(src: string) {
  return src.slice(src.lastIndexOf("/") + 1, src.length);
}

function moveTo(obj: any, path: string[], start: number) {
  if (path.length === start) {
    return obj;
  }
  const cur = path[start];
  // eslint-disable-next-line no-param-reassign
  obj[cur] = obj[cur] ? obj[cur] : {};
  return moveTo(obj[cur], path, start + 1);
}

function pushValue(obj: any, value: Image) {
  if (!obj.values) {
    // eslint-disable-next-line no-param-reassign
    obj.values = [value];
  } else {
    obj.values.push(value);
  }
}

export function recursive(images: Image[]) {
  const result = {};
  let currentNode = result;
  let prevPrefix = "";

  for (let i = 0; i < images.length; i += 1) {
    const curPrefix = getPrefix(images[i].src);
    const curValue = images[i];
    if (!curPrefix) {
      pushValue(result, curValue);
    } else {
      if (curPrefix.startsWith(prevPrefix)) {
        const sub = curPrefix.slice(prevPrefix.length, curPrefix.length);
        currentNode = moveTo(
          currentNode,
          sub.length === 0 ? [] : sub.split("/"),
          0,
        );
      } else {
        currentNode = moveTo(result, curPrefix.split("/"), 0);
      }
      prevPrefix = curPrefix;
      pushValue(currentNode, curValue);
    }
  }
  return result;
}

function DeleteImageButton({ name }: { name: string }) {
  const [loading, setLoading] = React.useState(false);
  const { mutate } = useSWRConfig();

  const deleteImage = React.useCallback(async (ev) => {
    ev.stopPropagation();
    setLoading(true);
    try {
      await grpcClient().deleteImage({ name });
      await mutate("images");
    } finally {
      setLoading(false);
    }
  }, []);

  return (
    <LoadingButton
      variant="outlined"
      color="secondary"
      loading={loading}
      onClick={deleteImage}
    >
      Delete
    </LoadingButton>
  );
}

type ImageFormProps = Omit<NoIds<Image>, "data"> & { file: File };

function removeStartingSlack(s: string): string {
  if (s.length === 0) {
    return "";
  }
  if (s.startsWith("/")) {
    return s.slice(1);
  }
  return s;
}

function getExtension(fname: string): string {
  return fname.slice((Math.max(0, fname.lastIndexOf(".")) || Infinity) + 1);
}

function ImageCreatorDialog({
  folder,
  onSubmit,
}: {
  folder: string;
  onSubmit: () => void;
}) {
  const [loading, setLoading] = React.useState(false);
  const { mutate } = useSWRConfig();

  const addImage = React.useCallback(
    async (image: ImageFormProps) => {
      setLoading(true);
      try {
        const data = await image.file.stream().getReader().read();
        const fileName = image.src
          ? `${image.src}${getExtension(image.src)}`
          : image.file.name;
        const obj = {
          ...image,
          name: "",
          uid: "",
          src: removeStartingSlack(`${folder}/${fileName}`),
          data: data.value,
        };

        await grpcClient().createImage({ parent: "images", image: obj });
        await mutate("images");
      } finally {
        setLoading(false);
        onSubmit();
      }
    },
    [folder],
  );

  return (
    <Box margin={3} minHeight="500px">
      <Form<ImageFormProps>
        onSubmit={addImage}
        render={({ handleSubmit, values }) => (
          <form onSubmit={handleSubmit}>
            <Spacing spacing={3} direction="column">
              <TextField
                label="Image source"
                helperText="Enter the name of the image. In English"
                name="src"
                variant="outlined"
                color="secondary"
                placeholder={values?.file?.name}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">{folder}/</InputAdornment>
                  ),
                  endAdornment:
                    values?.file?.name && values.src ? (
                      <InputAdornment position="end">
                        .{getExtension(values.file.name)}
                      </InputAdornment>
                    ) : undefined,
                }}
              />
              <TextField
                label="Image description"
                helperText="Enter meaningful description of the image. In ukrainian."
                name="alt"
                required
                variant="outlined"
                color="secondary"
              />
              <Field name="file" required>
                {({ input: { value, onChange, ...input } }) => (
                  // eslint-disable-next-line jsx-a11y/label-has-associated-control
                  <FormControl variant="outlined" color="secondary">
                    <InputLabel hidden htmlFor="image-upload">
                      Upload image
                    </InputLabel>
                    <OutlinedInput
                      required
                      id="upload-image"
                      inputProps={{ accept: "image/*" }}
                      type="file"
                      onChange={({ target }) =>
                        onChange((target as any).files[0])
                      }
                      {...input}
                    />
                  </FormControl>
                )}
              </Field>
              <LoadingButton
                variant="outlined"
                color="secondary"
                loading={loading}
                type="submit"
              >
                Submit
              </LoadingButton>
            </Spacing>
          </form>
        )}
      />
    </Box>
  );
}

function Label({ image }: { image: Image }) {
  const fileName = getSuffix(image.src);
  return (
    <Grid container alignItems="center" spacing={1}>
      <Grid item>
        <MyAvatar image={image} variant="big" />
      </Grid>
      <Grid item>
        <Typography variant="h5">{fileName}</Typography>
      </Grid>
      <Grid item>
        <DeleteImageButton name={image.name} />
      </Grid>
    </Grid>
  );
}

function FolderLabel({
  prefix,
  folderName,
}: {
  prefix: string;
  folderName: string;
}) {
  const [open, setOpen] = React.useState(false);
  return (
    <>
      <Dialog
        open={open}
        PaperProps={{
          onClick: (ev) => {
            ev.stopPropagation();
          },
        }}
        onClose={(ev) => {
          (ev as any).stopPropagation();
          setOpen(false);
        }}
      >
        <ImageCreatorDialog
          folder={`${prefix}/${folderName}`}
          onSubmit={() => setOpen(false)}
        />
      </Dialog>
      <Grid container alignItems="center" spacing={1}>
        <Grid item>
          <Typography variant="h6">{folderName}</Typography>
        </Grid>
        <Grid item>
          <Button
            onClick={(e) => {
              e.stopPropagation();
              setOpen(true);
            }}
            color="secondary"
            variant="outlined"
            size="small"
          >
            Add New Image
          </Button>
        </Grid>
      </Grid>
    </>
  );
}

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
            label={<FolderLabel prefix={prefix} folderName={folderName} />}
          >
            {toTree(obj[folderName], `${prefix}/${folderName}`)}
          </TreeItem>
        ))}
      {obj.values &&
        obj.values.map((image: Image) => (
          <TreeItem
            key={image.name}
            nodeId={image.name}
            label={<Label image={image} />}
          />
        ))}
    </>
  );
}

export default function ImageEditor() {
  const images = useImages();
  const [expanded, setExpanded] = React.useState<string[]>([]);

  const recursive1 = React.useMemo(() => {
    if (images.data) {
      return recursive(images.data.sort((a, b) => a.src.localeCompare(b.src)));
    }
    return {};
  }, [images.data]);

  const handleToggle = (event: React.SyntheticEvent, nodeIds: string[]) => {
    setExpanded(nodeIds);
  };

  return (
    <SwrFallback
      swrData={images}
      name="images"
      main={() => (
        <Container maxWidth="sm">
          <Typography>Images</Typography>
          <Button
            variant="outlined"
            color="secondary"
            onClick={() => setExpanded([])}
          >
            Collapse all
          </Button>
          <TreeView
            aria-label="file system navigator"
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpandIcon={<ChevronRightIcon />}
            expanded={expanded}
            onNodeToggle={handleToggle}
          >
            <TreeItem
              nodeId="root"
              label={<FolderLabel prefix="" folderName="" />}
            >
              {toTree(recursive1, "")}
            </TreeItem>
          </TreeView>
        </Container>
      )}
    />
  );
}
