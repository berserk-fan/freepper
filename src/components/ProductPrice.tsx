import React from "react";
import {Typography} from "@material-ui/core";

export default function ProductPrice(props: {price: number, currency?: string}) {
    const {price, currency} = props;
    return (<Typography display={'inline'} variant={'h4'}>{`${price} ${currency || '₴'}`}</Typography>)
}
