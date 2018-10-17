-- alter table JOKERAPP_TICKET add column ORDER_ID varchar(36) ^
-- update JOKERAPP_TICKET set ORDER_ID = <default_value> ;
-- alter table JOKERAPP_TICKET alter column ORDER_ID set not null ;
alter table JOKERAPP_TICKET add column ORDER_ID varchar(36) not null ;
-- alter table JOKERAPP_TICKET add column PRODUCT_ITEM_ID varchar(36) ^
-- update JOKERAPP_TICKET set PRODUCT_ITEM_ID = <default_value> ;
-- alter table JOKERAPP_TICKET alter column PRODUCT_ITEM_ID set not null ;
alter table JOKERAPP_TICKET add column PRODUCT_ITEM_ID varchar(36) not null ;
