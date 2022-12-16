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
    IS_BEVERAGE boolean not null,
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
    PRINTER_GROUP varchar(50),
    IMAGE_ID uuid,
    DESCRIPTION text,
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
    ADD_PRICE decimal(19, 2) not null,
    SUBTRACT_PRICE decimal(19, 2) not null,
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
    TABLE_CAPTION varchar(255),
    TABLE_AREA_ID uuid,
    SEATS_CAPACITY integer,
    TABLE_STATUS varchar(50) not null,
    TABLE_RESERVATION_NAME varchar(255),
    TABLE_RESERVATION_SEATS varchar(255),
    TABLE_RESERVATION_TIME varchar(255),
    TABLE_RESERVATION_PHONE_NUMBER varchar(255),
    CURRENT_ORDER_ID uuid,
    LAST_ORDER_ID uuid,
    WITH_SERVICE_BY_DEFAULT boolean not null,
    CHECKED boolean not null,
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
    CURRENT_STATUS varchar(50) not null,
    PREVIOUS_STATUS varchar(50),
    ORDER_IN_PROGRESS boolean,
    WAITER_CALL_USER_ID uuid,
    TABLE_ITEM_CAPTION varchar(255),
    WITH_SERVICE boolean not null,
    DISCOUNT decimal(12, 2),
    CHARGE decimal(19, 2),
    TAXES decimal(19, 2),
    NEXT_TICKET_NUMBER integer,
    USER_ID uuid not null,
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
    TICKET_ID uuid,
    QUANTITY integer,
    ITEM_NAME varchar(255) not null,
    ITEM_ID uuid,
    UNIT_PRICE decimal(19, 2),
    PRICE decimal(12, 2) not null,
    TAXES decimal(12, 2) not null,
    POSITION_ integer,
    NEXT_MODIFIER_POSITION integer,
    HAS_MODIFIER boolean not null,
    IS_MODIFIER boolean not null,
    ITEM_TO_MODIFY_ID uuid,
    IS_BEVERAGE boolean,
    CHECKED boolean,
    IS_REVERSED boolean,
    PRINTER_GROUP varchar(50),
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
-- begin JOKERAPP_TICKET
create table JOKERAPP_TICKET (
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
    TICKET_NUMBER integer,
    TICKET_STATUS varchar(50),
    SUBTICKET_STATUS varchar(255),
    USER_ID uuid not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_TICKET
-- begin JOKERAPP_PREFERENCES
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
    SCREEN_ORIENTATION varchar(255),
    TASK varchar(255),
    --
    primary key (ID)
)^
-- end JOKERAPP_PREFERENCES
-- begin JOKERAPP_TABLE_ITEM_AREA
create table JOKERAPP_TABLE_ITEM_AREA (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    AREA_NUMBER integer not null,
    AREA_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end JOKERAPP_TABLE_ITEM_AREA
-- begin JOKERAPP_USER
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
    NOTIFICATION_TOKEN varchar(255),
    --
    primary key (ID)
)^
-- end JOKERAPP_USER
