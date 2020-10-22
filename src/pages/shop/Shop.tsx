import {beds} from "../../../configs/Products";
import React from "react";
import {Container, Grid, Typography} from "@material-ui/core";
import ItemView from "../../components/ItemView";

export default function Shop() {
    const categories = [beds];
    const currentCategory = categories[0];
    return (
        <Container>
            <Typography align={'center'} variant={'h1'}>{currentCategory.displayName}</Typography>
            <Grid container={true} spacing={3} justify={"space-between"}>
                {currentCategory.models.flatMap(p => p.products)
                    .map(item => (
                        <Grid key={item.id} item={true} xs={12} sm={6} md={3}>
                            <ItemView product={item} className={"mx-auto"}/>
                        </Grid>))}
            </Grid>
        </Container>)
}
