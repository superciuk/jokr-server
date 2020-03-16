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
    --
    primary key (ID)
);
