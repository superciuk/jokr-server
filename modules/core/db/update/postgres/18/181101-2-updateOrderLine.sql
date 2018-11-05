alter table JOKERAPP_ORDER_LINE rename column product_item_id to product_item_id__u75162 ;
alter table JOKERAPP_ORDER_LINE alter column product_item_id__u75162 drop not null ;
drop index IDX_JOKERAPP_ORDER_LINE_ON_PRODUCT_ITEM ;
alter table JOKERAPP_ORDER_LINE drop constraint FK_JOKERAPP_ORDER_LINE_ON_PRODUCT_ITEM ;
alter table JOKERAPP_ORDER_LINE add column NAME varchar(255) ^
update JOKERAPP_ORDER_LINE set NAME = '' where NAME is null ;
alter table JOKERAPP_ORDER_LINE alter column NAME set not null ;
