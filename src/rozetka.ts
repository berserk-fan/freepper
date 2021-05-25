import {Category, Product} from "@mamat14/shop-server/shop_model";
import convert, {ElementCompact, Options} from "xml-js";

const DogsAndBedsCategoryId = "beds_and_begs";

function toOffer(p: Product): ElementCompact {
    return {
        _attributes: {
            id: p.id.replace(/-/g,""),
            available: "true"
        },
        stock_quantity: 1,
        price: p.price.price,
        currencyId: "UAH",
        categoryId: DogsAndBedsCategoryId,
        picture: p.images.map(image => `https://pogladit-mozhno.vercel.app/_next/image?url=${encodeURIComponent(image.src)}&w=500&q=85`),
        vendor: 'ФОП Маматюсупова Л.О.',
        name: p.displayName + `(${p.id})`,
        description: p.description,
        param: [
            {
                _attributes: {
                    name: "Ткань",
                    paramid: "fabrics",
                    valueid: p.details.dogBed.fabricId
                },
                _text: p.details.dogBed.fabrics.find(x => x.id == p.details.dogBed.fabricId).displayName
            },
            {
                _attributes: {
                    name: "Размер",
                    paramid: "sizes",
                    valueid: p.details.dogBed.sizeId
                },
                _text: p.details.dogBed.sizes.find(x => x.id == p.details.dogBed.sizeId).displayName
            }
        ]
    }
}

export function toRozetkaXml(categories: Category[], products: Product[]): string {
    const options: Options.JS2XML = {compact: true,
        ignoreComment: true,
        spaces: 4,
        fullTagEmptyElement: true,
        indentCdata: true,
        indentInstruction: true,
    };

    return convert.js2xml({
        shop: {
            name: {
                _text: "ФОП Маматюсупова Л.О."
            },
            company: {
                _text: "Погладить можно"
            },
            categories: {
                category: [
                    {
                        _attributes: {
                            "id": "beds_and_begs"
                        },
                        _text: "Спальные места и переноски"
                    }
                ]
            },
            currencies: {
                currency: [
                    {
                        _attributes: {
                            id: "UAH",
                            rate: "1"
                        }
                    }
                ]
            },
            groups: {
                group: [
                    {
                        _attributes: {
                            id: "chemodan",
                            var_param_id: "sizes,fabrics"
                        },
                        _text: "Чемодан"
                    },
                ]
            },
            offers: {
                offer: products.map(toOffer)
            }
        }
    }, options);
}
