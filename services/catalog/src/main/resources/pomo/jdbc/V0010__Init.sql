CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

CREATE TABLE images
(
    id  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    src VARCHAR UNIQUE NOT NULL,
    alt VARCHAR        NOT NULL
);

CREATE TABLE image_lists
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR NOT NULL
);

CREATE TABLE image_list_member
(
    image_list_id UUID NOT NULL REFERENCES image_lists (id) ON DELETE CASCADE,
    image_id      UUID NOT NULL REFERENCES images (id),
    UNIQUE (image_list_id, image_id)
);

CREATE TABLE categories
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    readable_id  VARCHAR UNIQUE NOT NULL,
    display_name VARCHAR        NOT NULL,
    description  VARCHAR        NOT NULL
);

CREATE TABLE fabric_list
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR NOT NULL
);

CREATE TABLE fabrics
(
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name   VARCHAR NOT NULL,
    image_id       UUID    NOT NULL REFERENCES images (id),
    list_order     INT     NOT NULL,
    fabric_list_id UUID    NOT NULL REFERENCES fabric_list (id),
    UNIQUE (fabric_list_id, list_order)
);

CREATE TABLE size_list
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR NOT NULL
);

CREATE TABLE sizes
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    display_name VARCHAR NOT NULL,
    list_order   INT     NOT NULL,
    size_list_id UUID    NOT NULL,
    UNIQUE (size_list_id, list_order)
);

CREATE TABLE models
(
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    readable_id    VARCHAR UNIQUE NOT NULL,
    display_name   VARCHAR        NOT NULL,
    description    VARCHAR        NOT NULL,
    create_time    TIMESTAMP without time zone not null DEFAULT NOW(),
    update_time    TIMESTAMP without time zone not null DEFAULT NOW(),
    category_id    UUID           NOT NULL REFERENCES categories (id),
    image_list_id  UUID           NOT NULL REFERENCES image_lists (id),
    size_list_id   UUID           NOT NULL REFERENCES size_list (id),
    fabric_list_id UUID           NOT NULL REFERENCES fabric_list (id)
);


CREATE TABLE products
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    price_usd       FLOAT NOT NULL,
    promo_price_usd FLOAT,
    create_time     TIMESTAMP WITHOUT TIME ZONE not null DEFAULT NOW(),
    update_time     TIMESTAMP WITHOUT TIME ZONE not null DEFAULT NOW(),
    image_list_id   UUID  NOT NULL references image_lists (id),
    model_id        UUID  NOT NULL references models (id),
    fabric_id       UUID  NOT NULL references fabrics (id),
    size_id         UUID  NOT NULL references sizes (id)
);

CREATE
OR REPLACE FUNCTION update_time() RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time
= now();
RETURN NEW;
END;
$$
language 'plpgsql' IMMUTABLE
                      STRICT;

CREATE
OR REPLACE FUNCTION product_size_list_constraint() RETURNS TRIGGER AS
$$
DECLARE
model_size_list_id   UUID;
    product_size_list_id
UUID;
BEGIN
SELECT size_list_id
INTO model_size_list_id
FROM models
WHERE id = NEW.model_id;
SELECT size_list_id
INTO product_size_list_id
FROM sizes
WHERE id = NEW.size_id;
IF
model_size_list_id != product_size_list_id THEN
        RAISE EXCEPTION 'product has size_list_id % not equal to % of model', product_size_list_id, model_size_list_id;
END IF;
RETURN NEW;
END;
$$
language 'plpgsql' IMMUTABLE
                      STRICT;

CREATE
OR REPLACE FUNCTION product_fabric_list_constraint() RETURNS TRIGGER AS
$$
DECLARE
model_fabric_list_id   UUID;
    product_fabric_list_id
UUID;
BEGIN
SELECT fabric_list_id
INTO model_fabric_list_id
FROM models
WHERE id = NEW.model_id;
SELECT fabric_list_id
INTO product_fabric_list_id
FROM fabrics
WHERE id = NEW.fabric_id;
IF
model_fabric_list_id != product_fabric_list_id THEN
        RAISE EXCEPTION 'product has size_list_id % not equal to % of model', product_fabric_list_id, model_fabric_list_id;
END IF;
RETURN NEW;
END;
$$
language 'plpgsql' IMMUTABLE
                      STRICT;


CREATE TRIGGER update_product_update_time
    BEFORE UPDATE
    ON products
    FOR EACH ROW
    EXECUTE PROCEDURE update_time();

CREATE TRIGGER update_model_update_time
    BEFORE UPDATE
    ON products
    FOR EACH ROW
    EXECUTE PROCEDURE update_time();

CREATE TRIGGER product_size_list_constraint_t
    BEFORE UPDATE
    ON products
    FOR EACH ROW
    EXECUTE PROCEDURE product_size_list_constraint();