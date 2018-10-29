alter table JOKERAPP_ORDER rename column tax_amount to tax_amount__u53133 ;
alter table JOKERAPP_ORDER rename column item_price to item_price__u32532 ;
alter table JOKERAPP_ORDER rename column table_number_id to table_number_id__u13695 ;
alter table JOKERAPP_ORDER alter column table_number_id__u13695 drop not null ;
drop index IDX_JOKERAPP_ORDER_ON_TABLE_NUMBER ;
alter table JOKERAPP_ORDER drop constraint FK_JOKERAPP_ORDER_ON_TABLE_NUMBER ;
alter table JOKERAPP_ORDER rename column order_id_id to order_id_id__u08519 ;
alter table JOKERAPP_ORDER alter column order_id_id__u08519 drop not null ;
drop index IDX_JOKERAPP_ORDER_ON_ORDER_ID ;
alter table JOKERAPP_ORDER drop constraint FK_JOKERAPP_ORDER_ON_ORDER_ID ;
-- alter table JOKERAPP_ORDER add column ORDER_ID uuid ^
-- update JOKERAPP_ORDER set ORDER_ID = <default_value> ;
-- alter table JOKERAPP_ORDER alter column ORDER_ID set not null ;
alter table JOKERAPP_ORDER add column ORDER_ID uuid not null ;
alter table JOKERAPP_ORDER add column ITEM_PRICE bigint ;
alter table JOKERAPP_ORDER add column TAX_AMOUNT bigint ;
