CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

CREATE TABLE images
(
    id  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    src VARCHAR NOT NULL,
    alt VARCHAR NOT NULL
);

CREATE TABLE categories
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    readable_id  VARCHAR UNIQUE NOT NULL,
    display_name VARCHAR        NOT NULL,
    description  VARCHAR        NOT NULL
);

CREATE TABLE models
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    readable_id  VARCHAR UNIQUE NOT NULL,
    display_name VARCHAR        NOT NULL,
    description  VARCHAR        NOT NULL,
    category_id  UUID            NOT NULL REFERENCES categories (id)
);

CREATE TABLE image_lists
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR
);

CREATE TABLE image_list_member
(
    image_list_id UUID NOT NULL REFERENCES image_lists (id),
    image_id     UUID NOT NULL REFERENCES images (id),
    UNIQUE (image_list_id, image_id)
);

CREATE TABLE fabrics
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR NOT NULL,
    description  VARCHAR NOT NULL,
    image_id     UUID REFERENCES images (id)
);

CREATE TABLE sizes
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR NOT NULL,
    description  VARCHAR NOT NULL
);

CREATE TABLE products
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name    VARCHAR,
    price_usd       FLOAT,
    promo_price_usd FLOAT,
    model_id        UUID references models (id),
    fabric_id       UUID references fabrics (id),
    size_id         UUID references sizes (id)
);

CREATE TABLE model_images
(
    model_id     UUID NOT NULL references models (id),
    fabric_id    UUID NOT NULL references fabrics (id),
    image_list_id UUID NOT NULL references image_lists (id),
    UNIQUE (model_id, fabric_id, image_list_id)
);
