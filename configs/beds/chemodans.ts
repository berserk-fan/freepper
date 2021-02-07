import {DogBed_Variant, ImageData, Price, Product} from "@mamat14/shop-server/shop_model";
import avFabrics from "../fabrics/avFabrics";
import chemodanSizes from "../sizes/chemodanSizes";

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

const images: Record<string, ImageData[]> = Object.fromEntries(
    Object.entries({
        "av-01": [],
        "av-02": [],
        "av-04": [],
        "av-06": [],
        "av-07": ["Dogs-7161.jpg", "Dogs-7169.jpg", "Dogs-7173.jpg", "Dogs-7183.jpg", "Dogs-7268 (2).jpg", "Dogs-7271.jpg", "Dogs-24800.jpg", "IMG_4965.JPG"],
        "av-10": [],
        "av-11": [],
        "av-12": ["Dogs-7161.jpg", "Dogs-7169.jpg", "Dogs-7173.jpg", "Dogs-7183.jpg", "Dogs-7196 (1).jpg", "Dogs-7255.jpg", "Dogs-7257.jpg", "Dogs-7258 (1).jpg", "Dogs-24947 (2).jpg", "Dogs-24967 (2).jpg", "Dogs-24998 (1).jpg", "Dogs-25034.jpg", "IMG_4961.JPG", "IMG_4964.JPG"],
        "av-13": [],
        "av-14": [],
        "av-15": [],
        "av-17": [],
        "av-18": ["Dogs-7078.jpg", "Dogs-25044.jpg", "Dogs-25071.jpg"]
    }).map(([id, photos]: [string, ImageData[]]) => [
        id,
        photos.map(name => ({
            src: `/beds/chemodan/${name}`,
            alt: "фото лежанки Чемодан",
        })).concat([{ src: `/fabrics/av/${id}.png`, alt: "Фото ткани" }]),
    ])
);

const prices: Record<string, Price> = {
    "chemodan-1": {price: 1350},
    "chemodan-2": {price: 1450},
    "chemodan-3": {price: 1550},
    "chemodan-4": {price: 1750},
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

export default chemodans
