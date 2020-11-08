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
  Avatar,
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
import { OrderForm } from "./CheckoutForm";
import {
  getDeliveryOptionName,
  getDeliveryProviderName,
} from "./DeliveryDetailsStep";

function rowPrice({ price, count }: CartProduct) {
  return price.price * count;
}

function subtotal(cartProducts: CartProduct[]) {
  return cartProducts.reduce(
    (sum, cartProduct) => sum + rowPrice(cartProduct),
    0
  );
}

function ccyFormat(num: number) {
  return `${num}`;
}

type Column<T> = {
  name: string;
  extractor: (product: T) => string;
};

function getSizeName(dogBed: DogBed): string {
  return dogBed.sizes.find((s) => s.id == dogBed.sizeId).displayName;
}

function getFabricName(dogBed: DogBed): string {
  return dogBed.fabrics.find((f) => f.id === dogBed.fabricId).displayName;
}

function getColumns(productType: string): Column<CartProduct>[] {
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

function getDetailsColumns(productTypes: string[]): Column<CartProduct>[] {
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
  detailsColumns: Column<CartProduct>[];
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

function ProductDetailsTable({
  detailsColumns,
  row,
}: {
  detailsColumns: Column<CartProduct>[];
  row: Product & { count: number };
}) {
  const classes = useRowStyles();
  return (
    <Box className={"flex"}>
      <Avatar alt="Remy Sharp" src="/Dogs-7051.jpg" />
      <Box className={"flex-grow"} paddingLeft={1}>
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
            <TableRow classes={classes}>
              {detailsColumns.map((col) => (
                <TableCell key={col.name} align="right">
                  {col.extractor(row)}
                </TableCell>
              ))}
            </TableRow>
          </TableBody>
        </Table>
      </Box>
    </Box>
  );
}

function CompactRow({
  row,
  detailsColumns,
}: {
  row: CartProduct;
  detailsColumns: Column<CartProduct>[];
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
            color={"secondary"}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell style={{ paddingLeft: 0, paddingRight: 0 }}>
          {row.displayName}
        </TableCell>
        <TableCell align={"right"}>{ccyFormat(row.price.price)}</TableCell>
      </TableRow>
      <TableRow>
        <TableCell
          style={{ paddingBottom: 0, paddingTop: 0 }}
          colSpan={detailsColumns.length}
        >
          <Collapse in={open} timeout="auto" unmountOnExit>
            <ProductDetailsTable {...{ detailsColumns, row }} />
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
}

const formSummaryColumns: Column<OrderForm>[] = [
  {
    name: "Имя",
    extractor: (t: OrderForm) => t?.deliveryDetails?.fullName || "Отсутствует",
  },
  {
    name: "Телефон",
    extractor: (t: OrderForm) => t?.deliveryDetails?.phone || "Отсутствует",
  },
  {
    name: "Способ доставки",
    extractor: (t: OrderForm) =>
      getDeliveryOptionName(t?.deliveryDetails?.option) || "Отсутствует",
  },
  {
    name: "Служба доставки",
    extractor: (t: OrderForm) =>
      getDeliveryProviderName(t?.deliveryDetails?.provider) || "Отсутствует",
  },
  {
    name: "Адрес",
    extractor: (t: OrderForm) => {
      const address = t?.deliveryDetails?.address;
      if (!address) {
        return "Отсутствует";
      }
      if (address.match(/[0-9]+/)) {
        return `Отделение номер ${address}`;
      } else {
        return address;
      }
    },
  },
];

const bigSummary = ({
  cartProducts,
  orderForm,
}: {
  cartProducts: CartProduct[];
  orderForm: OrderForm;
}) => {
  console.log(orderForm);
  const invoiceShipping = 0;
  const invoiceSubtotal = subtotal(cartProducts);
  const invoiceTotal = invoiceShipping + invoiceSubtotal;
  const productTypes = [...new Set(cartProducts.map((p) => p.details.$case))];
  const detailsColumns: Column<CartProduct>[] = [
    ...getDetailsColumns(productTypes),
    {
      name: "Количество",
      extractor: (p: CartProduct) => p.count.toString(),
    },
  ];
  const sortedProducts = cartProducts.sort((a, b) =>
    strcmp(a.details.$case, b.details.$case)
  );
  const fullWidth = useMediaQuery(theme.breakpoints.up("sm"));
  const infoColumnsNumber = 2 + (fullWidth ? detailsColumns.length : 0);
  const tableSize = fullWidth ? "medium" : "small";
  return (
    <>
      <Box>
        <TableContainer
          component={(p) => <Paper variant={"outlined"} {...p} />}
        >
          <Table size={tableSize}>
            <TableHead>
              <TableRow>
                <TableCell colSpan={4}>
                  <Typography variant={"h6"}>Данные для доставки</Typography>
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {formSummaryColumns.map((col) => (
                <TableRow key={col.name}>
                  <TableCell
                    style={{
                      paddingLeft: theme.spacing(1),
                      paddingRight: theme.spacing(0.5),
                    }}
                    colSpan={1}
                  >
                    {col.name}
                  </TableCell>
                  <TableCell
                    style={{ paddingLeft: theme.spacing(1) }}
                    colSpan={3}
                  >
                    {col.extractor(orderForm)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
      <Box marginTop={1}>
        <TableContainer
          component={(p) => <Paper variant={"outlined"} {...p} />}
        >
          <Table size={tableSize} aria-label="spanning table">
            <TableHead>
              <TableRow>
                <TableCell colSpan={fullWidth ? detailsColumns.length + 2 : 3}>
                  <Typography variant={"h6"}>Данные о заказе</Typography>
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell colSpan={fullWidth ? 1 : 2}>Название</TableCell>
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
                  <Row_
                    key={row.id}
                    row={row}
                    detailsColumns={detailsColumns}
                  />
                );
              })}
              <TableRow>
                <TableCell
                  rowSpan={2}
                  colSpan={fullWidth ? infoColumnsNumber - 1 : 1}
                />
                <TableCell>Без доставки</TableCell>
                <TableCell align="right">
                  {ccyFormat(invoiceSubtotal)}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell style={{ paddingRight: 4 }}>Доставка</TableCell>
                <TableCell style={{ paddingLeft: 0 }} align="right">
                  {invoiceShipping !== 0
                    ? ccyFormat(invoiceShipping)
                    : "Бесплатно"}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell colSpan={infoColumnsNumber}>Итого</TableCell>
                <TableCell align="right">{ccyFormat(invoiceTotal)}</TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </>
  );
};

const smallSummary = ({ cartProducts }: { cartProducts: CartProduct[] }) => {
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

export default function SummaryStep(props: {
  cartProducts: CartProduct[];
  orderForm: OrderForm;
}) {
  if (props.cartProducts.length <= 2) {
    return smallSummary(props);
  } else {
    return bigSummary(props);
  }
}
