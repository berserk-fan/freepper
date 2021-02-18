import {Breakpoint} from "@material-ui/core/styles/createBreakpoints";
import {noMedia} from "../../utils/Breakpoint";
import theme from "../../theme";
import {GridSize} from "@material-ui/core";

export const PAGE_SIZES: Record<Breakpoint, Exclude<GridSize, "auto">> = {
  xs: 12,
  sm: 6,
  md: 4,
  lg: 3,
  xl: 3
};

export const SIZES = Object.entries(PAGE_SIZES)
    .map(([br, gridCols]) => `${noMedia(theme.breakpoints.up(br as Breakpoint))} ${Math.floor(100 * gridCols / 12)}vw`)
    .join(",")
    .concat(", 100vw");
