update JOKERAPP_PRODUCT_MODIFIER set ADD_PRICE = 0 where ADD_PRICE is null ;
alter table JOKERAPP_PRODUCT_MODIFIER alter column ADD_PRICE set not null ;
update JOKERAPP_PRODUCT_MODIFIER set SUBTRACT_PRICE = 0 where SUBTRACT_PRICE is null ;
alter table JOKERAPP_PRODUCT_MODIFIER alter column SUBTRACT_PRICE set not null ;
