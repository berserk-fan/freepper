import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import CloseIcon from "@mui/icons-material/Close";
import React, { memo, useState } from "react";
import useMediaQuery from "@mui/material/useMediaQuery";
import { TransitionProps } from "@mui/material/transitions";
import { connect } from "react-redux";
import Badge from "@mui/material/Badge";
import { StoreState } from "store";
import useTheme from "@mui/styles/useTheme";
import withStyles from "@mui/styles/withStyles";
import makeStyles from "@mui/styles/makeStyles";
import Slide from "@mui/material/Slide";
import Fab from "@mui/material/Fab/Fab";
import Dialog from "@mui/material/Dialog/Dialog";
import IconButton from "@mui/material/IconButton";
import Box from "@mui/material/Box";
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
    props: TransitionProps & { children: React.ReactElement<any, any> },
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
                size="large"
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
