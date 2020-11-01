import {useRouter} from "next/router";
import React, {useState} from "react";
import {Box, Button, Container, Fade, Grid, Paper, Typography} from "@material-ui/core";
import {getModelIndex, productIdsToModel} from "../../../configs/Products";
import {Color, Model} from "../../model/Model";
import ColorPicker from "../../components/ColorPicker";
import ShoppingCartIcon from '@material-ui/icons/ShoppingCart';
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import "keen-slider/keen-slider.min.css";
import {Carousel} from "../../components/Product/Carousel";
import {Flex} from "../../components/styled";
import styled from 'styled-components'


const ProductName = styled(Flex)`
	color:black;
	background-color:white;
	font-weight:bold;
	position:absolute;
	z-index:1;
	padding:8px 16px;
	left:10px;
	top:5px;
`

const Price = styled(Flex)`
	color:black;
	background-color:white;
	font-weight:bold;
	position:absolute;
	z-index:1;
	padding: 5px 10px;
	left:45px;
	top:45px;
	font-size:14px;
`

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
	const {colors} = getModelIndex(model);
	const images = model.products.map(({image}) => image);
	const product = model.products.find(({id}) => id === productId);

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

	const [selectedColor, setSelectedColor] = useState(colors[0]);
	const [showSelectedColorName, setShowSelectedColorName] = useState(true);

	return (
		<LayoutWithHeader>
			<Container>
				<Flex m={[15, -15]}>
					<Flex grow position={'relative'} p={[0, 15]}>
						<ProductName>
							{product.displayName}
						</ProductName>
						<Price>
							₴{product.size.price} UAH
						</Price>
						<Carousel images={images}/>
					</Flex>
					<Grid item xs={12} md={6}>
						<Flex m={[15, 0]} column justify={'center'}>
							<Flex align={'center'} justify={'center'}>
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
							</Flex>
							<ColorPicker
								colors={colors}
								itemId={model.id}
								onChange={colorChanged}
							/>
						</Flex>
						<Flex m={[15, 0]} justify={'center'}>
							<Button
								startIcon={<ShoppingCartIcon/>}
								size={'large'}
								color={'primary'}
								variant={'contained'}
							>
								Добавить в корзину
							</Button>
						</Flex>
					</Grid>
				</Flex>
			</Container>
		</LayoutWithHeader>
	)
}
