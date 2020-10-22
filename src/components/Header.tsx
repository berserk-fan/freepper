import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Button from "@material-ui/core/Button";
import Link from "next/link";
import {Box, Slide, useScrollTrigger} from "@material-ui/core";

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
                        </ButtonGroup>
                    </Toolbar>
                </AppBar>
            </div>
    );
}
