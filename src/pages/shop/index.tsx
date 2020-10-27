import {beds} from "../../../configs/Products";
import {Container, Grid, Typography} from "@material-ui/core";
import ItemView from "../../components/ItemView";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import ShopClient from '@mamat14/shop-server'
import {category, shopProducts} from "../../../configs/Data";
import {useState} from "react";
import {Product} from "@mamat14/shop-server/shop_model";

type STATUS = "PENDING" | "COMPLETED" | "FAILED";

export default function Shop() {
    const categories = [beds];
    const currentCategory = categories[0];
    const shopClient = new ShopClient({categories: [category], products: shopProducts});
    const [[products, status], setProductsAndStatus] = useState<[Product[], STATUS]>([[], "PENDING"]);
    if(status === "PENDING") {
        shopClient
            .listProducts({
                parent: '/categories/some-cat/products',
                pageSize: 25,
                pageToken: ""
            })
            .then((response) => setProductsAndStatus([response.products, "COMPLETED"]))
            .catch((err) => setProductsAndStatus([[], "FAILED"]));
    }

    let res;
    switch (status) {
        case "PENDING":
            res = <p>Loading products...</p>;
            break;
        case "COMPLETED":
            res = <LayoutWithHeader>
                <Container>
                    <Typography align={'center'} variant={'h1'}>{currentCategory.displayName}</Typography>
                    <Grid container={true} spacing={3} justify={"space-between"}>
                        {products.map(item => (
                            <Grid key={item.id} item={true} xs={12} sm={6} md={3}>
                                <ItemView product={item} className={"mx-auto"}/>
                            </Grid>))
                        }
                    </Grid>
                </Container>
            </LayoutWithHeader>;
            break;
        case "FAILED":
            res = <p>Failed to load resourses from server</p>;
            break;
    }
    return res;
}
