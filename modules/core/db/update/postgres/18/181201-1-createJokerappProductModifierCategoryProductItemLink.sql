create table JOKERAPP_PRODUCT_MODIFIER_CATEGORY_PRODUCT_ITEM_LINK (
    PRODUCT_MODIFIER_CATEGORY_ID uuid,
    PRODUCT_ITEM_ID uuid,
    primary key (PRODUCT_MODIFIER_CATEGORY_ID, PRODUCT_ITEM_ID)
);
