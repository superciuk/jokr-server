alter table JOKERAPP_PRODUCT_ITEM alter column PRICE rename to PRICE__U15064 ^
alter table JOKERAPP_PRODUCT_ITEM alter column PRICE__U15064 set null ;
alter table JOKERAPP_PRODUCT_ITEM add column PRICE bigint ^
update JOKERAPP_PRODUCT_ITEM set PRICE = 0 where PRICE is null ;
alter table JOKERAPP_PRODUCT_ITEM alter column PRICE set not null ;
