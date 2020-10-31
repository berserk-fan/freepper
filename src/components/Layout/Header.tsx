import React, {useEffect, useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Button from "@material-ui/core/Button";
import Link from "next/link";
import {Box, Dialog, Divider, IconButton} from "@material-ui/core";
import ShoppingCartTwoToneIcon from '@material-ui/icons/ShoppingCartTwoTone';
import CloseIcon from '@material-ui/icons/Close';
import CartNoProps from "../Cart/CartNoProps";
import useMediaQuery from '@material-ui/core/useMediaQuery';
import theme from "../../theme";

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

const CustomAppBar = ({children}) => {
    return <AppBar elevation={1} position="relative" color="transparent">
        {children}
    </AppBar>
};

export default function Header() {
    const classes = useStyles();
    const [open, setOpen] = useState(false);

    const handleClickOpen = () => {
        setOpen(true);
    };
    const handleClose = () => {
        setOpen(false);
    };

    //TODO: fix behaves like md
    const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));

    return (
            <div className={classes.container}>
                <CustomAppBar>
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
                        </ButtonGroup>
                        <IconButton size={'medium'} onClick={handleClickOpen}>
                            <ShoppingCartTwoToneIcon fontSize={'large'} />
                        </IconButton>
                        <Dialog scroll={isSmallScreen ? 'body': 'paper'} fullScreen={isSmallScreen} fullWidth maxWidth={'md'} onClose={handleClose} aria-labelledby="cart-window" open={open}>
                            <CustomAppBar>
                                <Toolbar className={"flex justify-between"}>
                                    <Typography variant="h5">
                                        Корзина
                                    </Typography>
                                    <IconButton edge="start" color="inherit" onClick={handleClose} aria-label="close-cart-window">
                                        <CloseIcon fontSize={'large'} />
                                    </IconButton>
                                </Toolbar>
                            </CustomAppBar>
                            <Box paddingX={2} paddingBottom={2}>
                                <CartNoProps/>
                            </Box>
                        </Dialog>
                    </Toolbar>
                </CustomAppBar>
            </div>
    );
}
