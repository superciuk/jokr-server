create table JOKERAPP_ORDER_LINE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_ID uuid not null,
    PRICE decimal(12, 2) not null,
    TAXES decimal(12, 2) not null,
    PRODUCT_ITEM_ID uuid not null,
    --
    primary key (ID)
);
