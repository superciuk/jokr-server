alter table JOKERAPP_PRODUCT_ITEM alter column CATEGORY_ID rename to CATEGORY_ID__UNUSED ;
alter table JOKERAPP_PRODUCT_ITEM alter column CATEGORY_ID__UNUSED set null ;
drop index IDX_JOKERAPP_PRODUCT_ITEM_CATEGORY ;
-- alter table JOKERAPP_PRODUCT_ITEM add column CATEGORY_ID varchar(36) ^
-- update JOKERAPP_PRODUCT_ITEM set CATEGORY_ID = <default_value> ;
-- alter table JOKERAPP_PRODUCT_ITEM alter column CATEGORY_ID set not null ;
alter table JOKERAPP_PRODUCT_ITEM add column CATEGORY_ID varchar(36) not null ;
