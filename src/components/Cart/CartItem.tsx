import {Product} from "@mamat14/shop-server/shop_model";
import {Box, Typography} from "@material-ui/core";
import React from "react";

export default function CartItem({product}: { product: Product }) {
    return (
        <Box>
            <Typography variant={'h3'}>{product.displayName}</Typography>
        </Box>
    );
}
