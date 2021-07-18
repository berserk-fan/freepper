import { Product } from "apis/catalog";
import Typography from "@material-ui/core/Typography/Typography";
import React from "react";

export default function AdditionalInfo({ details }: Product) {
  switch (details.$case) {
    case "dogBed": {
      return (
        <div>
          <Typography noWrap display="inline" variant="caption">
            Размер:
          </Typography>
          <Typography noWrap display="inline" variant="h6">
            {` ${details.dogBed.size}`}
          </Typography>
        </div>
      );
    }
    default:
      return <></>;
  }
}
