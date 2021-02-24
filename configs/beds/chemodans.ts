import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import avFabrics from "../fabrics/avFabrics";
import chemodanSizes, {ChemodanSizeKeys} from "../sizes/chemodanSizes";
import {makeProductName} from "./commons";

function getVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of avFabrics) {
    for (const size of chemodanSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: `products/chemodan-${fabric.id}-${size.id}`,
      });
    }
  }
  return res;
}

const variants: DogBed_Variant[] = getVariants();

const folder = "/beds/chemodan";
export const chemodanImages = {
    "chemodanYellow0": {
        src: `${folder}/IMG_4965.JPG`,
        alt: "Собака на желтом чемодане",
        name: makeProductName("chemodan", "av-07", "chemodan-m"),
    },
    "chemodanEmerald0": {
        src: `${folder}/Dogs-25020.jpg`,
        alt: "Чемодан зеленый",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "blackChemodan": {
        src: `${folder}/Dogs-7078.jpg`,
        alt: "Чемодан черный ",
        name: makeProductName("chemodan", "av-18", "chemodan-m"),
    },
    "chemodanHandles": {
        src: `${folder}/Dogs-7161.jpg`,
        alt: "ручки чемодана",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanHandles2": {
        src: `${folder}/Dogs-7169.jpg`,
        alt: "Чемодан ручки",
        name: makeProductName("chemodan", "av-07", "chemodan-m"),
    },
    "chemodanLabel": {
        src: `${folder}/Dogs-7173.jpg`,
        alt: "Чемодан этикетка",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanHandle": {
        src: `${folder}/Dogs-7180 (2).jpg`,
        alt: "Чемодан ручка",
        name: makeProductName("chemodan", "av-07", "chemodan-m"),
    },
    "chemodanHandle2": {
        src: `${folder}/Dogs-7196 (1).jpg`,
        alt: "Чемодан ручка",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanEmerald": {
        src: `${folder}/Dogs-7255 (1).jpg`,
        alt: "Лежанка Чемодан изумрудного цвета",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanEmerald2": {
        src: `${folder}/Dogs-7258 (1).jpg`,
        alt: "Лежанка Чемодан изумрудного цвета",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanYellowAndEmerald": {
        src: `${folder}/Dogs-7268 (2).jpg`,
        alt: "Изумрудный и желтый чемодан",
        name: makeProductName("chemodan", "av-07", "chemodan-m"),
    },
    "chemodanYellow": {
        src: `${folder}/Dogs-7271.jpg`,
        alt: " Желтый чемодан",
        name: makeProductName("chemodan", "av-07", "chemodan-m"),
    },
    "chemodanCorgi": {
        src: `${folder}/Dogs-24800.jpg`,
        alt: "Желтый чемодан с корги",
        name: makeProductName("chemodan", "av-07", "chemodan-m"),
    },
    "chemodanEmerald3": {
        src: `${folder}/Dogs-24947 (2).jpg`,
        alt: " Чемодан зеленый",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanWithDog": {
        src: `${folder}/Dogs-24967 (2).jpg`,
        alt: "Лежанка Чемодан изумрудного цвета с собакой вид спереди",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanWithDogAndGirl": {
        src: `${folder}/Dogs-25034.jpg`,
        alt: "Собака и девочка на лежанке Чемодан изумрудного цвета",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanWithLabrador": {
        src: `${folder}/Dogs-25044 (1).jpg`,
        alt: "Белый лабрадор на лежанке Чемодан черного цвета",
        name: makeProductName("chemodan", "av-18", "chemodan-m"),
    },
    "chemodanBeagleLabradorAndDog": {
        src: `${folder}/Dogs-25071.jpg`,
        alt: "Лабрадор, бигль и девочка на лежанке Чемодан Черного цвета",
        name: makeProductName("chemodan", "av-18", "chemodan-m"),
    },
    "chemodans": {
        src: `${folder}/Dogs-25157.jpg`,
        alt: "Чемоданы цвета изумруд, желтый и синий зодиак с мопсом сверху",
        name: makeProductName("chemodan", "av-18", "chemodan-m"),
    },
    "chemodanBiggleAndGirl": {
        src: `${folder}/IMG_4961.jpg`,
        alt: "Чемодан цвета изумруд, бигль и девочка",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    },
    "chemodanBiggleAndGirl2": {
        src: `${folder}/IMG_4964.jpg`,
        alt: "Чемодан цвета изумруд, бигль и девочка",
        name: makeProductName("chemodan", "av-12", "chemodan-m"),
    }
};

const fabricToImage = {
    "av-01": [],
    "av-02": [],
    "av-04": [],
    "av-06": [],
    "av-07": [
        chemodanImages.chemodanHandles,
        chemodanImages.chemodanHandles2,
        chemodanImages.chemodanLabel,
        chemodanImages.chemodanYellowAndEmerald,
        chemodanImages.chemodanYellow,
        chemodanImages.chemodanCorgi,
        chemodanImages.chemodanYellow0,
    ],
    "av-10": [],
    "av-11": [],
    "av-12": [
        chemodanImages.chemodanHandles,
        chemodanImages.chemodanHandles2,
        chemodanImages.chemodanLabel,
        chemodanImages.chemodanEmerald,
        chemodanImages.chemodanEmerald2,
        chemodanImages.chemodanEmerald3,
        chemodanImages.chemodanWithDog,
        chemodanImages.chemodanWithDogAndGirl,
        chemodanImages.chemodanBiggleAndGirl,
        chemodanImages.chemodanBiggleAndGirl2,
    ],
    "av-13": [],
    "av-14": [],
    "av-15": [],
    "av-17": [],
    "av-18": [
        chemodanImages.blackChemodan,
        chemodanImages.chemodanWithLabrador,
        chemodanImages.chemodanBeagleLabradorAndDog],
};

const images: Record<string, ImageData[]> = Object.fromEntries(
  Object.entries(fabricToImage).map(([id, photos]: [string, ImageData[]]) =>
      [id, photos.concat([{ src: `/fabrics/av/${id}.png`, alt: "Фото ткани" }])]
  )
);

const prices: Record<ChemodanSizeKeys, Price> = {
  "chemodan-m": { price: 1350 },
  "chemodan-l": { price: 1450 },
  "chemodan-xl": { price: 1550 },
  "chemodan-xxl": { price: 1750 },
};

const description = `
# О лежанке
Наша лежаночка состоит из водоотталкивающей ткани
* подушечки и бортики со съемным чехлом
* наполнителя: холофайбер.

# О размерах
* XS (50 х 40 см) для мини йорков, той-терьеров, чихуахуа и др. малышек
* S (60 х 40 см) для шпицев, папильонов, мальтийских болонок
* M (70 х 50 см) для ши-тцу, французских бульдогов, цверг-шнауцеров, вест хайленд уайт терьеров, пекинесов, мейн-кунов, британских вислоухих.
* L (90 х 60 см) для корги, биглей, американских бультерьеров, английских бульдогов, русских кокер спаниелей.
`;

const chemodans: Product[] = variants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Чемодан`,
  description: description,
  price: prices[v.sizeId],
  images: images[v.fabricId],
  details: {
    $case: "dogBed",
    dogBed: {
      sizeId: v.sizeId,
      fabricId: v.fabricId,
      fabrics: avFabrics,
      sizes: chemodanSizes,
      variants: variants,
    },
  },
}));

export default chemodans;
