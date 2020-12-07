import { Product } from "@mamat14/shop-server/shop_model";
import React, { useState } from "react";
import {
    Box, Button,
    FormControl,
    InputLabel,
    MenuItem,
    Select, Typography,
} from "@material-ui/core";

type Subject = "PRICE" | "ALPHABET";
type Direction = "ASC" | "DESC";
type Sorting = [Subject, Direction];

const sortings: Sorting[] = ["PRICE", "ALPHABET"].flatMap((s: Subject) =>
  ["DESC", "ASC"].map<Sorting>((d: Direction) => [s, d])
);

function getSortingFunction([s,d]: Sorting): (p1: Product, p2: Product) => number {
    if(s == "PRICE" && d == "ASC") {
        return (a,b) => (a.price.price - b.price.price);
    }
    if(s == "PRICE" && d == "DESC") {
        return (b,a) => (a.price.price - b.price.price);
    }
    if(s == "ALPHABET" && d == "DESC") {
        return (p1,p2) => p1.displayName.localeCompare(p2.displayName)
    }
    if(s == "ALPHABET" && d == "ASC") {
        return (p2,p1) => p1.displayName.localeCompare(p2.displayName)
    }
    return (a,b) => 0;
}

function getOptionName([s, d]: Sorting): string {
  if(s == "PRICE" && d == "ASC") {
      return "От дешевых к дорогим";
  }
    if(s == "PRICE" && d == "DESC") {
        return "От дорогих к дешевым";
    }
    if(s == "ALPHABET" && d == "DESC") {
        return "От А до Я";
    }
    if(s == "ALPHABET" && d == "ASC") {
        return "От Я до А";
    }
    return "Неизвестно";
}

export default function ShopControls({
  products,
  setProducts,
}: {
  products: Product[];
  setProducts: (products: Product[]) => void;
}) {
    const [sorting, setSorting] = useState<Sorting>(null);
    function handleSorting(ev) {
        const sorting = JSON.parse(ev.target.value as string) as Sorting
        setSorting(sorting)
        setProducts([...products].sort(getSortingFunction(sorting)))
    }

  return (
    products && (
        <Box marginY={1} className={"flex "}>
            <FormControl style={{width: '50%'}} variant={"outlined"}>
                <InputLabel id="products-sorting-label">Сортировка</InputLabel>
                <Select
                    labelId="products-sorting-label"
                    id="products-soring"
                    value={(sorting && JSON.stringify(sorting)) || ""}
                    label={"Сортировка"}
                    displayEmpty
                    onChange={handleSorting}
                >
                    {sortings.map((s) => (
                        <MenuItem value={JSON.stringify(s)}>{getOptionName(s)}</MenuItem>
                    ))}
                </Select>
            </FormControl>
            <Box paddingLeft={1} width={'50%'} className={"items-stretch"}>
                <Button className={"w-full h-full"} variant={'outlined'}>
                    <Typography>
                        Фильтры
                    </Typography>
                </Button>
            </Box>
        </Box>)
  );
}
