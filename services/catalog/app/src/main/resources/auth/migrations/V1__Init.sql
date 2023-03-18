CREATE TABLE users
(
    uid                         UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    id                          VARCHAR UNIQUE NOT NULL,
    display_name                VARCHAR NOT NULL,
    email                       VARCHAR NOT NULL,
    email_verification_time_utc TIMESTAMP,
    image_src                   VARCHAR
);

CREATE TABLE verification_tokens
(
    uid                         UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    id                          VARCHAR UNIQUE NOT NULL,
    expire_time                 TIMESTAMP NOT NULL
)
