import React from "react";
import Container from "@material-ui/core/Container";
import Box from "@material-ui/core/Box";
import Grid from "@material-ui/core/Grid";
import { Model } from "apis/catalog";
import ItemView from "./ItemView";
import LayoutWithHeaderAndFooter from "../Layout/LayoutWithHeaderAndFooter";
import { PAGE_SIZES } from "./definitions";

export type ShopPageProps = {
  products: Model[];
  categoryName: string;
};

export default function ShopPage({ products, categoryName }: ShopPageProps) {
  return (
    <LayoutWithHeaderAndFooter showValueProp>
      <Container>
        <Box paddingTop={1}>
          <Grid container spacing={3}>
            {products.map((item, idx) => (
              <Grid item key={item.id} {...PAGE_SIZES}>
                <ItemView
                  categoryName={categoryName}
                  product={item}
                  className="mx-auto"
                  priority={idx === 0}
                />
              </Grid>
            ))}
          </Grid>
        </Box>
      </Container>
    </LayoutWithHeaderAndFooter>
  );
}
