import fs from "fs";
import assert from "assert";
import { ImageData } from "apis/catalog";
import {
  FabricKey2,
  isFabricKeyOf,
  isProductKey,
  BedKey,
  makeProductName,
  ALL_SIZES,
} from "../configs/catalog/beds";

type ParsedBedImage<T extends BedKey> = {
  modelId: T;
  fabricId: FabricKey2<T>;
  imageName: string;
  src: string;
};

function parse(pathToImage: string): ParsedBedImage<BedKey> {
  const commonPrefix = "public/beds/";
  if (!pathToImage.startsWith(commonPrefix)) {
    throw new Error("this is unexpected");
  }
  const pathNoPrefix = pathToImage.substr(commonPrefix.length);
  const [modelId, fabricId, imageName] = pathNoPrefix.split("/");
  assert(isProductKey(modelId), modelId);
  assert(isFabricKeyOf(modelId, fabricId), `${modelId} ${fabricId}`);
  const src = pathToImage.substring("public".length); // to correctly refer to image in code. correct reference is without public
  return {
    fabricId,
    src,
    imageName,
    modelId,
  };
}

function listFiles(dir: string): string[] {
  const contents = fs.readdirSync(dir);
  return contents.flatMap((file: string) => {
    if (fs.lstatSync(`${dir}/${file}`).isDirectory()) {
      return listFiles(`${dir}/${file}`);
    }
    if (file.includes(".DS_Store")) {
      return [];
    }
    return [`${dir}/${file}`];
  });
}

function genForFabric(
  files: ParsedBedImage<BedKey>[],
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
  files: ParsedBedImage<BedKey>[],
): Record<string, ParsedBedImage<BedKey>[]> {
  const res: Record<string, ParsedBedImage<BedKey>[]> = {};
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
  const files = listFiles("public/beds").map(parse);
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
