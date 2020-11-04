import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import { DogBed, Product } from "@mamat14/shop-server/shop_model";
import {
  Box,
  Collapse,
  IconButton,
  Typography,
  useMediaQuery,
} from "@material-ui/core";
import { CartProduct } from "../../pages/checkout";
import CartItem from "../Cart/CartItem";
import KeyboardArrowDownIcon from "@material-ui/icons/KeyboardArrowDown";
import KeyboardArrowUpIcon from "@material-ui/icons/KeyboardArrowUp";
import theme from "../../theme";

type Row = CartProduct;

function rowPrice({ price, count }: Row) {
  return price.price * count;
}

function subtotal(cartProducts: Row[]) {
  return cartProducts.reduce(
    (sum, cartProduct) => sum + rowPrice(cartProduct),
    0
  );
}

function ccyFormat(num: number) {
  return `${num}`;
}

type Column = {
  name: string;
  extractor: (product: Row) => string;
};

function getSizeName(dogBed: DogBed): string {
  return dogBed.sizes.find((s) => s.id == dogBed.sizeId).displayName;
}

function getFabricName(dogBed: DogBed): string {
  return dogBed.fabrics.find((f) => f.id === dogBed.fabricId).displayName;
}

function getColumns(productType: string): Column[] {
  switch (productType) {
    case "dogBed":
      return [
        {
          name: "Размер",
          extractor: (p) =>
            p.details.dogBed ? getSizeName(p.details.dogBed) : "-",
        },
        {
          name: "Ткань",
          extractor: (p) =>
            p.details.dogBed ? getFabricName(p.details.dogBed) : "-",
        },
      ];
    default:
      return [];
  }
}

function getDetailsColumns(productTypes: string[]): Column[] {
  return productTypes.flatMap(getColumns);
}

const strcmp = (a, b) => (a < b ? -1 : a > b ? 1 : 0);

const useRowStyles = makeStyles({
  root: {
    "& > *": {
      borderBottom: "unset",
    },
  },
});

function StandardRow({
  row,
  detailsColumns,
}: {
  row: CartProduct;
  detailsColumns: Column[];
}) {
  return (
    <TableRow key={row.displayName}>
      <TableCell>{row.displayName}</TableCell>
      {detailsColumns.map((col) => (
        <TableCell align="right">{col.extractor(row)}</TableCell>
      ))}
      <TableCell align="right">{row.count}</TableCell>
      <TableCell align="right">{ccyFormat(row.price.price)}</TableCell>
    </TableRow>
  );
}

function DetailsTable({
  detailsColumns,
  row,
}: {
  detailsColumns: Column[];
  row: Product & { count: number };
}) {
  return (
    <Table padding={"none"} size="small" aria-label="purchases">
      <TableHead>
        <TableRow>
          {detailsColumns.map((col) => (
            <TableCell key={col.name} align="right">
              {col.name}
            </TableCell>
          ))}
        </TableRow>
      </TableHead>
      <TableBody>
        <TableRow>
          {detailsColumns.map((col) => (
            <TableCell key={col.name} align="right">
              {col.extractor(row)}
            </TableCell>
          ))}
        </TableRow>
      </TableBody>
    </Table>
  );
}

function CompactRow({
  row,
  detailsColumns,
}: {
  row: CartProduct;
  detailsColumns: Column[];
}) {
  const [open, setOpen] = React.useState(false);
  const classes = useRowStyles();
  return (
    <>
      <TableRow key={row.id} className={classes.root}>
        <TableCell padding={"none"} align={"center"}>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell style={{ paddingLeft: 0 }}>{row.displayName}</TableCell>
        <TableCell align={"right"}>{ccyFormat(row.price.price)}</TableCell>
      </TableRow>
      <TableRow>
        <TableCell
          style={{ paddingBottom: 0, paddingTop: 0 }}
          colSpan={detailsColumns.length}
        >
          <Collapse in={open} timeout="auto" unmountOnExit>
            <DetailsTable {...{ detailsColumns, row }} />
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
}

const bigSummary = ({ cartProducts }: { cartProducts: Row[] }) => {
  const invoiceShipping = 0;
  const invoiceSubtotal = subtotal(cartProducts);
  const invoiceTotal = invoiceShipping + invoiceSubtotal;
  const productTypes = [...new Set(cartProducts.map((p) => p.details.$case))];
  const detailsColumns: Column[] = [
    ...getDetailsColumns(productTypes),
    {
      name: "Количество",
      extractor: (p: Row) => p.count.toString(),
    },
  ];
  const sortedProducts = cartProducts.sort((a, b) =>
    strcmp(a.details.$case, b.details.$case)
  );
  const fullWidth = useMediaQuery(theme.breakpoints.up("sm"));
  const infoColumnsNumber = 2 + (fullWidth ? detailsColumns.length : 0);
  const tableSize = fullWidth ? "medium" : "small";
  return (
    <TableContainer component={Paper}>
      <Table size={tableSize} aria-label="spanning table">
        <TableHead>
          <TableRow>
            {fullWidth ? false : <TableCell />}
            <TableCell style={fullWidth ? {} : { paddingLeft: 0 }} size="small">
              Название
            </TableCell>
            {fullWidth && (
              <>
                {detailsColumns.map((col) => (
                  <TableCell key={col.name} align="right">
                    {col.name}
                  </TableCell>
                ))}
                <TableCell align="right">Количество</TableCell>
              </>
            )}
            <TableCell align="right">Сумма</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {sortedProducts.map((row) => {
            const Row_ = fullWidth ? StandardRow : CompactRow;
            return (
              <Row_ key={row.id} row={row} detailsColumns={detailsColumns} />
            );
          })}
          <TableRow>
            <TableCell
              rowSpan={2}
              colSpan={fullWidth ? infoColumnsNumber - 1 : 1}
            />
            <TableCell>Без доставки</TableCell>
            <TableCell align="right">{ccyFormat(invoiceSubtotal)}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Доставка</TableCell>
            <TableCell align="right">
              {invoiceShipping !== 0 ? ccyFormat(invoiceShipping) : "Бесплатно"}
            </TableCell>
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

const smallSummary = ({ cartProducts }: { cartProducts: Row[] }) => {
  return (
    <Box marginTop={2}>
      {cartProducts.length === 0 ? (
        <Typography variant={"h2"}>Корзина пуста</Typography>
      ) : (
        cartProducts.map((product) => (
          <Box key={product.id} marginY={1}>
            <CartItem product={product} />
          </Box>
        ))
      )}
    </Box>
  );
};

export default function Summary(props: { cartProducts: Row[] }) {
  if (props.cartProducts.length <= 2) {
    return smallSummary(props);
  } else {
    return bigSummary(props);
  }
}
