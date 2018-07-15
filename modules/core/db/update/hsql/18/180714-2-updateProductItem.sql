alter table JOKERAPP_PRODUCT_ITEM add column PRINTER_GROUP_ID varchar(36) ;
alter table JOKERAPP_PRODUCT_ITEM add column SORT_ORDER integer ;
alter table JOKERAPP_PRODUCT_ITEM add column PRICE double precision ^
update JOKERAPP_PRODUCT_ITEM set PRICE = 0 where PRICE is null ;
alter table JOKERAPP_PRODUCT_ITEM alter column PRICE set not null ;
