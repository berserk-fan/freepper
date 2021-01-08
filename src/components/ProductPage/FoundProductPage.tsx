import { Product } from "@mamat14/shop-server/shop_model";
import { CartState } from "../Cart/Cart";
import Slider from "../Shop/Slider";
import { Box, Button, Typography } from "@material-ui/core";
import Image from "next/image";
import Price from "../Shop/Price";
import CheckCircleOutlineOutlinedIcon from "@material-ui/icons/CheckCircleOutlineOutlined";
import React, { memo } from "react";
import { addProductAction, StoreState } from "../../store";
import { connect } from "react-redux";
import Link from "next/link";
import DogBedDetails from "./DogBedDetails";

const checkMarks = ["Гарантия 2 месяца", "Сделано в Украине"];

function getDetails(product: Product): React.ReactNode {
  switch (product.details.$case) {
    case "dogBed":
      return <DogBedDetails details={product.details.dogBed} />;
    default:
      return <></>;
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

  return (
    <div>
      <div>
        <Slider
          className={"border"}
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
      </div>
      <div className={"flex flex-col gap-2"}>
        <Typography variant={"h4"}>{displayName}</Typography>
        <Price price={price} />
        {getDetails(product)}
        {notInCart ? (
          <>
            <Button
              variant={"contained"}
              color={"secondary"}
              onClick={addToCart}
            >
              Добавить в корзину
            </Button>
            <Button
              variant={"contained"}
              color={"primary"}
              onClick={checkoutNow}
            >
              <Typography>
                <Link href={"/checkout"}>Заказать сейчас</Link>
              </Typography>
            </Button>
          </>
        ) : (
          <>
            <Typography>Товар уже в корзине</Typography>
            <Button
              variant={"contained"}
              color={"primary"}
              onClick={checkoutNow}
            >
              <Typography>
                <Link href={"/checkout"}>Заказать сейчас</Link>
              </Typography>
            </Button>
          </>
        )}

        <ul>
          {checkMarks.map((text) => (
            <li className={"flex gap-1"}>
              <CheckCircleOutlineOutlinedIcon /> <Typography>{text}</Typography>
            </li>
          ))}
        </ul>
      </div>
    </div>
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
