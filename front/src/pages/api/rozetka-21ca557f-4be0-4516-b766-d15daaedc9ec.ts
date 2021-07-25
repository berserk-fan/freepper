import { NextApiRequest, NextApiResponse } from "next";
import { Category, Product, ImageData } from "apis/catalog";
import convert, { ElementCompact, Options } from "xml-js";
import showdown from "showdown";
import { categories, all_products } from "configs/catalog/beds";
import { ProductKey } from "../../configs/catalog/defs";
import { checkExhaustive } from "../../commons/utils";

const DogsAndBedsCategoryId = "beds_and_begs";

function toGroups(p: Product[]): ElementCompact {
  const r = p.reduce(
    (prev, cur) => ({ ...prev, ...{ [cur.modelId]: cur } }),
    {} as Record<string, Product>,
  );

  return Object.entries(r).map(([modelName, product]) => ({
    _attributes: {
      id: modelName,
      var_param_id: "size,colour",
    },
    _text: product.displayName,
  }));
}

function toFiller(p: Product): string | null {
  const mId = p.modelId as ProductKey;
  switch (mId) {
    case "podushka":
      return "Холлофайбер";
    case "kreslo":
      return null;
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
    case "kolorado":
      return "Холлофайбер";
    case "norka":
      return "Холлофайбер, Мебельный параллон";
    default:
      return checkExhaustive(mId);
  }
}

function toMartindale(p: Product): string | null {
  const mId = p.modelId as ProductKey;
  switch (mId) {
    case "norka":
      return "50000 циклов";
    case "podushka":
      return "30000 циклов";
    case "kolorado":
      return "50000 циклов";
    case "kreslo":
      return null;
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
      return checkExhaustive(mId);
  }
}

function toDensity(p: Product): string | null {
  const mId = p.modelId as ProductKey;
  switch (mId) {
    case "norka":
      return null;
    case "podushka":
      return null;
    case "kolorado":
      return null;
    case "kreslo":
      return null;
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
      return checkExhaustive(mId);
  }
}

function toColourDefense(p: Product): string | null {
  const mId = p.modelId as ProductKey;
  switch (mId) {
    case "norka":
      return "5/5";
    case "podushka":
      return "4/5";
    case "kolorado":
      return "5/5";
    case "kreslo":
      return null;
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
      return checkExhaustive(mId);
  }
}

enum WaterResistance {
  YES = 1,
  NO = 2,
  SLOW = 3,
}

function waterResistanceToString(w: WaterResistance) {
  switch (w) {
    case WaterResistance.YES:
      return "Да";
    case WaterResistance.NO:
      return "Нет";
    case WaterResistance.SLOW:
      return "Медленное впитывание";
    default:
      return checkExhaustive(w);
  }
}

function toProductType(p: Product): string {
  const mId = p.modelId as ProductKey;
  switch (mId) {
    case "kreslo":
      return "Автокресло";
    default:
      return "Лежанка";
  }
}
function waterResistance(p: Product): WaterResistance {
  const mId = p.modelId as ProductKey;
  switch (mId) {
    case "norka":
      return WaterResistance.YES;
    case "podushka":
      return WaterResistance.YES;
    case "kolorado":
      return WaterResistance.YES;
    case "kreslo":
      return WaterResistance.NO;
    case "chemodan":
      return WaterResistance.YES;
    case "kvadroSoft":
      return WaterResistance.SLOW;
    case "kvadroStrong":
      return WaterResistance.YES;
    case "lukoshkoDuo":
      return WaterResistance.SLOW;
    case "lukoshkoTrio":
      return WaterResistance.SLOW;
    default:
      return checkExhaustive(mId);
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
  const { size, fabrics, fabricId, sizes } = p.details.dogBed;
  const sizeDetails = sizes[size];
  const fabric = fabrics.find((f) => f.id === fabricId);
  const vendor = "Погладить можно?";
  const pType = toProductType(p);
  const name = `${pType} ${vendor} ${p.displayName} ${size}(${sizeDetails.description}) ${fabric.displayName} (${p.id})`;
  const picture = imgs
    .map((image) => encodeURIComponent(image.src))
    .map(
      (srcEncoded) =>
        `https://pogladit-mozhno.vercel.app/_next/image?url=${srcEncoded}&w=3840&q=100`,
    );
  const description = {
    _cdata: converter.makeHtml(p.description),
  };
  const param = [
    {
      _attributes: {
        name: "Цвет",
        paramid: "сolour",
      },
      _text: fabric.displayName,
    },
    {
      _attributes: {
        name: "Размер",
        paramid: "size",
      },
      _text: `${size}(${sizeDetails.description})`,
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
      _text: toFiller(p),
    },
    {
      _attributes: {
        name: "Тип",
      },
      _text: pType,
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
      _text: waterResistanceToString(waterResistance(p)),
    },
    {
      _attributes: {
        name: "Плотность ткани",
      },
      _text: toDensity(p),
    },
    // eslint-disable-next-line no-underscore-dangle
  ].filter((x) => x._text != null);
  return {
    _attributes: {
      id: p.id.replace(/-/g, ""),
      available: "true",
      groupId: p.modelId,
    },
    stock_quantity: 1,
    price: p.price.price,
    currencyId: "UAH",
    categoryId: DogsAndBedsCategoryId,
    picture,
    vendor,
    name,
    description,
    param,
  };
}

export function toRozetkaXml(
  categories1: Category[],
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

  return `${
    `<?xml version="1.0" encoding="UTF-8"?>\n` +
    `<!DOCTYPE yml_catalog SYSTEM "shops.dtd">\n` +
    `<yml_catalog date="2021-05-30 20:36">\n`
  }${convert.js2xml(
    {
      shop: {
        name: {
          _text: "ZooHugge",
        },
        company: {
          _text: "ФОП Маматюсупова Л.О.",
        },
        categories: {
          category: [
            {
              _attributes: {
                id: "beds_and_begs",
              },
              _text: "Спальные места и переноски",
            },
          ],
        },
        currencies: {
          currency: [
            {
              _attributes: {
                id: "UAH",
                rate: "1",
              },
            },
          ],
        },
        groups: {
          group: toGroups(products),
        },
        offers: {
          offer: products.map(toOffer).filter((x) => !!x),
        },
      },
    },
    options,
  )}\n</yml_catalog>`;
}

export default async function postOrderHandler(
  req: NextApiRequest,
  res: NextApiResponse<string>,
) {
  return res.send(toRozetkaXml(categories, all_products));
}
