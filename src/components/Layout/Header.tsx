import React, {useEffect, useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Button from "@material-ui/core/Button";
import Link from "next/link";
import {Box, Dialog, IconButton} from "@material-ui/core";
import ShoppingCartTwoToneIcon from '@material-ui/icons/ShoppingCartTwoTone';
import CartPage, {requestCartProducts} from "../../pages/cart";
import {Product} from "@mamat14/shop-server/shop_model";
import {cartReducer, shopClient} from "../../store";
import CartNoProps from "../Cart/CartNoProps";

const useStyles = makeStyles((theme) => ({
    container: {
        flexGrow: 1,
        position: "relative"
    },
    title: {
        position: "absolute",
        display: "none",
        [theme.breakpoints.up("md")]: {
            display: "block"
        }
    },
}));

export default function Header() {
    const classes = useStyles();
    const [open, setOpen] = useState(false);

    const handleClickOpen = () => {
        setOpen(true);
    };
    const handleClose = () => {
        setOpen(false);
    };
    return (
            <div className={classes.container}>
                <AppBar elevation={0} position="relative" color="transparent">
                    <Toolbar>
                        <Box className={`${classes.title} absolute cursor-default uppercase`}>
                            <Typography variant="h5" noWrap>
                                Погладить можно?
                            </Typography>
                        </Box>
                        <ButtonGroup className="mx-auto" color="primary" aria-label="contained primary button group">
                            <Link href={"/"}>
                                <Button>Домой</Button>
                            </Link>
                            <Link href={"/shop"}>
                                <Button>Магазин</Button>
                            </Link>
                            <Link href={"/about"}>
                                <Button>О нас</Button>
                            </Link>
                            <IconButton size={'medium'} onClick={handleClickOpen}>
                                <ShoppingCartTwoToneIcon fontSize={'large'} />
                            </IconButton>
                        </ButtonGroup>
                        <Dialog onClose={handleClose} aria-labelledby="open-cart-button" open={open}>
                            <CartNoProps/>
                        </Dialog>
                    </Toolbar>
                </AppBar>
            </div>
    );
}
