import React, { useEffect, useMemo } from "react";
import { FormControl, InputLabel, MenuItem, Select } from "@material-ui/core";
import Box from "@material-ui/core/Box";
import { GetServerSideProps } from "next";
import Button from "@material-ui/core/Button";
import Image from "next/image";
import {
  MODEL_TO_FABRIC,
  ProductKey,
  productKeys,
} from "../../configs/catalog/defs";
import { listFiles, parse, ParsedBedImage } from "../../commons/utils";

const PHOTOS_FOLDER = "public/beds";

type IndexedImages = Map<string, ParsedBedImage<ProductKey>[]>;
type UploadedFile = {
  file: File;
  src: string;
};
type NewFiles = Map<string, UploadedFile[]>;
type Image1 = {
  src: string;
  name: string;
};

export default function AddImages({
  images,
}: {
  images: ParsedBedImage<ProductKey>[];
}) {
  const imagesMap: IndexedImages = useMemo(() => {
    const res: IndexedImages = new Map();
    for (let i = 0; i < images.length; i += 1) {
      const image = images[i];
      const key: string = `${image.modelId}_${image.fabricId}`;
      const cur = res.get(key) || [];
      cur.push(image);
      res.set(key, cur);
    }
    return res;
  }, []);

  const [newImages, setNewImages] = React.useState<NewFiles>(new Map());
  const [deleted, setDeletedImages] = React.useState<Set<string>>(new Set());
  const [model, setModel] = React.useState<string>(productKeys[0]);
  const [fabric, setFabric] = React.useState<string>("");
  const [fabrics, setFabrics] = React.useState<string[]>([]);
  const [curImages, setCurImages] = React.useState<
    ParsedBedImage<ProductKey>[]
  >([]);
  const handleModelChange = (event) => {
    setModel(event.target.value);
  };
  const handleFabricChange = (event) => {
    setFabric(event.target.value);
  };
  const handleImageUpload = (event) => {
    const file = event.target.files[0];
    const src = window.URL.createObjectURL(event.target.files[0]);
    setNewImages((prev: NewFiles) => {
      const key = `${model}_${fabric}`;
      const curNew = prev.get(key) || [];
      curNew.push({ file, src });
      prev.set(key, curNew);
      return new Map(prev.entries());
    });
  };
  const handleDelete = (value) => () => {
    setDeletedImages((prev) => new Set(prev.add(value)));
  };
  useEffect(() => {
    const allFabrics = MODEL_TO_FABRIC[model];
    const nonEmptyFabics = allFabrics.filter(
      (fabKey) => !!imagesMap.get(`${model}_${fabKey}`),
    );
    setFabrics(nonEmptyFabics);
  }, [model]);
  useEffect(() => setFabric(fabrics.length > 0 ? fabrics[0] : ""), [fabrics]);
  useEffect(() => {
    const selectedImages = imagesMap.get(`${model}_${fabric}`) || [];
    setCurImages(selectedImages);
  }, [fabric]);
  const [finalPhotos, setFinalPhotos] = React.useState<Image1[]>([]);
  useEffect(() => {
    const curImages1 = curImages.map((photo) => ({
      src: photo.src,
      name: photo.imageName,
    }));
    const curNewImages = (newImages.get(`${model}_${fabric}`) || []).map(
      (uploaded) => ({ src: uploaded.src, name: uploaded.file.name }),
    );
    curNewImages.push(...curImages1);
    const withoutDeleted = curNewImages.filter(
      (image) => !deleted.has(image.src),
    );
    setFinalPhotos(withoutDeleted);
  }, [newImages, curImages, deleted]);

  return (
    <Box padding={10}>
      <Box height="100px" width="200px">
        <FormControl fullWidth>
          <InputLabel id="choose-model">Model</InputLabel>
          <Select
            labelId="choose-model"
            id="choose-model-select"
            value={model}
            label="Model"
            onChange={handleModelChange}
          >
            {productKeys.map((key) => (
              <MenuItem key={key} value={key}>
                {key}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>
      <Box height="100px" width="200px">
        <FormControl fullWidth>
          <InputLabel id="choose-fabric">Fabric</InputLabel>
          <Select
            labelId="choose-fabric"
            id="choose-fabric-select"
            value={fabric}
            label="Fabric"
            onChange={handleFabricChange}
          >
            {fabrics.map((fab) => (
              <MenuItem key={fab} value={fab}>
                {fab}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>
      Photos: <br />
      <input
        style={{ display: "none" }}
        id="contained-button-file"
        type="file"
        onChange={handleImageUpload}
      />
      {/* eslint-disable-next-line jsx-a11y/label-has-associated-control */}
      <label htmlFor="contained-button-file">
        <Button component="span" variant="contained" size="large">
          Add new
        </Button>
      </label>
      <br />
      <Box
        height="100px"
        width="100vw"
        display="flex"
        flexWrap="wrap"
        flexDirection="row"
      >
        {finalPhotos.map((image) => (
          <Box display="inline" width={250} key={image.src} margin={1}>
            {image.name}
            <Button
              variant="contained"
              size="large"
              onClick={handleDelete(image.src)}
            >
              Delete
            </Button>
            <Image src={image.src} width={250} height={250} />
          </Box>
        ))}
      </Box>
    </Box>
  );
}

export const getServerSideProps: GetServerSideProps = async () => {
  const images = listFiles(PHOTOS_FOLDER).map(parse);
  return {
    props: {
      images,
    },
  };
};
