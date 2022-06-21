import { ReactNode } from "react";
import Typography from "@material-ui/core/Typography/Typography";

export default function SwrFallback<
  T extends { data: U; isLoading: boolean; isError: boolean },
  U,
>({
  swrData,
  main,
  name,
}: {
  name: string;
  swrData: T | T[];
  main: () => ReactNode;
}) {
  if (
    Array.isArray(swrData)
      ? swrData.map((x) => x.isError).reduce((a, b) => a || b)
      : swrData.isError
  ) {
    return <Typography>Error during loading {name}!</Typography>;
  }

  if (
    Array.isArray(swrData)
      ? swrData.map((x) => x.isLoading).reduce((a, b) => a || b)
      : swrData.isLoading
  ) {
    return <Typography>{name} is loading</Typography>;
  }

  if (
    Array.isArray(swrData)
      ? swrData
          .map((x) => Array.isArray(x.data) && x.data.length === 0)
          .reduce((a, b) => a || b)
      : Array.isArray(swrData.data) && swrData.data.length === 0
  ) {
    return <Typography>{name} is empty!</Typography>;
  }

  return <>{main()}</>;
}
