import {useRouter} from "next/router";
import React from "react";
import {Container, Grid} from "@material-ui/core";
import {getModelIndex, productIdsToModel} from "../../configs/Products";
import ImageGallery from 'react-image-gallery';

export default function ProductPage() {
    const router = useRouter();
    const {productId} = router.query;
    const images = [
        {
            original: 'https://picsum.photos/id/1018/1000/600/',
            thumbnail: 'https://picsum.photos/id/1018/250/150/',
        },
        {
            original: 'https://picsum.photos/id/1015/1000/600/',
            thumbnail: 'https://picsum.photos/id/1015/250/150/',
        },
        {
            original: 'https://picsum.photos/id/1019/1000/600/',
            thumbnail: 'https://picsum.photos/id/1019/250/150/',
        },
    ];
    let model, modelIndex, res;
    if (productId) {
        model = productIdsToModel.get(productId as string);
        modelIndex = getModelIndex(model);
    }
    if (modelIndex) {
        res = <Container>
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <ImageGallery showPlayButton={false} items={images} />
                </Grid>
                <Grid item xs={12} md={6}>
                    Hello
                </Grid>
            </Grid>
        </Container>
    } else if(productId) {
        res = <h1>Page Not Found</h1>;
    } else {
        res = false;
    }
    return res;
}
