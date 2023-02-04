import useSWR from "swr";
import { hash } from "immutable";
import { ImageList } from "apis/image_list.pb";
import { Model } from "apis/model.pb";
import { Category } from "apis/category.pb";
import { Product } from "apis/product.pb";
import { Image } from "apis/image.pb";
import grpcClient from "./shopClient";

type Named = { name: string };
function compareArr(x?: Named[], y?: Named[]) {
  if (x === undefined || y === undefined) {
    return x === y;
  }
  return hash(x.map((x1) => x1.name)) === hash(y.map((x1) => x1.name));
}

export function useImages() {
  const key = `images`;
  const { data, error, mutate } = useSWR<Image[]>(
    key,
    () =>
      grpcClient()
        .listImages({
          parent: key,
          pageSize: 1000,
        })
        .then((x) => x.images),
    { compare: compareArr },
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}

export function useCategories() {
  const key = `categories`;
  const { data, error, mutate } = useSWR<Category[]>(
    key,
    () =>
      grpcClient()
        .listCategories({
          parent: key,
          pageSize: 1000,
        })
        .then((x) => x.categories),
    { compare: compareArr },
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}

export function useModels(categoryName: string) {
  const key = `${categoryName}/models`;
  const { data, error, mutate } = useSWR<Model[]>(
    key,
    () =>
      grpcClient()
        .listModels({
          parent: key,
          pageSize: 1000,
        })
        .then((x) => x.models),
    { compare: compareArr },
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}

export function useModel(modelName: string) {
  const { data, error, mutate } = useSWR<Model>(modelName, () =>
    grpcClient().getModel({ name: modelName }),
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}

export function useImageLists() {
  const { data, error, mutate } = useSWR<ImageList[]>(
    "imageLists",
    () =>
      grpcClient()
        .listImageLists({
          parent: "imageLists",
          pageSize: 1000,
        })
        .then((x) => x.imageLists),
    { compare: compareArr },
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}

export function useProducts(modelName: string) {
  const key = `${modelName}/products`;
  const { data, error, mutate } = useSWR<Product[]>(
    key,
    () =>
      grpcClient()
        .listProducts({
          parent: key,
          pageSize: 1000,
        })
        .then((x) => x.products),
    { compare: compareArr },
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}
