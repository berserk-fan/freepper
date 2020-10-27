import {DogBed_Variant, Fabric, Product, Size} from "@mamat14/shop-server/shop_model";

const sizes: Size[] = [
    {
        id: '1',
        displayName: 'XS',
        description: '80x40'
    },
    {
        id: '2',
        displayName: 'S',
        description: '90x60'
    },
    {
        id: '3',
        displayName: 'M',
        description: '120x90'
    },
    {
        id: '4',
        displayName: 'L',
        description: '150x100'
    }
];


const avroFabrics: Fabric[] = [
    {
        id: 'avro-500',
        displayName: 'Avro 500',
        description: 'Ткань avro 500',
        image: {
            src: "https://picsum.photos/30/30?random=1",
            alt: "Ткань avro 500"
        }
    },
    {
        id: 'avro-600',
        displayName: 'Avro 600',
        description: 'Ткань avro 600',
        image: {
            src: "https://picsum.photos/30/30?random=1",
            alt: "Ткань avro 500"

        }
    },
    {
        id: 'avro-700',
        displayName: 'Avro 700',
        description: 'Ткань avro 700',
        image: {
            src: "https://picsum.photos/30/30?random=1",
            alt: "Ткань avro 500"

        }
    },
    {
        id: 'avro-800',
        displayName: 'Avro 800',
        description: 'Ткань avro 800',
        image: {
            src: "https://picsum.photos/30/30?random=1",
            alt: "Ткань avro 500"
        }
    },
    {
        id: 'avro-900',
        displayName: 'Avro 900',
        description: 'Ткань avro 900',
        image: {
            src: "https://picsum.photos/30/30?random=1",
            alt: "Ткань avro 500"
        }
    }
];

function getVariants(): DogBed_Variant[] {
    const res: DogBed_Variant[] = [];
    for(const fabric of avroFabrics) {
        for(const size of sizes) {
            res.push({
                fabricId: fabric.id,
                sizeId: size.id,
                variantName: 'categories/beds-category/products/lukoshko-grey-xs',
            })
        }
    }
    return res;
}
const lukoshkoVariants: DogBed_Variant[] = getVariants();


export const shopProducts: Product[] = [
    {
        id: 'lukoshko-grey',
        name: 'categories/beds-category/products/lukoshko-grey',
        displayName: 'Лукошко - Серый',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=1",
            alt: "лежанка"
        },
        price: {
            price: 999
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                sizeId: '1',
                fabricId: 'avro-500',
                fabrics: avroFabrics,
                sizes: sizes,
                variants: lukoshkoVariants,
            }
        },
    },{
        id: 'lukoshko-white',
        name: 'categories/beds-category/products/lukoshko-white',
        displayName: 'Лукошко серое - White',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=66",
            alt: "лежанка"
        },
        price: {
            price: 999
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                sizeId: '1',
                fabricId: 'avro-500',
                fabrics: avroFabrics,
                sizes: sizes,
                variants: lukoshkoVariants,
            }
        },
    },
    {
        id: 'lukoshko-red',
        name: 'categories/beds-category/products/lukoshko-red',
        displayName: 'Лукошко - Красный',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=2",
            alt: "лежанка"
        },
        price: {
            price: 1100
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                sizeId: '1',
                fabricId: 'avro-500',
                fabrics: avroFabrics,
                sizes: sizes,
                variants: lukoshkoVariants,
            }
        },
    },
    {
        id: 'lukoshko-blue',
        name: 'categories/beds-category/products/lukoshko-blue',
        displayName: 'Лукошко - Синий',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=4",
            alt: "лежанка"
        },
        price: {
            price: 1299
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                sizeId: '1',
                fabricId: 'avro-500',
                fabrics: avroFabrics,
                sizes: sizes,
                variants: lukoshkoVariants,
            }
        },
    }
];


export const category = {
    id: 'beds-category',
    name: `/categories/beds-category`,
    displayName: 'Лежанки',
    description: 'Лежанки для питомцев',
    image: {
        src: "https://picsum.photos/300/300?random=1",
        alt: "beds category"
    }
};
