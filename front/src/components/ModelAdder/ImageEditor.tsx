import TreeView from "@material-ui/lab/TreeView";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import TreeItem from "@material-ui/lab/TreeItem";
import React from "react";
import Typography from "@material-ui/core/Typography/Typography";
import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup/ButtonGroup";
import Grid from "@material-ui/core/Grid";
import { Image } from "apis/image.pb";
import SwrFallback from "../Swr/SwrFallback";
import { useImages } from "../../commons/swrHooks";
import { MyAvatar } from "../Commons/Icon";

function getPrefix(src: string) {
  return src.slice(0, src.lastIndexOf("/"));
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

function recursive(images: Image[]) {
  const result = {};
  let currentNode = result;
  let prevPrefix = "";

  for (let i = 0; i < images.length; i += 1) {
    const curPrefix = getPrefix(images[i].src);
    const curValue = images[i];
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
  return result;
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
        <ButtonGroup>
          <Button>Delete</Button>
        </ButtonGroup>
      </Grid>
    </Grid>
  );
}
function FolderLabel({ folder }: { folder }) {
  return (
    <Grid container alignItems="center" spacing={1}>
      <Grid item>
        <Typography>{folder}</Typography>
      </Grid>
      <Grid item>
        <Button size="small" variant="outlined">
          Add New Image
        </Button>
      </Grid>
    </Grid>
  );
}

function toTree(obj: any, prefix: string) {
  const next = Object.keys(obj);
  return (
    <>
      {next
        .filter((x) => x !== "values")
        .map((folder) => (
          <TreeItem
            key={`${prefix}/${folder}`}
            nodeId={`${prefix}/${folder}`}
            label={<FolderLabel folder={folder}/>}
          >
            {toTree(obj[folder], `${prefix}/${folder}`)}
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
        <Box>
          <Typography>Images</Typography>
          <Button onClick={() => setExpanded([])}>Collapse all</Button>
          <TreeView
            aria-label="file system navigator"
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpandIcon={<ChevronRightIcon />}
            expanded={expanded}
            onNodeToggle={handleToggle}
          >
            {toTree(recursive1, "")}
          </TreeView>
        </Box>
      )}
    />
  );
}
