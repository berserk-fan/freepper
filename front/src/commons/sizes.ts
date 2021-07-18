import { Breakpoint } from "@material-ui/core/styles/createBreakpoints";
import { GridSize } from "@material-ui/core";
import theme from "../theme";

function noMedia(query: string) {
  return query.slice(6);
}

export type SizesSpec = Record<Breakpoint, Exclude<GridSize, "auto">>;
export function createSizes(sizes: SizesSpec): string {
  return Object.entries(sizes)
    .map(
      ([br, gridCols]) =>
        `${noMedia(theme.breakpoints.up(br as Breakpoint))} ${Math.floor(
          (100 * gridCols) / 12,
        )}vw`,
    )
    .reverse()
    .join(",")
    .concat(", 500px");
}
