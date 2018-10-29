alter table JOKERAPP_ORDER alter column ITEM_PRICE rename to ITEM_PRICE__U77936 ^
alter table JOKERAPP_ORDER alter column ORDER_ID_ID rename to ORDER_ID_ID__U45232 ^
alter table JOKERAPP_ORDER alter column ORDER_ID_ID__U45232 set null ;
drop index IDX_JOKERAPP_ORDER_ORDER_ID ;
alter table JOKERAPP_ORDER drop constraint FK_JOKERAPP_ORDER_ORDER_ID ;
alter table JOKERAPP_ORDER alter column TAX_AMOUNT rename to TAX_AMOUNT__U55801 ^
alter table JOKERAPP_ORDER alter column TABLE_NUMBER_ID rename to TABLE_NUMBER_ID__U79554 ^
alter table JOKERAPP_ORDER alter column TABLE_NUMBER_ID__U79554 set null ;
drop index IDX_JOKERAPP_ORDER_TABLE_NUMBER ;
alter table JOKERAPP_ORDER drop constraint FK_JOKERAPP_ORDER_TABLE_NUMBER ;
-- alter table JOKERAPP_ORDER add column ORDER_ID varchar(36) ^
-- update JOKERAPP_ORDER set ORDER_ID = <default_value> ;
-- alter table JOKERAPP_ORDER alter column ORDER_ID set not null ;
alter table JOKERAPP_ORDER add column ORDER_ID varchar(36) not null ;
alter table JOKERAPP_ORDER add column TAX_AMOUNT bigint ;
alter table JOKERAPP_ORDER add column ITEM_PRICE bigint ;
