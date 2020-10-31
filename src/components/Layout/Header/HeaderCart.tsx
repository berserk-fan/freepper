import {Badge, Box, Dialog, IconButton} from "@material-ui/core";
import ShoppingCartTwoToneIcon from "@material-ui/icons/ShoppingCartTwoTone";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import CloseIcon from "@material-ui/icons/Close";
import CartNoProps from "../../Cart/CartNoProps";
import React, {useState} from "react";
import {cartReducer} from "../../../store";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import theme from "../../../theme";
import {makeStyles, withStyles} from "@material-ui/styles";
import {CustomAppBar} from "./Header";

const StyledBadge = withStyles(({
    badge: {
        right: 6,
        top: 21,
        border: `1px solid ${theme.palette.background.paper}`,
        padding: '0 4px',
    },
}))(Badge);
const useStyles = makeStyles(({
    cart: {
        marginLeft: 'auto',
    }
}));

export default function HeaderCart() {
    const classes = useStyles();
    const [open, setOpen] = useState(false);

    function calcCartSize() {
        return cartReducer.getState().selectedProducts
            .reduce((a, b) => (a + b.count), 0)
    }

    const [cartSize, setCartSize] = useState<number>(calcCartSize());
    cartReducer.subscribe(() => {
        setCartSize(calcCartSize())
    });

    const handleClickOpen = () => {
        setOpen(true);
    };
    const handleClose = () => {
        setOpen(false);
    };

    const isSmallScreen = !useMediaQuery(theme.breakpoints.up('sm'));

    return (<>
        <IconButton className={classes.cart} color={'primary'} size={'medium'} onClick={handleClickOpen}>
            <StyledBadge max={9} badgeContent={cartSize} color="primary">
                <ShoppingCartTwoToneIcon fontSize={'large'}/>
            </StyledBadge>
        </IconButton>
        <Dialog scroll={isSmallScreen ? 'body' : 'paper'} fullScreen={isSmallScreen} fullWidth maxWidth={'md'} onClose={handleClose} aria-labelledby="cart-window" open={open}>
            <CustomAppBar>
                <Toolbar className={"flex justify-between"}>
                    <Typography variant="h5">
                        Корзина
                    </Typography>
                    <IconButton edge="start" color="secondary" onClick={handleClose} aria-label="close-cart-window">
                        <CloseIcon fontSize={'large'}/>
                    </IconButton>
                </Toolbar>
            </CustomAppBar>
            <Box paddingX={2} paddingBottom={2}>
                <CartNoProps/>
            </Box>
        </Dialog>
    </>)
}
