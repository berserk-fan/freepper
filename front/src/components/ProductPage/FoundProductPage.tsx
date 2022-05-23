import CheckCircleOutlineOutlinedIcon from "@material-ui/icons/CheckCircleOutlineOutlined";
import React, { memo } from "react";
import { connect } from "react-redux";
import Link from "next/link";
import AddShoppingCartIcon from "@material-ui/icons/AddShoppingCart";
import dynamic from "next/dynamic";
import Fab from "@material-ui/core/Fab";
import Typography from "@material-ui/core/Typography";
import useTheme from "@material-ui/core/styles/useTheme";
import Box from "@material-ui/core/Box";
import Divider from "@material-ui/core/Divider";
import Zoom from "@material-ui/core/Zoom";
import { addProductAction, CartProduct, CartState, StoreState } from "store";
import Grid from "@material-ui/core/Grid";
import Container from "@material-ui/core/Container";
import { Model } from "apis/model.pb";
import { Product } from "apis/product.pb";
import SliderWithThumbs from "../SliderWithThumbs";
import Spacing from "../Commons/Spacing";
import Price from "../Shop/Price";
import { createSizes, SizesSpec } from "../../commons/sizes";
import ParameterPicker from "./ParameterPicker";

const Markdown = dynamic(() => import("../Markdown/Renderers"));

const sizesSpec: SizesSpec = {
  xs: 12,
  md: 6,
};
const SIZES = createSizes(sizesSpec);

const checkMarks = ["Гарантия 2 месяца", "Сделано в Украине"];

type MakeFabProps = {
  href?: string;
  onClick?: () => void;
  icon?: any;
  label: string;
  style?: any;
};

const MakeFab = React.memo(
  ({ icon, label, onClick, href, style }: MakeFabProps) => {
    const innerPart = (
      <Fab
        color="secondary"
        variant="extended"
        onClick={onClick}
        style={style}
        component="a"
      >
        {icon} <Typography variant="button">{label}</Typography>
      </Fab>
    );
    return href ? <Link href={href}>{innerPart}</Link> : innerPart;
  },
);

function ProductPage({
  model,
  products,
  cart,
  addProduct,
}: {
  model: Model;
  products: Product[];
  cart: CartState;
  addProduct: (value: CartProduct) => void;
}) {
  const theme = useTheme();
  const { displayName } = model;

  // we need it to find product using parameterIds
  const indexed: Record<string, Product> = React.useMemo(
    () =>
      Object.fromEntries(
        products.map((p) => [
          p.parameterIds.sort((a, b) => a.localeCompare(b)).join(),
          p,
        ]),
      ),
    [products],
  );

  // we need to filter out paramters for which we doesn't have products
  const parameterLists = model.parameterLists.map((paramList) => ({
    ...paramList,
    parameters: paramList.parameters.filter(
      (param) =>
        products.findIndex((p) => p.parameterIds.indexOf(param.uid) !== -1) !==
        -1,
    ),
  }));

  const [curParamIds, setCurParamIds] = React.useState(
    Object.fromEntries(parameterLists.map((x) => [x.uid, x.parameters[0].uid])),
  );

  const newProductId = Object.values(curParamIds)
    .sort((a, b) => a.localeCompare(b))
    .join();

  const product = indexed[newProductId];
  const { images } = product.imageList;
  const inCart = !!cart.selectedProducts[product.uid];

  function addToCart() {
    addProduct({ product, model, count: 1 });
  }

  const fabs = [
    {
      key: "fabAddToCart",
      show: !inCart,
      icon: <AddShoppingCartIcon />,
      label: "Добавить в корзину",
      onClick: addToCart,
    },
    {
      key: "fabCheckout",
      show: inCart,
      label: "Заказать сейчас",
      href: "/checkout",
    },
  ];

  const transitionDuration = {
    enter: theme.transitions.duration.enteringScreen,
    exit: theme.transitions.duration.leavingScreen,
  };

  return (
    <Container disableGutters>
      <Grid container spacing={2}>
        <Grid item {...sizesSpec}>
          <SliderWithThumbs images={images} thumbs={images} sizes={SIZES} />
        </Grid>
        <Grid
          item
          {...sizesSpec}
          style={{ maxWidth: "500px", marginRight: "auto" }}
        >
          <Spacing
            spacing={1}
            className="flex flex-col"
            childClassName="w-full"
          >
            <Typography variant="h4" component="h1">
              {displayName}
            </Typography>
            <Typography variant="h5">
              <Price price={product.price.standard} />
            </Typography>
            <Divider />
            <div>
              {parameterLists.map((parameterList) => (
                <ParameterPicker
                  key={parameterList.uid}
                  parameterList={parameterList}
                  selectedParameterId={curParamIds[parameterList.uid]}
                  onChange={(newId) =>
                    setCurParamIds((prev) => ({
                      ...prev,
                      [parameterList.uid]: newId,
                    }))
                  }
                />
              ))}
            </div>
            <Divider />
            <Box width="100%" height="50px">
              {(inCart ? fabs : fabs.reverse()).map((fab) => (
                <Zoom
                  key={fab.key}
                  in={fab.show}
                  timeout={transitionDuration}
                  style={{
                    transitionDelay: `${
                      fab.show ? transitionDuration.exit : 0
                    }ms`,
                    width: "100%",
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
                  <CheckCircleOutlineOutlinedIcon />
                  <Typography>{text}</Typography>
                </li>
              ))}
            </ul>
            <Divider />
            <Typography variant="h4">Описание</Typography>
            <Box marginLeft={1} paddingTop={0}>
              <Markdown>{model.description}</Markdown>
            </Box>
          </Spacing>
        </Grid>
      </Grid>
    </Container>
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
