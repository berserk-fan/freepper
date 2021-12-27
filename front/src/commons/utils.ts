import fs from "fs";
import assert from "assert";
import {
  FabricKey2,
  isFabricKeyOf,
  isProductKey,
  ProductKey,
} from "../configs/catalog/defs";

const HOME_FOLDER = "public";
const PRODUCT_FOLDER = "beds";
export const IMAGES_FOLDER = `${HOME_FOLDER}/${PRODUCT_FOLDER}`;

export type ParsedBedImage<T extends ProductKey> = {
  modelId: T;
  fabricId: FabricKey2<T>;
  imageName: string;
  src: string;
};

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function checkExhaustive(_: never): never {
  throw new Error("Not exhaustive switch case");
}

export function listFiles(dir: string): string[] {
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

export function parse(pathToImage: string): ParsedBedImage<ProductKey> {
  const commonPrefix = `${IMAGES_FOLDER}/`;
  if (!pathToImage.startsWith(commonPrefix)) {
    throw new Error("this is unexpected");
  }
  const pathNoPrefix = pathToImage.substr(commonPrefix.length);
  const [modelId, fabricId, imageName] = pathNoPrefix.split("/");
  assert(isProductKey(modelId), `QWEQWE ${pathToImage} ${pathNoPrefix}`);
  assert(isFabricKeyOf(modelId, fabricId), `${modelId} ${fabricId}`);
  const src = pathToImage.substring(HOME_FOLDER.length); // to correctly refer to image in code. correct reference is without public
  return {
    fabricId,
    src,
    imageName,
    modelId,
  };
}
