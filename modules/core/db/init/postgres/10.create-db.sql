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
    ADD_PRICE decimal(19, 2),
    SUBTRACT_PRICE decimal(19, 2),
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
    TABLE_STATUS varchar(50) not null,
    CURRENT_ORDER_ID uuid,
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
    ACTUAL_SEATS integer not null,
    DISCOUNT decimal(12, 2),
    STATUS varchar(50) not null,
    TABLE_ITEM_NUMBER integer,
    --
    primary key (ID)
)^
-- end JOKERAPP_ORDER
-- begin JOKERAPP_ORDER_LINE
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
    QUANTITY integer,
    ITEM_NAME varchar(255) not null,
    UNIT_PRICE decimal(19, 2),
    PRICE decimal(12, 2) not null,
    TAXES decimal(12, 2) not null,
    ORDER_ID uuid,
    POSITION_ integer,
    NEXT_MODIFIER_POSITION integer,
    HAS_MODIFIER boolean not null,
    IS_MODIFIER boolean not null,
    ITEM_TO_MODIFY_ID uuid,
    IS_SENDED boolean not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_ORDER_LINE
-- begin JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK
create table JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK (
    PRODUCT_MODIFIER_CATEGORY_ID uuid,
    PRODUCT_ITEM_ID uuid,
    primary key (PRODUCT_MODIFIER_CATEGORY_ID, PRODUCT_ITEM_ID)
)^
-- end JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK
