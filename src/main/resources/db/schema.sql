CREATE TABLE IF NOT EXISTS user_entity
(
    id         SERIAL       NOT NULL,
    enabled    BOOLEAN      NOT NULL,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    theme      VARCHAR(10)  NOT NULL,
    locale     VARCHAR(10)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_token
(
    id         SERIAL      NOT NULL,
    token      VARCHAR(10) NOT NULL,
    type       VARCHAR(20) NOT NULL,
    user_id    INTEGER     NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS budget
(
    id                  UUID         NOT NULL,
    name                VARCHAR(100) NOT NULL,
    initialization_step VARCHAR(20)  NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS income
(
    id           SERIAL       NOT NULL,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(255),
    amount       DECIMAL      NOT NULL,
    period       VARCHAR(5)   NOT NULL,
    period_value INTEGER      NOT NULL,
    budget_id    UUID         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (budget_id) REFERENCES budget (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS category
(
    id          SERIAL       NOT NULL,
    parent_id   INTEGER,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    visible     BOOLEAN      NOT NULL,
    budget_id   UUID         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (budget_id) REFERENCES budget (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES category (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS goal
(
    id          SERIAL       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    type        VARCHAR(14)  NOT NULL,
    value       DECIMAL      NOT NULL,
    category_id INTEGER      NOT NULL,
    budget_id   UUID         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (budget_id) REFERENCES budget (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_right
(
    id        SERIAL      NOT NULL,
    user_id   INTEGER     NOT NULL,
    budget_id UUID         NOT NULL,
    right_type   VARCHAR(21) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (budget_id) REFERENCES budget (id) ON DELETE CASCADE
);