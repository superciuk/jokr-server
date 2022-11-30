create table JOKERAPP_USER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USERNAME varchar(255) not null,
    ENCRYPTED_USER_PASSWORD varchar(255) not null,
    USER_TYPE varchar(50) not null,
    USER_STATUS varchar(50) not null,
    --
    primary key (ID)
);