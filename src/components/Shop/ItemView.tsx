import React, { memo, useState } from "react";
import { fade, makeStyles } from "@material-ui/core/styles";
import Card from "@material-ui/core/Card";
import CardActionArea from "@material-ui/core/CardActionArea";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Link from "next/link";
import { BriefProduct } from "../../types";
import {Box, Collapse, Divider, IconButton} from "@material-ui/core";
import ShoppingCartTwoToneIcon from "@material-ui/icons/ShoppingCartTwoTone";
import { withStyles } from "@material-ui/styles";
import theme from "../../theme";
import { connect } from "react-redux";
import { addProductAction, deleteProductAction, StoreState } from "../../store";
import { Product } from "@mamat14/shop-server/shop_model";
import Image from "next/image";
import "./Slider.module.css";
import Slider from "./Slider";
import { ToggleButton } from "@material-ui/lab";
import Price from "./Price";
import Spacing from "../Commons/Spacing";
import exp from "constants";
import {ExpandLess, ExpandMore} from "@material-ui/icons";

const CartButton = withStyles({
  root: {
    fontSize: "24px",
    borderRadius: "20%",
    "&$selected": {
      color: fade(theme.palette.success.dark, 0.9),
      backgroundColor: fade(theme.palette.success.light, 0.12),
    },
    "&$selected:hover": {
      backgroundColor: fade(theme.palette.success.light, 0.2),
    },
  },
  selected: {},
})(ToggleButton);

function AddToCartButton({
  handleAddedToCart,
  inCart,
}: {
  handleAddedToCart: () => void;
  inCart: boolean;
}) {
  return (
    <CartButton onClick={handleAddedToCart} size="small" selected={inCart}>
      <ShoppingCartTwoToneIcon fontSize={"inherit"} />
    </CartButton>
  );
}

const useStyles = makeStyles({
  media: {
    width: "100%",
    '&::after': {
      content: "",
      display: "block",
      "padding-bottom": "100%"
    }
  },
  expandButton: {
    right: 4,
    top: 2,
    position: "absolute",
  }
});

function ItemView({
  product,
  addProduct,
  deleteProduct,
  className = "",
  inCart,
}: {
  product: Product;
  className?: string;
  addProduct: (product: Product) => void;
  deleteProduct: (id: string) => void;
  inCart: boolean;
}) {
  const classes = useStyles();
  const { id, displayName, description, images } = product;
  const [expandedAndZIndex, setExpandedAndZIndex] = useState<[boolean, number]>([false, 0]);

  function handleAddedToCart() {
    !inCart ? addProduct(product) : deleteProduct(id);
  }

  function handleExpandToggle() {
    setExpandedAndZIndex(([expanded, _]) => {
      const newVal = !expanded;
      return [newVal, newVal ? 100 : 0];
    })
  }

  return (
    <Card className={`overflow-hidden ${className}`}>
      <div style={{zIndex : expandedAndZIndex[1]}}>
        <Slider
            slides={images.map((image) => (
                <Box className={`flex ${classes.media} overflow-hidden items-center`}>
                  <Image width={500} height={500} src={image.src} alt={displayName} objectFit={"cover"}/>
                </Box>
            ))}
        />
        <CardContent style={{ paddingRight: 0}}>
          <Typography variant="h5">{displayName}</Typography>
          <Collapse className={"flex flex-col justify-start relative"} in={expandedAndZIndex[0]} collapsedHeight={48}>
            <Box padding={"2px"}>
              <Box className={classes.expandButton}>
                <IconButton onClick={handleExpandToggle}>
                  {expandedAndZIndex[0] ? <ExpandLess fontSize={"small"}/> : <ExpandMore fontSize={"small"}/>}
                </IconButton>
              </Box>
              <Box style={{hyphens: "auto"}} paddingRight={"48px"}>
                <Typography variant="subtitle2" color="textSecondary" component="p">
                  {description}
                </Typography>
              </Box>
            </Box>
          </Collapse>
        </CardContent>
        <Divider />
        <CardActions>
          <Box
              paddingLeft={1}
              className={"flex w-full justify-between items-center"}
          >
            <Price price={product.price} />
            <Box>
              <Spacing
                  spacing={2}
                  className={"flex flex-row items-center"}
                  wrap={"nowrap"}
              >
                <Link href={product.name}>
                  <Button color={"primary"} variant={"outlined"}>
                    Детали
                  </Button>
                </Link>
                <AddToCartButton {...{ handleAddedToCart, inCart }} />
              </Spacing>
            </Box>
          </Box>
        </CardActions>
      </div>
    </Card>
  );
}

function mapStateToProps(
  state: StoreState,
  { product }: { product: BriefProduct }
) {
  return {
    inCart: !!state.cartState.selectedProducts[product.id],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addProduct: (product) => dispatch(addProductAction(product)),
    deleteProduct: (id) => dispatch(deleteProductAction(id)),
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(memo(ItemView));
