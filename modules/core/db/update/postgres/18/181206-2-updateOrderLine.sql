alter table JOKERAPP_ORDER_LINE add column UNIT_PRICE decimal(19, 2) ^
update JOKERAPP_ORDER_LINE set UNIT_PRICE = 0 where UNIT_PRICE is null ;
alter table JOKERAPP_ORDER_LINE alter column UNIT_PRICE set not null ;
