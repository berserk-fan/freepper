import ModelView from "../components/ModelView";
import 'react-responsive-carousel/lib/styles/carousel.min.css'
import React from "react";
import {Box, Container, Grid} from "@material-ui/core";
import {Carousel} from "react-responsive-carousel";
import Typography from "@material-ui/core/Typography";
import Image from 'material-ui-image';
import {beds, getModelIndex} from "../../configs/Products";
import {cardsData, carouselImages} from "../../configs/pages/index.config";
import LayoutWithHeader from "../components/Layout/LayoutWithHeader";
import HomeCard from "../components/Home/HomeCard";

export type CardData = {
    id: string
    src: string,
    title: string,
    text: string,
    alt: string,
};

export default function Home() {
    return (
        <LayoutWithHeader>
            <Container>
                <Box className="hidden md:block">
                    <Carousel autoPlay infiniteLoop showStatus={false} showThumbs={false}>
                        {carouselImages.map(imageSrc => (
                            <Box key={imageSrc}><Image disableSpinner src={imageSrc} aspectRatio={2.3}/></Box>
                        ))}
                    </Carousel>
                </Box>
                <Box className="mt-4">
                    <Grid container={true} spacing={3} justify="space-between">
                        {cardsData.map((cardData_: CardData) =>
                            (<Grid key={cardData_.id} item={true} xs={12} sm={6} md={3}>
                                <HomeCard key={cardData_.title} cardData={cardData_} center={true}/>
                            </Grid>))}
                    </Grid>
                </Box>
                <Box className={"mt-4"}>
                    <Typography align='center' gutterBottom variant="h2">
                        Выбор редакции
                    </Typography>
                    <Grid container={true} justify={"space-between"} spacing={2}>
                        {beds.models.map(product => (
                            <Grid key={product.id} className="mx-auto" item={true} xs={12} sm={6} md={3}>
                                <ModelView key={product.id} modelIndex={getModelIndex(product)} className={"mx-auto"}/>
                            </Grid>))}
                    </Grid>
                </Box>
            </Container>
        </LayoutWithHeader>
    )
}
