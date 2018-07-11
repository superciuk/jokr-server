-- begin JOKERAPP_PRODUCT_ITEM
create table JOKERAPP_PRODUCT_ITEM (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    CATEGORY_ID varchar(36) not null,
    VISIBLE boolean,
    --
    primary key (ID)
)^
-- end JOKERAPP_PRODUCT_ITEM
-- begin JOKERAPP_PRODUCT_ITEM_CATEGORY
create table JOKERAPP_PRODUCT_ITEM_CATEGORY (
    ID varchar(36) not null,
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
-- begin JOKERAPP_TICKET_ITEM
create table JOKERAPP_TICKET_ITEM (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PAID boolean not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_TICKET_ITEM
-- begin JOKERAPP_TABLE_ITEM_AREA
create table JOKERAPP_TABLE_ITEM_AREA (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_TABLE_ITEM_AREA
-- begin JOKERAPP_TABLE_ITEM
create table JOKERAPP_TABLE_ITEM (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NUMBER_ integer not null,
    AREA_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_TABLE_ITEM
-- begin JOKERAPP_TICKET_ITEM_LINE
create table JOKERAPP_TICKET_ITEM_LINE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    QUANTITY integer not null,
    PRODUCT_ITEM_ID varchar(36) not null,
    TICKET_ITEM_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_TICKET_ITEM_LINE
-- begin JOKERAPP_PRODUCT_MODIFIER_CATEGORY
create table JOKERAPP_PRODUCT_MODIFIER_CATEGORY (
    ID varchar(36) not null,
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
    ID varchar(36) not null,
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
    CATEGORY_ID varchar(36) not null,
    PRICE integer,
    --
    primary key (ID)
)^
-- end JOKERAPP_PRODUCT_MODIFIER
-- begin JOKERAPP_TAX
create table JOKERAPP_TAX (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    RATE double precision not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_TAX
-- begin JOKERAPP_PRINTER
create table JOKERAPP_PRINTER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_PRINTER
