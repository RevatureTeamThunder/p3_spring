create table if not exists customer
(
    customer_id integer generated always as identity
        constraint customer_pkey
            primary key,
    email       text                      not null,
    password    text                      not null,
    first_name  text                      not null,
    last_name   text                      not null,
    role        text default 'user'::text not null
);

create table if not exists cart
(
    cart_id     serial
        constraint cart_pkey
            primary key,
    customer_id integer
        constraint cart_customer_id_fkey
            references customer,
    purchased   boolean default false
);

create table if not exists category
(
    category_id   serial
        constraint category_pkey
            primary key,
    category_name text not null
);

create table if not exists product
(
    product_id   serial
        constraint product_pkey
            primary key,
    name         text                        not null,
    description  text                        not null,
    quantity     integer        default 0    not null,
    price        numeric(10, 2) default 0.00 not null,
    image        text                        not null,
    rating       numeric(10, 2) default 0,
    review_count integer        default 0    not null,
    category_id  integer
        constraint product_category_id_fkey
            references category
);

create table if not exists cart_items
(
    id          serial
        constraint cart_items_pkey
            primary key,
    cart_id     integer
        constraint cart_items_cart_id_fkey
            references cart
            on delete cascade,
    customer_id integer
        constraint cart_items_customer_id_fkey
            references customer,
    product_id  integer
        constraint cart_items_product_id_fkey
            references product,
    quantity    integer default 1 not null
);

create table if not exists purchased_items
(
    id          serial
        constraint purchased_items_pkey
            primary key,
    customer_id integer
        constraint purchased_items_customer_id_fkey
            references customer,
    product_id  integer
        constraint purchased_items_product_id_fkey
            references product,
    cart_id     integer
        constraint purchased_items_cart_id_fkey
            references cart,
    quantity    integer default 1,
    card_id     integer
);

create table if not exists product_review
(
    review_id        serial
        constraint product_review_pkey
            primary key,
    customer_id      integer
        constraint product_review_customer_id_fkey
            references customer,
    product_id       integer
        constraint product_review_product_id_fkey
            references product,
    rating           integer not null,
    product_comments text    not null
);

create or replace view review_cart (id, cart_id, customer_id, product_id, quantity, name, price, total_cost) as
SELECT cart_items.id,
       cart_items.cart_id,
       cart_items.customer_id,
       cart_items.product_id,
       cart_items.quantity,
       product.name,
       product.price,
       product.price * cart_items.quantity::numeric AS total_cost
FROM cart_items
         JOIN product ON cart_items.product_id = product.product_id;

create or replace view order_history (id, customer_id, product_id, cart_id, quantity, name, price, total_cost) as
SELECT purchased_items.id,
       purchased_items.customer_id,
       purchased_items.product_id,
       purchased_items.cart_id,
       purchased_items.quantity,
       product.name,
       product.price,
       purchased_items.quantity::numeric * product.price AS total_cost
FROM purchased_items
         JOIN product ON purchased_items.product_id = product.product_id;

create or replace view product_review_view
            (review_id, customer_id, product_id, rating, product_comments, first_name, last_name, name) as
SELECT product_review.review_id,
       product_review.customer_id,
       product_review.product_id,
       product_review.rating,
       product_review.product_comments,
       customer.first_name,
       customer.last_name,
       product.name
FROM product_review
         JOIN customer ON product_review.customer_id = customer.customer_id
         JOIN product ON product_review.product_id = product.product_id;

create or replace procedure purchase_items(shopping_cart_id integer)
    language plpgsql
as
$$
declare
    result_set RECORD;
begin
    for result_set in select id, customer_id, product_id, cart_id, quantity
                      from cart_items
                      where cart_id = shopping_cart_id
        loop
            update product set quantity = quantity - result_set.quantity where product_id = result_set.product_id;
            insert into purchased_items (customer_id, product_id, quantity, cart_id)
            values (result_set.customer_id, result_set.product_id, result_set.quantity, result_set.cart_id);
            delete from cart_items where id = result_set.id;
        end loop;
    update cart set purchased = true where cart_id = shopping_cart_id;
end;
$$;

create or replace procedure update_rating(id integer, cid integer, rating_value integer, comments_text text,
                                          operation text)
    language plpgsql
as
$$
begin
    if (operation = 'add') then
        insert into product_review (product_id, customer_id, rating, product_comments)
        values (id, cid, rating_value, comments_text);
    elsif (operation = 'delete') then
        delete from product_review where customer_id = cid and product_id = id;
    elsif (operation = 'update') then
        update product_review
        set rating = rating_value, product_comments = comments_text
        where customer_id = cid and product_id = id;
    else
    end if;

    update product
    set rating       = (select avg(pr.rating) from product_review pr where pr.product_id = id),
        review_count = (select count(pr.review_id) from product_review pr where pr.product_id = id)
    where product_id = id;
end;
$$;


