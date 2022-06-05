import React from "react";
import Container from "@material-ui/core/Container";
import Box from "@material-ui/core/Box";
import Grid from "@material-ui/core/Grid";
import { Model } from "apis/model.pb";
import ItemView from "./ItemView";
import LayoutWithHeaderAndFooter from "../Layout/LayoutWithHeaderAndFooter";
import { PAGE_SIZES } from "./definitions";

export default function ShopPage({ models }: { models: Model[] }) {
  return (
    <LayoutWithHeaderAndFooter showValueProp>
      <Container>
        <Box paddingTop={1}>
          <Grid container spacing={3}>
            {models.map((model, idx) => (
              <Grid item key={model.uid} {...PAGE_SIZES}>
                <ItemView
                  model={model}
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
