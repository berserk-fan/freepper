import fs from "fs";
import { ImageData } from "apis/catalog";
import {
  ProductKey,
  makeProductName,
  ALL_SIZES,
} from "../configs/catalog/defs";
import {IMAGES_FOLDER, listFiles, parse, ParsedBedImage} from "../commons/utils";

function genForFabric(
  files: ParsedBedImage<ProductKey>[],
): Record<string, ImageData[]> {
  const res: Record<string, ImageData[]> = {};
  files.forEach((cur) => {
    const imageData: ImageData = {
      src: cur.src,
      alt: "TODO",
      name: makeProductName(
        cur.modelId,
        cur.fabricId,
        Object.keys(ALL_SIZES[cur.modelId])[0],
      ),
    };
    if (!res[cur.fabricId]) {
      res[cur.fabricId] = [imageData];
    } else {
      res[cur.fabricId].push(imageData);
    }
  });
  return res;
}

function genForModel(
  files: ParsedBedImage<ProductKey>[],
): Record<string, ParsedBedImage<ProductKey>[]> {
  const res: Record<string, ParsedBedImage<ProductKey>[]> = {};
  files.forEach((cur) => {
    if (!res[cur.modelId]) {
      res[cur.modelId] = [cur];
    } else {
      res[cur.modelId].push(cur);
    }
  });
  return res;
}

function gen(): Record<string, Record<string, ImageData[]>> {
  const files = listFiles(IMAGES_FOLDER).map(parse);
  const groupByModelId = genForModel(files);
  const groupByFabricAndUseImageData = Object.entries(groupByModelId).map(
    ([model, modelFiles]) => [model, genForFabric(modelFiles)],
  );
  return Object.fromEntries(groupByFabricAndUseImageData);
}

function generateImages(): void {
  const generated = gen();
  const json = JSON.stringify(generated, null, 2);
  fs.writeFileSync("src/configs/catalog/images.json", json);
}

generateImages();
