CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

CREATE TABLE image_lists
(
    id           UUID PRIMARY KEY   DEFAULT public.uuid_generate_v4(),
    display_name VARCHAR   NOT NULL,
    create_time  TIMESTAMP not null DEFAULT NOW()
);

CREATE TABLE images
(
    id            UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    src           VARCHAR NOT NULL,
    alt           VARCHAR NOT NULL,
    image_list_id UUID REFERENCES image_lists ON DELETE CASCADE,
    list_order    INT     NOT NULL,
    UNIQUE (image_list_id, list_order)
);

CREATE TABLE categories
(
    id           UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    readable_id  VARCHAR UNIQUE NOT NULL,
    display_name VARCHAR        NOT NULL,
    description  VARCHAR        NOT NULL
);

CREATE TABLE parameter_lists
(
    id           UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    display_name VARCHAR NOT NULL
);

CREATE TABLE parameters
(
    id                UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    display_name      VARCHAR NOT NULL,
    description       VARCHAR,
    image_id          UUID REFERENCES images ON DELETE RESTRICT,
    list_order        INT     NOT NULL,
    parameter_list_id UUID    NOT NULL REFERENCES parameter_lists ON DELETE CASCADE,
    UNIQUE (parameter_list_id, list_order)
);

CREATE TABLE models
(
    id            UUID PRIMARY KEY        DEFAULT public.uuid_generate_v4(),
    readable_id   VARCHAR UNIQUE NOT NULL,
    display_name  VARCHAR        NOT NULL,
    description   VARCHAR        NOT NULL,
    category_id   UUID           NOT NULL REFERENCES categories ON DELETE RESTRICT,
    image_list_id UUID           NOT NULL REFERENCES image_lists ON DELETE RESTRICT,
    create_time   TIMESTAMP      not null DEFAULT NOW(),
    update_time   TIMESTAMP      not null DEFAULT NOW()
);

CREATE TABLE model_parameter_lists
(
    id                UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    model_id          UUID NOT NULL REFERENCES models ON DELETE CASCADE,
    parameter_list_id UUID NOT NULL REFERENCES parameter_lists,
    UNIQUE (model_id, parameter_list_id)
);


CREATE TABLE products
(
    id              UUID PRIMARY KEY   DEFAULT public.uuid_generate_v4(),
    price_usd       FLOAT     NOT NULL,
    promo_price_usd FLOAT,
    image_list_id   UUID      NOT NULL references image_lists ON DELETE RESTRICT,
    model_id        UUID      NOT NULL references models ON DELETE CASCADE,
    create_time     TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP NOT NULL DEFAULT NOW(),
    parameter_ids   UUID[]    NOT NULL,
    UNIQUE (model_id, parameter_ids)
);

CREATE OR REPLACE FUNCTION forbid_parameter_ids_updates() RETURNS TRIGGER AS
$$
BEGIN
    IF OLD.parameter_ids != NEW.parameter_ids THEN
        RAISE 'it is forbidden to update parameter_ids of product';
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql' IMMUTABLE
                      STRICT;

CREATE OR REPLACE FUNCTION check_parameter_ids_constraint() RETURNS TRIGGER AS
$$
DECLARE
    product_parameter_lists UUID[];
    model_parameter_lists   UUID[];
BEGIN
    select COALESCE(array_agg(p.parameter_list_id ORDER BY p.parameter_list_id), ARRAY []::UUID[])
    into product_parameter_lists
    FROM unnest(NEW.parameter_ids) parameter_id
             LEFT JOIN parameters p on parameter_id = p.id;

    select array_agg(mpl.parameter_list_id ORDER BY mpl.parameter_list_id)
    into model_parameter_lists
    from models m inner join model_parameter_lists mpl on m.id = mpl.model_id
    where m.id = NEW.model_id;

    IF model_parameter_lists != product_parameter_lists THEN
        RAISE 'bad parameter lists, mpl % ppl % pids %', model_parameter_lists, product_parameter_lists, NEW.parameter_ids;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql' IMMUTABLE
                      STRICT;

CREATE OR REPLACE FUNCTION update_time() RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time = now();
    RETURN NEW;
END;
$$ language 'plpgsql' IMMUTABLE
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

CREATE TRIGGER check_parameter_ids_and_constraint
    BEFORE INSERT
    ON products
    FOR EACH ROW
EXECUTE PROCEDURE check_parameter_ids_constraint();

CREATE TRIGGER forbid_parameter_ids_updates
    BEFORE UPDATE
    ON products
    FOR EACH ROW
EXECUTE PROCEDURE forbid_parameter_ids_updates();
