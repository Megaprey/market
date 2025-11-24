create table items
(
    id                        bigserial    not null,
    title                     varchar(255) not null,
    description               varchar(255) not null,
    price                     numeric not null,
    count_item                integer not null,
    img_path                  varchar(255) not null,
    cart_flg                  boolean not null,

    constraint items_id primary key (id)
);

create table orders
(
    id                        bigserial    not null,
    totalSum                  numeric not null,
    count_item                integer not null,
    items_id                   bigserial    not null,

    constraint orders_id primary key (id),
    constraint orders_items_id foreign key (items_id)
            references items (id)
);