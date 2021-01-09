import { Badge, Box, Dialog, IconButton, Slide } from "@material-ui/core";
import ShoppingCartTwoToneIcon from "@material-ui/icons/ShoppingCartTwoTone";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import CloseIcon from "@material-ui/icons/Close";
import React, { memo, useState } from "react";
import { StoreState } from "../../../store";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import theme from "../../../theme";
import { makeStyles, withStyles } from "@material-ui/styles";
import { TransitionProps } from "@material-ui/core/transitions";
import { CustomAppBar } from "./CustomAppBar";
import Cart, { CartState } from "../../Cart/Cart";
import { connect } from "react-redux";

const StyledBadge = withStyles({
  badge: {
    right: 6,
    top: 21,
    border: `2px solid ${theme.palette.background.paper}`,
    padding: "0 4px",
    color: theme.palette.background.paper,
    backgroundColor: theme.palette.grey["800"],
  },
})(Badge);

const useStyles = makeStyles({
  cart: {
    color: theme.palette.grey["800"],
    marginLeft: "auto",
  },
  closeButton: {
    color: theme.palette.grey["800"],
  },
});

const Transition = React.forwardRef(function Transition(
  props: TransitionProps & { children?: React.ReactElement<any, any> },
  ref: React.Ref<unknown>
) {
  return <Slide direction="up" ref={ref} {...props} />;
});

function HeaderCart({ cartSize }: { cartSize: number }) {
  const classes = useStyles();
  const [open, setOpen] = useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  const isSmallScreen = !useMediaQuery(theme.breakpoints.up("sm"));

  return (
    <>
      <IconButton
        className={classes.cart}
        size={"medium"}
        onClick={handleClickOpen}
      >
        <StyledBadge max={9} badgeContent={cartSize}>
          <ShoppingCartTwoToneIcon fontSize={"large"} />
        </StyledBadge>
      </IconButton>
      <Dialog
        scroll={isSmallScreen ? "body" : "paper"}
        fullScreen={isSmallScreen}
        fullWidth
        maxWidth={"md"}
        onClose={handleClose}
        aria-labelledby="cart-window"
        open={open}
        TransitionComponent={Transition}
      >
        <CustomAppBar>
          <Toolbar className={"flex justify-between"}>
            <Typography variant="h5">Корзина</Typography>
            <IconButton
              edge="start"
              className={classes.closeButton}
              onClick={handleClose}
              aria-label="close-cart-window"
            >
              <CloseIcon fontSize={"large"} />
            </IconButton>
          </Toolbar>
        </CustomAppBar>
        <Box paddingX={2} paddingBottom={2}>
          <Cart />
        </Box>
      </Dialog>
    </>
  );
}

function mapStateToProps(state: StoreState) {
  return {
    cartSize: state.cartState.cartSize,
  };
}

export default connect(mapStateToProps, null)(memo(HeaderCart));
