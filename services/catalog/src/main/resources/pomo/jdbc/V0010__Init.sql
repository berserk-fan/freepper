CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

CREATE TABLE image_lists
(
    id           UUID PRIMARY KEY                     DEFAULT uuid_generate_v4(),
    display_name VARCHAR                     NOT NULL,
    create_time  TIMESTAMP WITHOUT TIME ZONE not null DEFAULT NOW()
);

CREATE TABLE images
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    src           VARCHAR NOT NULL,
    alt           VARCHAR NOT NULL,
    image_list_id UUID    NOT NULL REFERENCES image_lists(id) ON DELETE CASCADE
);

CREATE TABLE categories
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    readable_id  VARCHAR UNIQUE NOT NULL,
    display_name VARCHAR        NOT NULL,
    description  VARCHAR        NOT NULL
);

CREATE TABLE parameter_lists
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR NOT NULL
);

CREATE TABLE parameters
(
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name      VARCHAR NOT NULL,
    image_id          UUID REFERENCES images (id),
    list_order        INT     NOT NULL,
    parameter_list_id UUID    NOT NULL REFERENCES parameter_lists (id),
    UNIQUE (parameter_list_id, list_order)
);

CREATE TABLE models
(
    id                 UUID PRIMARY KEY                     DEFAULT uuid_generate_v4(),
    readable_id        VARCHAR UNIQUE              NOT NULL,
    display_name       VARCHAR                     NOT NULL,
    description        VARCHAR                     NOT NULL,
    category_id        UUID                        NOT NULL REFERENCES categories (id),
    image_list_id      UUID                        NOT NULL REFERENCES image_lists (id),
    parameter_list_ids UUID[]                      NOT NULL,
    create_time        TIMESTAMP without time zone not null DEFAULT NOW(),
    update_time        TIMESTAMP without time zone not null DEFAULT NOW()
);

CREATE TABLE products
(
    id              UUID PRIMARY KEY                     DEFAULT uuid_generate_v4(),
    price_usd       FLOAT                       NOT NULL,
    promo_price_usd FLOAT,
    image_list_id   UUID                        NOT NULL references image_lists (id),
    model_id        UUID                        NOT NULL references models (id),
    parameters      UUID[]                      NOT NULL,
    create_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
