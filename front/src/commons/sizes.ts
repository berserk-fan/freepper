import { Breakpoint } from "@mui/material/styles/createBreakpoints";
import { GridSize } from "@mui/material";
import theme from "../theme";

function noMedia(query: string) {
  return query.slice(6);
}

export type SizesSpec = Partial<Record<Breakpoint, Exclude<GridSize, "auto">>>;
export function createSizes(
  sizes: SizesSpec,
  maxVW = 100,
  fallback = "500px",
): string {
  return Object.entries(sizes)
    .map(
      ([br, gridCols]) =>
        `${noMedia(theme.breakpoints.up(br as Breakpoint))} ${Math.floor(
          (maxVW * gridCols) / 12,
        )}vw`,
    )
    .reverse()
    .join(",")
    .concat(`, ${fallback}`);
}
