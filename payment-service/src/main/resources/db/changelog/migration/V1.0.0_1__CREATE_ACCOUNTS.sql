CREATE TABLE accounts
(
    id       BIGSERIAL    NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    balance  NUMERIC      NOT NULL DEFAULT 0,

    CONSTRAINT accounts_pk PRIMARY KEY (id)
);

INSERT INTO accounts (username, balance)
VALUES ('user1', 10000.00),
       ('user2', 5000.00);
