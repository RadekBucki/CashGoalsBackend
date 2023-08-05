CREATE TABLE IF NOT EXISTS user_entity
(
    id        SERIAL       NOT NULL,
    enabled   BOOLEAN      NOT NULL,
    username  VARCHAR(100) NOT NULL UNIQUE,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);