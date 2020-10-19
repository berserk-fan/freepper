import ModelView from "../src/components/ModelView";

require('react-responsive-carousel/lib/styles/carousel.min.css');
import React from "react";
import {Box, Container, Grid} from "@material-ui/core";
import {Carousel} from "react-responsive-carousel";
import Typography from "@material-ui/core/Typography";
import Image from 'material-ui-image';
import {beds, getModelIndex} from "../configs/Products";
import {cardsData, carouselImages} from "../configs/pages/index.config";

export type CardData = {
    id: string
    src: string,
    title: string,
    text: string,
    alt: string,
};

function HomeCard(props: { cardData: CardData, center: boolean }) {
    const {src, alt, title, text} = props.cardData;
    return (
        <Box
            className={"max-w-sm rounded overflow-hidden shadow-lg" + (props.center ? " mx-auto" : "")}>
            <Image src={src} aspectRatio={2.0}/>
            <div className="px-6 py-4">
                <Typography gutterBottom={true} variant={"h6"}> {title} </Typography>
                <Typography color={"textSecondary"} variant={"body2"}> {text} </Typography>
            </div>
        </Box>)
}

export default function Home() {
    return (
        <div>
            <Container>
                <Box className="hidden md:block mt-4">
                    <Carousel showArrows={true} showThumbs={false}>
                        {carouselImages.map(imageSrc =>
                            <Box key={imageSrc}><Image src={imageSrc} aspectRatio={2.3}/></Box>)}
                    </Carousel>
                </Box>
                <Box className="mt-4">
                    <Grid container={true} spacing={3} justify="space-between">
                        {cardsData.map((cardData_: CardData, index: number) =>
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
        </div>)
}
