CREATE TABLE user_entity
(
    id        SERIAL       NOT NULL,
    enabled   BOOLEAN      NOT NULL,
    username  VARCHAR(100) NOT NULL UNIQUE,
    email     VARCHAR(100) NOT NULL UNIQUE,
    firstname VARCHAR(100) NOT NULL,
    lastname  VARCHAR(100) NOT NULL,
    password  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);