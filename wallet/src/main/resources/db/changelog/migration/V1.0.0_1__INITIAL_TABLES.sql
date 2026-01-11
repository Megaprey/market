create table wallet
(
    id                        bigserial    not null,
    balance                   numeric not null,
    user_id                   bigserial not null,

    constraint wallet_id primary key (id)
);
