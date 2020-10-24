//deprecated

export type Category = {
    id: string,
    displayName: string,
    description: string,
    image: string,
    models: OldModel[]
}

export type OldModel = {
    id: string,
    displayName: string,
    description: string,
    image: string,
    products: Product[]
};

export type Product = {
    id: string,
    displayName: string,
    description: string,
    image: string,
    color: Color,
    size: ProductSize
}

export type Color = {
    id: string,
    value: string,
    displayName: string,
};

export type ProductSize = {
    id: string,
    displayName: string,
    price: number,
};
