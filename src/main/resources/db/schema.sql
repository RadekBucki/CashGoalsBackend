CREATE TABLE IF NOT EXISTS user_entity
(
    id        SERIAL       NOT NULL,
    enabled   BOOLEAN      NOT NULL,
    username  VARCHAR(100) NOT NULL UNIQUE,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_token
(
    id         SERIAL       NOT NULL,
    token      VARCHAR(100) NOT NULL,
    type       VARCHAR(100) NOT NULL,
    user_id    INTEGER      NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id)
);