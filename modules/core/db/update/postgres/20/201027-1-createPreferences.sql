create table JOKERAPP_PREFERENCES (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID uuid,
    PREF_KEY varchar(255),
    PREF_VALUE varchar(255),
    --
    primary key (ID)
);