import React, {memo} from "react";
import {makeStyles} from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Button from "@material-ui/core/Button";
import Link from "next/link";
import {Box} from "@material-ui/core";
import theme from "../../../theme";
import HeaderMenu from "./HeaderMenu";
import HeaderCart from "./HeaderCart";

const useStyles = makeStyles(({
    title: {
        marginLeft: 'auto',
        [theme.breakpoints.up("md")]: {
            marginLeft: '0',
        }
    },
    mainButtonGroup: {
        position: 'absolute',
        width: 220,
        marginLeft: theme.spacing(2),
        marginRight: 'auto',
        left: 0,
        right: 0,
        textAlign: 'center',
        justifyContent: 'center',
        display: 'none',
        [theme.breakpoints.up("md")]: {
            marginLeft: 'auto',
            display: 'flex',
        }
    },
    toolbar: {
        position: 'relative',
        display: 'flex',
        justifyContent: 'center',
    }
}));

export const CustomAppBar = ({children}) => {
    return <AppBar elevation={1} position="relative" color="transparent">
        {children}
    </AppBar>
};

export default memo(function Header() {
    const classes = useStyles();
    return (
        <CustomAppBar>
            <Toolbar className={classes.toolbar}>
                <HeaderMenu/>
                <Box className={`${classes.title} cursor-default uppercase`}>
                    <Typography variant="h5" noWrap>
                        Погладить можно?
                    </Typography>
                </Box>
                <ButtonGroup className={`${classes.mainButtonGroup}`} color="primary" aria-label="page tabs">
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
                <HeaderCart/>
            </Toolbar>
        </CustomAppBar>
    );
})
