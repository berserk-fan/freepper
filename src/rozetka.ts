import { Category, Product, ImageData } from "@mamat14/shop-server/shop_model";
import convert, { ElementCompact, Options } from "xml-js";
import showdown from 'showdown';
import {ProductKey} from "./configs/beds/commons";

const DogsAndBedsCategoryId = "beds_and_begs";

function toGroups(p: Product[]): ElementCompact {
  const r = p.reduce((prev, cur) => {
    prev[cur.modelName] = cur;
    return prev;
  }, {} as Record<string, Product>);

  return Object.entries(r).map(([modelName, product]) => ({
            _attributes: {
                id: modelName,
                    var_param_id: "size,colour"
            },
            _text: product.displayName
        }))
}

function toFiller(p: Product): string {
  switch (p.modelName as ProductKey) {
    case "chemodan":
      return "Холлофайбер, Синтепон";
    case "kvadroSoft":
      return "Холлофайбер";
    case "kvadroStrong":
      return "Холлофайбер";
    case "lukoshkoDuo":
      return "Холлофайбер";
    case "lukoshkoTrio":
      return "Холлофайбер";
    default:
      throw new Error("unmatched product id" + p.id);
  }
}

function toMartindale(p: Product): string {
  switch (p.modelName as ProductKey) {
    case "chemodan":
      return "50000 циклов";
    case "kvadroSoft":
      return "30000 циклов";
    case "kvadroStrong":
      return "50000 циклов";
    case "lukoshkoDuo":
      return "30000 циклов";
    case "lukoshkoTrio":
      return "30000 циклов";
    default:
      throw new Error("unmatched product id" + p.id);
  }
}

function toDensity(p: Product): string | null {
  switch (p.modelName as ProductKey) {
    case "chemodan":
      return "370 г/м2";
    case "kvadroSoft":
      return null;
    case "kvadroStrong":
      return "370г/м2";
    case "lukoshkoDuo":
      return null;
    case "lukoshkoTrio":
      return null;
    default:
      throw new Error("unmatched product id" + p.id);
  }
}

function toColourDefense(p: Product): string {
  switch (p.modelName as ProductKey) {
    case "chemodan":
      return "5/5";
    case "kvadroSoft":
      return "4/5";
    case "kvadroStrong":
      return "5/5";
    case "lukoshkoDuo":
      return "4/5";
    case "lukoshkoTrio":
      return "4/5";
    default:
      throw new Error("unmatched product id" + p.id);
  }
}

function waterResistance(p: Product): string {
  switch (p.modelName as ProductKey) {
    case "chemodan":
      return "да";
    case "kvadroSoft":
      return "медленное впитывание";
    case "kvadroStrong":
      return "да";
    case "lukoshkoDuo":
      return "медленное впитывание";
    case "lukoshkoTrio":
      return "медленное впитывание";
    default:
      throw new Error("unmatched product id" + p.id);
  }
}

const nonSingleItemImages = [
  "Dogs-7161",
  "Dogs-7169",
  "Dogs-7173",
  "Dogs-7268",
  "Dogs-25157",
];

function realImages(imgs: ImageData[]): ImageData[] {
  return imgs
    .filter((img) => img.src.includes("/beds"))
    .filter(
      (img) =>
        !nonSingleItemImages.find((badImage) => img.src.includes(badImage)),
    );
}

const converter = new showdown.Converter();
function toOffer(p: Product): ElementCompact | undefined {
  const imgs = realImages(p.images);
  if (imgs.length === 0) {
    return undefined;
  }
  const detail = p.details.dogBed;
  const {sizes} = detail;
  const fillerName = toFiller(p);
  let size = p.details.dogBed.sizes.find(
    (x) => x.id == p.details.dogBed.sizeId,
  );
  let fabric = detail.fabrics.find((f) => f.id == detail.fabricId);
  return {
    _attributes: {
      id: p.id.replace(/-/g, ""),
      available: "true",
      groupId: p.modelName,
    },
    stock_quantity: 1,
    price: p.price.price,
    currencyId: "UAH",
    categoryId: DogsAndBedsCategoryId,
    picture: imgs.map(
      (image) =>
        `https://pogladit-mozhno.vercel.app/_next/image?url=${encodeURIComponent(
          image.src,
        )}&w=3840&q=85`,
    ),
    vendor: "Погладь можно?",
    name: `Лежанка Погладить можно? ${p.displayName} ${size.displayName}(${size.description}) ${fabric.displayName} (${p.id})`,
    description: {
      _cdata: converter.makeHtml(p.description),
    },
    param: [
      {
        _attributes: {
          name: "Цвет",
          paramid: "сolour",
        },
        _text: p.details.dogBed.fabrics.find(
          (x) => x.id == p.details.dogBed.fabricId,
        ).displayName,
      },
      {
        _attributes: {
          name: "Размер",
          paramid: "size",
        },
        _text: size.displayName + "(" + size.description + ")",
      },
      {
        _attributes: {
          name: "Ткань",
        },
        _text: "Микрофибра",
      },
      {
        _attributes: {
          name: "Страна-производитель товара",
        },
        _text: "Украина",
      },
      {
        _attributes: {
          name: "Страна-регистрации бренда",
        },
        _text: "Украина",
      },
      {
        _attributes: {
          name: "Гарантия",
        },
        _text: "30 дней",
      },
      {
        _attributes: {
          name: "Наполнитель",
        },
        _text: fillerName,
      },
      {
        _attributes: {
          name: "Тип",
        },
        _text: "Лежак",
      },
      {
        _attributes: {
          name: "Назначение",
        },
        _text: "Универсальные",
      },
      {
        _attributes: {
          name: "Статус товара",
        },
        _text: "Есть в наличии",
      },
      {
        _attributes: {
          name: "Тест на стирание по Мартиндейлу",
        },
        _text: toMartindale(p),
      },
      {
        _attributes: {
          name: "Цветостойкость",
        },
        _text: toColourDefense(p),
      },
      {
        _attributes: {
          name: "Водоотталкивание",
        },
        _text: waterResistance(p),
      },
    ].concat(
      toDensity(p) == null
        ? []
        : [
            {
              _attributes: {
                name: "Плотность ткани",
              },
              _text: toDensity(p),
            },
          ],
    ),
  };
}

export function toRozetkaXml(
  categories: Category[],
  products: Product[],
): string {
  const options: Options.JS2XML = {
    compact: true,
    ignoreComment: true,
    spaces: 4,
    fullTagEmptyElement: true,
    indentCdata: true,
    indentInstruction: true,
  };

  return (
    `${`<?xml version="1.0" encoding="UTF-8"?>\n` +
            `<!DOCTYPE yml_catalog SYSTEM "shops.dtd">\n` +
            `<yml_catalog date="2021-05-30 20:36">\n`}${  convert.js2xml({
        shop: {
            name: {
                _text: "ZooHugge"
            },
            company: {
                _text: "ФОП Маматюсупова Л.О."
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
                group: toGroups(products)
            },
            offers: {
                offer: products.map(toOffer).filter(x => !!x)
            }
        }
    }, options)  }\n</yml_catalog>`);
}
