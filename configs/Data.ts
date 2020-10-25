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

export const sizes = [
    {
        displayName: 'XS',
        description: '80x40'
    },
    {
        displayName: 'S',
        description: '90x60'
    },
    {
        displayName: 'M',
        description: '120x90'
    },
    {
        displayName: 'L',
        description: '150x100'
    }
];

export const products = [
    {
        id: 'lukoshko-grey',
        name: 'categories/beds-category/products/lukoshko-grey-xs',
        displayName: 'Лукошко серое',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=1",
            alt: "лежанка"
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                fabric: {
                    id: 'some-good-fabric-1',
                    displayName: 'Avro 500',
                    description: 'Хорошая ткань',
                    image: {
                        src: "https://picsum.photos/300/300?random=1",
                        alt: "Какая-то ткань"
                    }
                },
                sizes: sizes.slice(0, 2)
            }
        },
    },
    {
        id: 'lukoshko-blue',
        name: 'categories/beds-category/products/lukoshko-blue',
        displayName: 'Лукошко серое XS',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=1",
            alt: "лежанка"
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                fabric: {
                    id: 'some-good-fabric-1',
                    displayName: 'Avro 500',
                    description: 'Хорошая ткань',
                    image: {
                        src: "https://picsum.photos/300/300?random=1",
                        alt: "Какая-то ткань"
                    }
                },
                sizes: sizes.slice(1, 3)
            }
        },
    },
    {
        id: 'lukoshko-grey-xs',
        name: 'categories/beds-category/products/lukoshko-grey-xs',
        displayName: 'Лукошко серое XS',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=1",
            alt: "лежанка"
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                fabric: {
                    id: 'some-good-fabric-1',
                    displayName: 'Avro 500',
                    description: 'Хорошая ткань',
                    image: {
                        src: "https://picsum.photos/300/300?random=1",
                        alt: "Какая-то ткань"
                    }
                },
                sizes: sizes.slice(1,2)
            }
        },
    },
    {
        id: 'lukoshko-grey-xs',
        name: 'categories/beds-category/products/lukoshko-grey-xs',
        displayName: 'Лукошко серое XS',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=1",
            alt: "лежанка"
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                fabric: {
                    id: 'some-good-fabric-1',
                    displayName: 'Avro 500',
                    description: 'Хорошая ткань',
                    image: {
                        src: "https://picsum.photos/300/300?random=1",
                        alt: "Какая-то ткань"
                    }
                },
                sizes: [sizes[3], sizes[1]]
            }
        },
    },
    {
        id: 'lukoshko-grey-xs',
        name: 'categories/beds-category/products/lukoshko-grey-xs',
        displayName: 'Лукошко серое XS',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=1",
            alt: "лежанка"
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                fabric: {
                    id: 'some-good-fabric-1',
                    displayName: 'Avro 500',
                    description: 'Хорошая ткань',
                    image: {
                        src: "https://picsum.photos/300/300?random=1",
                        alt: "Какая-то ткань"
                    }
                },
                sizes: sizes.slice(0, 2)
            }
        },
    },
    {
        id: 'lukoshko-grey-xs',
        name: 'categories/beds-category/products/lukoshko-grey-xs',
        displayName: 'Лукошко серое XS',
        description: 'Хорошая лежанка',
        image: {
            src: "https://picsum.photos/300/300?random=1",
            alt: "лежанка"
        },
        details: {
            $case: 'dogBed',
            dogBed: {
                fabric: {
                    id: 'some-good-fabric-1',
                    displayName: 'Avro 500',
                    description: 'Хорошая ткань',
                    image: {
                        src: "https://picsum.photos/300/300?random=1",
                        alt: "Какая-то ткань"
                    }
                },
                sizes: sizes.slice(0, 2)
            }
        },
    },
];
