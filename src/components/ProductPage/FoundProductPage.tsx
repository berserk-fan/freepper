import { Product } from "@mamat14/shop-server/shop_model";
import Image from "next/image";
import CheckCircleOutlineOutlinedIcon from "@material-ui/icons/CheckCircleOutlineOutlined";
import React, { memo } from "react";
import { connect } from "react-redux";
import Link from "next/link";
import { makeStyles } from "@material-ui/styles";
import AddShoppingCartIcon from "@material-ui/icons/AddShoppingCart";
import dynamic from "next/dynamic";
import Fab from "@material-ui/core/Fab";
import Typography from "@material-ui/core/Typography";
import useTheme from "@material-ui/core/styles/useTheme";
import Box from "@material-ui/core/Box";
import Divider from "@material-ui/core/Divider";
import Zoom from "@material-ui/core/Zoom";
import Price from "../Shop/Price";
import { addProductAction, CartState, StoreState } from "../../store";
import SliderThumbs from "../Shop/SliderThumbs";
import Spacing from "../Commons/Spacing";
import DogBedDetails from "./DogBedDetails";

const Markdown = dynamic(() => import("../Markdown/Renderers"));

const checkMarks = ["Гарантия 2 месяца", "Сделано в Украине"];

function Details({
  categoryName,
  product,
}: {
  categoryName: string;
  product: Product;
}) {
  switch (product.details.$case) {
    case "dogBed":
      return (
        <DogBedDetails
          categoryName={categoryName}
          details={product.details.dogBed}
        />
      );
    default:
      return <></>;
  }
}

const useStyles = makeStyles({
  fab: {
    width: "100%",
  },
});

type MakeFabProps = {
  href?: string;
  onClick?: () => void;
  color: "secondary";
  icon?: any;
  className: string;
  label: string;
  style?: any;
};

function MakeFab({
  icon,
  label,
  className,
  onClick,
  href,
  color,
  style,
}: MakeFabProps) {
  const innerPart = (
    <Fab
      className={className}
      color={color}
      variant="extended"
      onClick={onClick}
      style={style}
    >
      {icon} <Typography variant="button">{label}</Typography>
    </Fab>
  );
  return href ? <Link href={href}>{innerPart}</Link> : innerPart;
}

function ProductPage({
  product,
  cart,
  addProduct,
  categoryName,
}: {
  categoryName: string;
  product: Product;
  cart: CartState;
  addProduct: (product: Product) => void;
}) {
  const classes = useStyles();
  const theme = useTheme();
  const { displayName, images, price } = product;
  const inCart = !!cart.selectedProducts[product.id];
  function addToCart() {
    addProduct(product);
  }

  const fabs = [
    {
      key: "fabAddToCart",
      show: !inCart,
      color: "secondary" as "secondary",
      className: classes.fab,
      icon: <AddShoppingCartIcon />,
      label: "Добавить в корзину",
      onClick: addToCart,
    },
    {
      key: "fabCheckout",
      show: inCart,
      color: "secondary" as "secondary",
      className: classes.fab,
      label: "Заказать сейчас",
      href: "/checkout",
    },
  ];

  const transitionDuration = {
    enter: theme.transitions.duration.enteringScreen,
    exit: theme.transitions.duration.leavingScreen,
  };

  return (
    <Box marginX="auto" maxWidth="500px" padding={1}>
      <SliderThumbs
        slides={images.map((image, idx) => (
          <Box key={image.src} className="flex overflow-hidden items-center">
            <Image
              priority={idx === 0}
              width={500}
              height={500}
              src={image.src}
              alt={displayName}
              layout="intrinsic"
            />
          </Box>
        ))}
        thumbs={images.map((image) => (
          <Box key={image.src} className="flex overflow-hidden items-center">
            <Image width={75} height={75} src={image.src} alt={displayName} />
          </Box>
        ))}
      />
      <Spacing spacing={1} className="flex flex-col" childClassName="w-full">
        <Typography variant="h4" component="h1">
          {displayName}
        </Typography>
        <Typography variant="h5">
          <Price price={price} />
        </Typography>
        <Divider />
        <Details categoryName={categoryName} product={product} />
        <Divider />
        <Box width="100%" height="50px">
          {(inCart ? fabs : fabs.reverse()).map((fab) => (
            <Zoom
              key={fab.key}
              in={fab.show}
              timeout={transitionDuration}
              style={{
                transitionDelay: `${fab.show ? transitionDuration.exit : 0}ms`,
              }}
              mountOnEnter
              unmountOnExit
            >
              <MakeFab {...fab} />
            </Zoom>
          ))}
        </Box>
        <ul>
          {checkMarks.map((text) => (
            <li key={text} className="flex">
              <CheckCircleOutlineOutlinedIcon /> <Typography>{text}</Typography>
            </li>
          ))}
        </ul>
        <Divider />
        <Typography variant="h4">Описание</Typography>
        <Box marginLeft={1} paddingTop={0}>
          <Markdown>{product.description}</Markdown>
        </Box>
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
