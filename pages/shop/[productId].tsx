import 'react-responsive-carousel/lib/styles/carousel.min.css';

import {useRouter} from "next/router";
import React from "react";
import {Box, Container, Grid, Typography} from "@material-ui/core";
import {getModelIndex, ModelIndex, productIdsToModel} from "../../configs/Products";
import {Model, Product} from "../../src/model/Model";
import {Carousel, Thumbs} from "react-responsive-carousel";
import Image from 'material-ui-image';

export default function ProductPage() {
    const router = useRouter();
    const {productId} = router.query;

    let model: Model;
    let modelIndex: ModelIndex;
    let images: string[];
    let res;
    let product: Product;
    if (productId) {
        model = productIdsToModel.get(productId as string);
        product = model.products.find((product) => product.id === productId)
        modelIndex = getModelIndex(model);
        images = model.products.map((p) => p.image);
    }

    let [selectedItem, setSelectedItem] = React.useState(null);

    if (modelIndex) {
        res = <Container>
            <Box className={"w-full"}>
              <Typography className={"py-4"} variant={'h4'}>
                  {product.displayName}
              </Typography>
            </Box>
            <Grid container={true} spacing={3}>
                <Grid item={true} xs={12} md={6} spacing={3}>
                    <Carousel selectedItem={selectedItem} showThumbs={false}>
                        {images.map((image) => <Image aspectRatio={16/9} src={image}/>)}
                    </Carousel>
                    <Thumbs selectedItem={selectedItem} onSelectItem={setSelectedItem} thumbWidth={60}>
                        {images.map((image) => <img src={image}/>)}
                    </Thumbs>
                </Grid>
                <Grid item xs={12} md={6}>
                    Hello
                </Grid>
            </Grid>
        </Container>
    } else if (productId) {
        res = <h1>Page Not Found</h1>;
    } else {
        res = false;
    }
    return res;
}
