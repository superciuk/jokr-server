alter table JOKERAPP_ORDER_LINE add column ITEM_NAME varchar(255) ^
update JOKERAPP_ORDER_LINE set ITEM_NAME = '' where ITEM_NAME is null ;
alter table JOKERAPP_ORDER_LINE alter column ITEM_NAME set not null ;
