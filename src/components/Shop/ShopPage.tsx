import {Box, Container, Grid} from "@material-ui/core";
import ItemView from "./ItemView";
import LayoutWithHeaderAndFooter from "../Layout/LayoutWithHeaderAndFooter";
import React from "react";
import {TmpGroupedProduct} from "../../../configs/tmpProducts";
import {PAGE_SIZES} from "./ShopDefinitions";

export type ShopPageProps = {
    products: TmpGroupedProduct[];
    categoryName: string;
};

export default function ShopPage({products, categoryName}: ShopPageProps) {
    return <LayoutWithHeaderAndFooter>
        <Container>
            <Box paddingTop={1}>
                <Grid container spacing={3}>
                    {products.map((item, idx) => (
                        <Grid item key={item.id} {...PAGE_SIZES}>
                            <ItemView
                                categoryName={categoryName}
                                product={item}
                                className={"mx-auto"}
                                priority={idx === 0}
                            />
                        </Grid>
                    ))}
                </Grid>
            </Box>
        </Container>
    </LayoutWithHeaderAndFooter>
}