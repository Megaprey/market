create table items
(
    id                        bigserial    not null,
    title                     varchar(255) not null,
    description               varchar(255) not null,
    price                     numeric not null,
    count_item                integer null,
    img_path                  varchar(255) null,
    order_id                  integer    null,
    check_order               boolean    not null default false,

    constraint items_id primary key (id)
);

create table orders
(
    id                        bigserial    not null,
    total_sum                  numeric not null,

    constraint orders_id primary key (id)
);