import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import {DogBed, Product} from "@mamat14/shop-server/shop_model";
import {Box, Typography} from "@material-ui/core";
import {CartProduct} from "../../pages/checkout";
import CartItem from "../Cart/CartItem";

const useStyles = makeStyles({
    table: {
        minWidth: 700,
    },
});

type Row = CartProduct

function rowPrice({price, count}: Row) {
    return price.price * count;
}

function subtotal(cartProducts: Row[]) {
    return cartProducts.reduce((sum, cartProduct) => sum + rowPrice(cartProduct), 0);
}

function ccyFormat(num: number) {
    return `${num}`;
}

type Column = {
    name: string,
    extractor: (product: Product) => string
}

function getSizeName(dogBed: DogBed): string {
    return dogBed.sizes.find(s => s.id == dogBed.sizeId).displayName
}

function getFabricName(dogBed: DogBed): string {
    return dogBed.fabrics.find(f => f.id === dogBed.fabricId).displayName
}

function getColumns(productType: string): Column[] {
    switch (productType) {
        case 'dogBed':
            return [
                {
                    name: 'Размер',
                    extractor: (p) => p.details.dogBed ? getSizeName(p.details.dogBed) : '-'
                },
                {
                    name: 'Ткань',
                    extractor: (p) => p.details.dogBed ? getFabricName(p.details.dogBed) : '-'
                }];
        default:
            return []
    }
}

function getDetailsColumns(productTypes: string[]): Column[] {
    return productTypes.flatMap(getColumns);
}

const strcmp = (a, b) => (a < b ? -1 : (a > b ? 1 : 0));

export const bigSummary = ({cartProducts}: { cartProducts: Row[] }) => {
    const classes = useStyles();
    const invoiceShipping = 0;
    const invoiceSubtotal = subtotal(cartProducts);
    const invoiceTotal = invoiceShipping + invoiceSubtotal;
    const productTypes = [...new Set(cartProducts.map(p => p.details.$case))];
    const detailsColumns = getDetailsColumns(productTypes);
    const sortedProducts = cartProducts.sort((a, b) => strcmp(a.details.$case, b.details.$case));
    const infoColumnsNumber = 2 + detailsColumns.length;
    return (
        <TableContainer component={Paper}>
            <Table className={classes.table} aria-label="spanning table">
                <TableHead>
                    <TableRow>
                        <TableCell>Название</TableCell>
                        {detailsColumns.map(col => <TableCell align="right">{col.name}</TableCell>)}
                        <TableCell align="right">Количество</TableCell>
                        <TableCell align="right">Сумма</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {sortedProducts.map((row) => (
                        <TableRow key={row.displayName}>
                            <TableCell>{row.displayName}</TableCell>
                            {detailsColumns
                                .map(col => col.extractor)
                                .map(e => e(row))
                                .map(detailValue =>
                                    <TableCell align="right">{detailValue}</TableCell>)
                            }
                            <TableCell align="right">{row.count}</TableCell>
                            <TableCell align="right">{ccyFormat(row.price.price)}</TableCell>
                        </TableRow>
                    ))}
                    <TableRow>
                        <TableCell rowSpan={2} colSpan={infoColumnsNumber - 1}/>
                        <TableCell>Без доставки</TableCell>
                        <TableCell align="right">{ccyFormat(invoiceSubtotal)}</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Доставка</TableCell>
                        <TableCell align="right">{invoiceShipping !== 0 ? ccyFormat(invoiceShipping) : 'Бесплатно'}</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell colSpan={infoColumnsNumber}>Итого</TableCell>
                        <TableCell align="right">{ccyFormat(invoiceTotal)}</TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </TableContainer>
    );
};

const smallSummary = ({cartProducts}: { cartProducts: Row[] }) => {
    return (<Box marginTop={2}>
        {cartProducts.length === 0
            ? <Typography variant={'h2'}>Корзина пуста</Typography>
            : cartProducts.map(product => (
                <Box key={product.id} marginY={1}>
                    <CartItem disableControls product={product}/>
                </Box>))
        }
    </Box>)
};

export default smallSummary
