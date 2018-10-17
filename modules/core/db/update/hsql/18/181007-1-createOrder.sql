create table JOKERAPP_ORDER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TABLE_NUMBER_ID varchar(36) not null,
    SEATS integer,
    CHARGE double precision,
    TAX_AMOUNT double precision,
    --
    primary key (ID)
);
