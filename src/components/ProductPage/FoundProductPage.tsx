import { Product } from "@mamat14/shop-server/shop_model";
import { CartState } from "../Cart/Cart";
import Slider from "../Shop/Slider";
import {Box, Button, Container, Divider, Grid, Typography} from "@material-ui/core";
import Image from "next/image";
import Price from "../Shop/Price";
import CheckCircleOutlineOutlinedIcon from "@material-ui/icons/CheckCircleOutlineOutlined";
import React, { memo } from "react";
import { addProductAction, StoreState } from "../../store";
import { connect } from "react-redux";
import Link from "next/link";
import DogBedDetails from "./DogBedDetails";
import Spacing from "../Commons/Spacing";

const checkMarks = ["Гарантия 2 месяца", "Сделано в Украине"];

function getDetails(product: Product): React.ReactNode {
  switch (product.details.$case) {
    case "dogBed":
      return <>
        <DogBedDetails details={product.details.dogBed} />
        </>;
    default:
      return false;
  }
}

function ProductPage({
  product,
  cart,
  addProduct,
}: {
  product: Product;
  cart: CartState;
  addProduct: (product: Product) => void;
}) {
  const { id, displayName, images, price, details } = product;
  const notInCart = !cart.selectedProducts[product.id];

  function checkoutNow(ev): void {
    if (notInCart) {
      addProduct(product);
    }
  }
  function addToCart() {
    addProduct(product);
  }

    const productDetailsPart = getDetails(product);
    return (
    <Box marginX={"auto"} maxWidth={"500px"}>
        <Slider
          className={"border rounded overflow-hidden"}
          slides={images.map((image) => (
            <Box className={`flex overflow-hidden items-center`}>
              <Image
                width={500}
                height={500}
                src={image.src}
                alt={displayName}
              />
            </Box>
          ))}
        />
      <Spacing spacing={1} className={"flex flex-col"} childClassName={"w-full"}>
          <Typography variant={"h2"}>{displayName}</Typography>
          <Price price={price} />
          {productDetailsPart && <Divider/>}
          {productDetailsPart}
          {productDetailsPart && <Divider/>}
          {notInCart ? (
              <Button variant={"contained"} color={"secondary"} onClick={addToCart} fullWidth>
                  Добавить в корзину
              </Button>
          ) : (
              <Typography variant={"overline"}>Товар уже в корзине</Typography>
          )}
          <Button variant={"contained"} color={"primary"} onClick={checkoutNow} fullWidth>
              <Typography>
                  <Link href={"/checkout"}>Заказать сейчас</Link>
              </Typography>
          </Button>
          <ul>
              {checkMarks.map((text) => (
                  <li className={"flex"}>
                      <CheckCircleOutlineOutlinedIcon /> <Typography>{text}</Typography>
                  </li>
              ))}
          </ul>
          <Divider/>
          <Typography variant={"h5"}>
              Описание
              <Box marginLeft={1} paddingTop={0}>
                  <Typography className={"pt-0"}>
                      {product.description}
                  </Typography>
              </Box>
          </Typography>
      </Spacing>
    </Box>
  );
}

function mapStateToProps(store: StoreState) {
  return {
    cart: store.cartState,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addProduct: (a1) => dispatch(addProductAction(a1)),
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(memo(ProductPage));
