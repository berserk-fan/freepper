import {Box, Dialog, Fab, IconButton, Slide, Theme, useTheme} from "@material-ui/core";

import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import CloseIcon from "@material-ui/icons/Close";
import React, { memo, useState } from "react";
import { StoreState } from "../../../store";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { makeStyles, withStyles } from "@material-ui/styles";
import { TransitionProps } from "@material-ui/core/transitions";
import { CustomAppBar } from "./CustomAppBar";
import { connect } from "react-redux";
import ShoppingCartIcon from "../../Icons/ShoppingCartIcon";
import Cart from "../../Cart/Cart";
import Badge from "@material-ui/core/Badge";

const StyledBadge = withStyles((theme: Theme) => ({
  badge: {
    right: 6,
    top: 30,
    border: `1px solid ${theme.palette.grey["800"]}`,
    padding: "0 4px",
    color: theme.palette.grey["800"],
    backgroundColor: theme.palette.background.default,
  },
}))(Badge);

const useStyles = makeStyles((theme: Theme) => ({
  cart: {
    marginLeft: "auto",
    color: theme.palette.grey["800"],
  },
  closeButton: {
    color: theme.palette.grey["800"],
  },
}));

const Transition = React.forwardRef(function Transition(
  props: TransitionProps & { children?: React.ReactElement<any, any> },
  ref: React.Ref<unknown>
) {
  return <Slide direction="up" ref={ref} {...props} />;
});

function HeaderCart({ cartSize }: { cartSize: number }) {
  const classes = useStyles();
  const theme = useTheme();
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
      <Fab
        color={"primary"}
        className={classes.cart}
        size={"large"}
        onClick={handleClickOpen}
        aria-label="open cart"
      >
        <StyledBadge max={9} badgeContent={cartSize}>
          <ShoppingCartIcon fontSize={"large"} />
        </StyledBadge>
      </Fab>

      <Dialog
        scroll={isSmallScreen ? "body" : "paper"}
        fullScreen={isSmallScreen}
        fullWidth
        maxWidth={"md"}
        onClose={handleClose}
        open={open}
        TransitionComponent={Transition}
      >
        <CustomAppBar show>
          <Toolbar className={"flex justify-between"}>
            <Typography variant="h5">Корзина</Typography>
            <IconButton
              className={classes.closeButton}
              onClick={handleClose}
              aria-label="close cart"
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
