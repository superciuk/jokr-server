alter table JOKERAPP_ORDER alter column CHARGE rename to CHARGE__UNUSED ;
alter table JOKERAPP_ORDER alter column SEATS rename to SEATS__UNUSED ;
-- alter table JOKERAPP_ORDER add column ORDER_ID_ID varchar(36) ^
-- update JOKERAPP_ORDER set ORDER_ID_ID = <default_value> ;
-- alter table JOKERAPP_ORDER alter column ORDER_ID_ID set not null ;
alter table JOKERAPP_ORDER add column ORDER_ID_ID varchar(36) not null ;
alter table JOKERAPP_ORDER add column ITEM_NAME varchar(255) ^
update JOKERAPP_ORDER set ITEM_NAME = '' where ITEM_NAME is null ;
alter table JOKERAPP_ORDER alter column ITEM_NAME set not null ;
alter table JOKERAPP_ORDER add column ITEM_PRICE double precision ;
alter table JOKERAPP_ORDER add column STATUS varchar(255) ^
update JOKERAPP_ORDER set STATUS = '' where STATUS is null ;
alter table JOKERAPP_ORDER alter column STATUS set not null ;
