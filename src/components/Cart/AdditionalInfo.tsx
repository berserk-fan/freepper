import { Product } from "@mamat14/shop-server/shop_model";
import Typography from "@material-ui/core/Typography/Typography";
import React from "react";

export default function AdditionalInfo({ details }: Product) {
  switch (details.$case) {
    case "dogBed": {
      const size = details.dogBed.sizes.find(
        (s) => s.id === details.dogBed.sizeId,
      );
      return (
        <div>
          <Typography noWrap display="inline" variant="caption">
            Размер:
          </Typography>
          <Typography noWrap display="inline" variant="h6">
            {` ${size.displayName}`}
          </Typography>
        </div>
      );
    }
    default:
      return <></>;
  }
}
