declare module "*.svg" {
  const content: any;
  export default content;
}

type NoIds<T> = Omit<T, "uid" | "name">;
