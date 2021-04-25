import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import CloseIcon from "@material-ui/icons/Close";
import React, { memo, useState } from "react";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { TransitionProps } from "@material-ui/core/transitions";
import { connect } from "react-redux";
import Badge from "@material-ui/core/Badge";
import { StoreState } from "store";
import useTheme from "@material-ui/core/styles/useTheme";
import withStyles from "@material-ui/styles/withStyles";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Slide from "@material-ui/core/Slide/Slide";
import Fab from "@material-ui/core/Fab/Fab";
import Dialog from "@material-ui/core/Dialog/Dialog";
import IconButton from "@material-ui/core/IconButton/IconButton";
import Box from "@material-ui/core/Box/Box";
import Cart from "../../Cart/Cart";
import ShoppingCartIcon from "../../Icons/ShoppingCartIcon";
import { CustomAppBar } from "./CustomAppBar";

const StyledBadge = withStyles((theme) => ({
  badge: {
    right: 6,
    top: 30,
    border: `1px solid ${theme.palette.grey["800"]}`,
    padding: "0 4px",
    color: theme.palette.grey["800"],
    backgroundColor: theme.palette.background.default,
  },
}))(Badge);

const useStyles = makeStyles((theme) => ({
  cart: {
    marginLeft: "auto",
    color: theme.palette.grey["800"],
  },
  closeButton: {
    color: theme.palette.grey["800"],
  },
}));

const Transition = React.forwardRef(
  (
    props: TransitionProps & { children?: React.ReactElement<any, any> },
    ref: React.Ref<unknown>,
  ) => <Slide direction="up" ref={ref} {...props} />,
);

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
        color="primary"
        className={classes.cart}
        size="large"
        onClick={handleClickOpen}
        aria-label="open cart"
      >
        <StyledBadge max={9} badgeContent={cartSize}>
          <ShoppingCartIcon fontSize="large" />
        </StyledBadge>
      </Fab>

      {open && (
        <Dialog
          scroll={isSmallScreen ? "body" : "paper"}
          fullScreen={isSmallScreen}
          fullWidth
          maxWidth="md"
          onClose={handleClose}
          open={open}
          TransitionComponent={Transition}
        >
          <CustomAppBar show>
            <Toolbar className="flex justify-between">
              <Typography variant="h5">Корзина</Typography>
              <IconButton
                className={classes.closeButton}
                onClick={handleClose}
                aria-label="close cart"
              >
                <CloseIcon fontSize="large" />
              </IconButton>
            </Toolbar>
          </CustomAppBar>
          <Box paddingX={2} paddingBottom={2}>
            <Cart />
          </Box>
        </Dialog>
      )}
    </>
  );
}

function mapStateToProps(state: StoreState) {
  return {
    cartSize: state.cartState.cartSize,
  };
}

export default connect(mapStateToProps, null)(memo(HeaderCart));
