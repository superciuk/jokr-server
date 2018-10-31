-- alter table JOKERAPP_ORDER add column TABLE_ITEM_ID uuid ^
-- update JOKERAPP_ORDER set TABLE_ITEM_ID = <default_value> ;
-- alter table JOKERAPP_ORDER alter column TABLE_ITEM_ID set not null ;
alter table JOKERAPP_ORDER add column TABLE_ITEM_ID uuid not null ;
alter table JOKERAPP_ORDER add column ACTUAL_SEATS integer ^
update JOKERAPP_ORDER set ACTUAL_SEATS = 0 where ACTUAL_SEATS is null ;
alter table JOKERAPP_ORDER alter column ACTUAL_SEATS set not null ;
alter table JOKERAPP_ORDER add column DISCOUNT decimal(12, 2) ;
alter table JOKERAPP_ORDER add column STATUS varchar(50) ^
update JOKERAPP_ORDER set STATUS = '0' where STATUS is null ;
alter table JOKERAPP_ORDER alter column STATUS set not null ;
