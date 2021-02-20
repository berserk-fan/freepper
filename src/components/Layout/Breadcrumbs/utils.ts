import { Page, pages } from "../Header/pages";

export function prefixes<T>(arr: T[]): T[][] {
  return arr.map((_, idx) => arr.slice(0, idx + 1));
}

export function toPath(x: string[]): string {
  return x.length === 1 ? "/" : x.join("/")
}

export const hrefToPage = Object.fromEntries(Object.values(pages).map((page):[string, Page] => [page.path, page]))

export function toName(pathAsString: string, path: string[]): string {
  const last = path[path.length -1];
  const predefinedPage = hrefToPage[pathAsString];
  if(predefinedPage) {
    return predefinedPage.name;
  } else {
    return last;
  }
}

export function pathNeeded(path: string[]): boolean {
  const prevLast = path[path.length - 2];
  const last = path[path.length - 1];
  return !(prevLast === "categories" || last === "categories");
}
