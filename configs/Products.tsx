import {Category, Color, Product, Model} from "../src/model/Model";

export const beds: Category = {
    id: 'beds-category',
    displayName: 'Лежанки',
    description: 'Лежанки для питомцев',
    image: "https://picsum.photos/300/300?random=1",
    models: [
        {
            id: 'lukoshko',
            displayName: 'Лукошко',
            description: 'Очень хорошая лежанка сделанная из качественных материалов.',
            image: "https://picsum.photos/300/300?random=12",
            products: [
                {
                    id: 'lukoshko-grey-xs',
                    displayName: 'Лукошко серое XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=1",
                    color: {id: "1", value: "#445566", displayName: "q"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko-red-xs',
                    displayName: 'Лукошко красное XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=2",
                    color: {id: "2", value: "#FA1233", displayName: "w"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko-violet-xs',
                    displayName: 'Лукошко зеленое XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=3",
                    color: {id: "3", value: "#912376", displayName: "r"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                }
            ]
        },
        {
            id: 'lukoshko2',
            displayName: 'Лукошко',
            description: 'Очень хорошая лежанка сделанная из качественных материалов.',
            image: "https://picsum.photos/300/300?random=13",
            products: [
                {
                    id: 'lukoshko2-grey-xs',
                    displayName: 'Лукошко серое XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=1",
                    color: {id: "1", value: "#445566", displayName: "q"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko2-red-xs',
                    displayName: 'Лукошко красное XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=2",
                    color: {id: "2", value: "#FA1233", displayName: "w"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko2-violet-xs',
                    displayName: 'Лукошко зеленое XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=3",
                    color: {id: "3", value: "#912376", displayName: "r"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko2-violet-m',
                    displayName: 'Лукошко зеленое M',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=3",
                    color: {id: "3", value: "#912376", displayName: "r"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                }
            ]
        },
        {
            id: 'lukoshko3',
            displayName: 'Лукошко',
            description: 'Очень хорошая лежанка сделанная из качественных материалов.',
            image: "https://picsum.photos/300/300?random=14",
            products: [
                {
                    id: 'lukoshko3-grey-xs',
                    displayName: 'Лукошко серое XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/1600/1600?random=1",
                    color: {id: "1", value: "#445566", displayName: "Grey"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko3-red-xs',
                    displayName: 'Лукошко красное XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/1600/900?random=2",
                    color: {id: "2", value: "#FA1233", displayName: "Red"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko3-violet-xs',
                    displayName: 'Лукошко зеленое XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/1600/900?random=3",
                    color: {id: "3", value: "#912376", displayName: "Red"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                },
                {
                    id: 'lukoshko3-violet-M',
                    displayName: 'Лукошко зеленое M',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/1600/1600?random=3",
                    color: {id: "3", value: "#912376", displayName: "Red"},
                    size: {id: "m", displayName: "M", price: 1400.0}
                }
            ]
        },
        {
            id: 'lukoshko4',
            displayName: 'Лукошко',
            description: 'Очень хорошая лежанка сделанная из качественных материалов.',
            image: "https://picsum.photos/300/300?random=15",
            products: [
                {
                    id: 'lukoshko4-grey-xs',
                    displayName: 'Лукошко серое XS',
                    description: 'Хорошая лежанка',
                    image: "https://picsum.photos/300/300?random=1",
                    color: {id: "1", value: "#445566", displayName: "Grey"},
                    size: {id: "xs", displayName: "XS", price: 1400.0}
                }
            ]
        }
    ]
};

export const categories: Category[] = [beds];

export type ModelIndex = {
    id: string,
    displayName: string,
    description: string,
    image: string,
    colors: Color[],
    colorIdToImage: Map<string, string>
}

function groupBy<T, K extends keyof any>(list: T[], getKey: (item: T) => K): Record<K, T[]> {
    return list.reduce((previous, currentItem) => {
        const group = getKey(currentItem);
        if (!previous[group]) previous[group] = [];
        previous[group].push(currentItem);
        return previous;
    }, {} as Record<K, T[]>);
}

const indexes = new Map<string, ModelIndex>();

export function getModelIndex(model: Model): ModelIndex {
    let cached = indexes.get(model.id);
    if (!!cached) {
        return cached;
    }
    const {products} = model;
    const colors = products.map((item) => item.color).reduce<[Color[], Set<string>]>(
        ([colors, metColorIds], curColor) => {
            if (!metColorIds.has(curColor.id)) {
                metColorIds.add(curColor.id);
                colors.push(curColor);
            }
            return [colors, metColorIds]
        }, [[], new Set<string>()])[0];
    let entries: [string, Product[]][] = Object.entries(groupBy(products, (item) => item.color.id));
    const imagesForColors: Map<string, string> = new Map(
        entries.map(([colorId, items]: [string, Product[]]) => {
            const imagesForColor = items.map((item: Product) => item.image);
            return [colorId, imagesForColor[0]]
        }));
    const res = {...model, colors: colors, colorIdToImage: imagesForColors};
    indexes.set(model.id, res);
    return res;
}

function getProductIdToModel(): Map<string, Model> {
    const res = new Map<string, Model>();
    for(const category of categories) {
        for(const model of category.models) {
            for(const product of model.products) {
                res.set(product.id, model);
            }
        }
    }
    return res;
}

export const productIdsToModel: Map<string, Model> = getProductIdToModel();
