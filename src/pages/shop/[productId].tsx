import 'react-responsive-carousel/lib/styles/carousel.min.css';
import {useRouter} from "next/router";
import React from "react";
import {Box, Button, Container, Fade, Grid, Paper, styled, Typography} from "@material-ui/core";
import {getModelIndex, productIdsToModel} from "../../../configs/Products";
import {Color, Model} from "../../model/Model";
import {Carousel, Thumbs} from "react-responsive-carousel";
import Image from 'material-ui-image';
import ColorPicker from "../../components/ColorPicker";
import ProductPrice from "../../components/ProductPrice";
import ShoppingCartIcon from '@material-ui/icons/ShoppingCart';

const BuyButton = styled(Button)({});

export default function ProductPage() {
    const router = useRouter();
    const {productId} = router.query;
    if (!productId) {
        return false;
    }
    const model: Model = productIdsToModel.get(productId as string);
    if (!model) {
        return <h1>Page Not Found</h1>
    }
    const modelIndex = getModelIndex(model);
    const images = model.products.map((p) => p.image);
    const product = model.products.find((product) => product.id === productId);
    const colors = modelIndex.colors;

    const colorTextTransitionTime = 200;

    function colorChanged(color: Color) {
        setShowSelectedColorName(false);
        setTimeout(
            () => {
                setShowSelectedColorName(true);
                setSelectedColor(color);
            },
            colorTextTransitionTime + 100
        )
    }

    const [selectedItem, setSelectedItem] = React.useState(null);
    const [selectedColor, setSelectedColor] = React.useState(colors[0]);
    const [showSelectedColorName, setShowSelectedColorName] = React.useState(true);


    const galleryHeight = 300;
    const gallery =
        <Paper>
            <Carousel swipeable showStatus={false} onChange={setSelectedItem} selectedItem={selectedItem}
                      showThumbs={false}>
                {images.map((image) =>
                    <Box style={{height: galleryHeight}}>
                        <img className={"object-scale-down"} style={{height: galleryHeight}} src={image}/>
                    </Box>)}
            </Carousel>
            <Thumbs selectedItem={selectedItem} onSelectItem={setSelectedItem} thumbWidth={60}>
                {images.map((image) => <Image disableSpinner src={image} cover={true}/>)}
            </Thumbs>
        </Paper>;

    const buyButton =
        <BuyButton startIcon={<ShoppingCartIcon/>} size={'large'} color={'primary'} variant={'contained'}>
            <Box>
                <Typography variant={'button'}>Купить</Typography>
            </Box>
        </BuyButton>;

    const actionsBlock =
        <Paper className={"p-1"}>
            <Box className={"m-1 px-1"}>
                <Box className={"px-1"}>
                    <Typography display={'inline'} variant={'caption'} color={'textSecondary'}>
                        Цвет:
                    </Typography>
                    <Fade in={showSelectedColorName} timeout={colorTextTransitionTime}>
                        <Box component='span' pl={"4px"}>
                            <Typography display={'inline'} variant={'subtitle1'} color={'textPrimary'}>
                                {selectedColor.displayName}
                            </Typography>
                        </Box>
                    </Fade>
                </Box>
                <ColorPicker colors={colors} itemId={model.id} onChange={colorChanged}/>
            </Box>

            <Paper className={"m-1"}>
                <Box p={2} className="flex flex-row gap-2">
                    <ProductPrice price={product.size.price}/>
                    <Box pl={3}>
                        {buyButton}
                    </Box>
                </Box>
            </Paper>
        </Paper>;


    return <Container>
        <Box className={"w-full"}>
            <Typography className={`py-4`} variant={'h4'}>
                {product.displayName}
            </Typography>
        </Box>
        <Grid container={true} spacing={3}>
            <Grid item={true} xs={12} md={6} spacing={3}>
                {gallery}
            </Grid>
            <Grid item xs={12} md={6}>
                {actionsBlock}
            </Grid>
        </Grid>
    </Container>
}
