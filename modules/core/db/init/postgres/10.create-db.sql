-- begin JOKERAPP_PRODUCT_ITEM_CATEGORY
create table JOKERAPP_PRODUCT_ITEM_CATEGORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(120) not null,
    VISIBLE boolean not null,
    SORT_ORDER integer not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_PRODUCT_ITEM_CATEGORY
-- begin JOKERAPP_PRODUCT_ITEM
create table JOKERAPP_PRODUCT_ITEM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    SORT_ORDER integer,
    CATEGORY_ID uuid not null,
    VISIBLE boolean,
    PRICE decimal(12, 2) not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_PRODUCT_ITEM
-- begin JOKERAPP_PRODUCT_MODIFIER_CATEGORY
create table JOKERAPP_PRODUCT_MODIFIER_CATEGORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    SORT_ORDER integer,
    --
    primary key (ID)
)^
-- end JOKERAPP_PRODUCT_MODIFIER_CATEGORY
-- begin JOKERAPP_PRODUCT_MODIFIER
create table JOKERAPP_PRODUCT_MODIFIER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    ADD_PRICE decimal(12, 2),
    SUBTRACT_PRICE decimal(12, 2),
    CATEGORY_ID uuid not null,
    SORT_ORDER integer,
    --
    primary key (ID)
)^
-- end JOKERAPP_PRODUCT_MODIFIER

-- begin JOKERAPP_TABLE_ITEM
create table JOKERAPP_TABLE_ITEM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TABLE_NUMBER integer not null,
    SEATS_CAPACITY integer,
    ACTUAL_SEATS integer,
    ORDER_ID uuid,
    CHARGE decimal(12, 2),
    TAX decimal(12, 2),
    DISCOUNT decimal(12, 2),
    STATUS integer,
    --
    primary key (ID)
)^
-- end JOKERAPP_TABLE_ITEM
-- begin JOKERAPP_ORDER
create table JOKERAPP_ORDER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_ID uuid,
    ITEM_NAME varchar(255) not null,
    ITEM_PRICE decimal(12, 2),
    TAX_AMOUNT decimal(12, 2),
    --
    primary key (ID)
)^
-- end JOKERAPP_ORDER
