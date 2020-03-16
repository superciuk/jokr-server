alter table JOKERAPP_PRODUCT_MODIFIER alter column PRICE rename to PRICE__UNUSED ;
alter table JOKERAPP_PRODUCT_MODIFIER add column ADD_PRICE double precision ;
alter table JOKERAPP_PRODUCT_MODIFIER add column SUBTRACT_PRICE double precision ;
