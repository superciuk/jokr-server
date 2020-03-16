create table JOKERAPP_PRODUCT_ITEM_MODIFIER_CATEGORY_ASSOC (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PRODUCT_ITEM_ID varchar(36) not null,
    PRODUCT_MODIFIER_CATEGORY_ID varchar(36) not null,
    MIN_NUMBER integer not null,
    MAX_NUMBER integer not null,
    --
    primary key (ID)
);
