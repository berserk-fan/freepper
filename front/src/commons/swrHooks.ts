import useSWR, { useSWRConfig } from "swr";
import grpcClient from "./shopClient";

export function useCategories() {
  const key = `categories`;
  const { data, error, mutate } = useSWR(key, () =>
    grpcClient()
      .listCategories({
        parent: key,
        pageSize: 1000,
      })
      .then((x) => x.categories),
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
  const { data, error, mutate } = useSWR(key, () =>
    grpcClient()
      .listModels({
        parent: key,
        pageSize: 1000,
      })
      .then((x) => x.models),
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}

export function useModel(modelName: string) {
  const { data, error, mutate } = useSWR(modelName, () =>
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
  const { data, error, mutate } = useSWR("imageLists", () =>
    grpcClient()
      .listImageLists({
        parent: "imageLists",
        pageSize: 1000,
      })
      .then((x) => x.imageLists),
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
  const { data, error, mutate } = useSWR(key, () =>
    grpcClient()
      .listProducts({
        parent: key,
        pageSize: 1000,
      })
      .then((x) => x.products),
  );

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
}
