alter table JOKERAPP_PRODUCT_MODIFIER rename column subtract_price to subtract_price__u20862 ;
alter table JOKERAPP_PRODUCT_MODIFIER rename column add_price to add_price__u46426 ;
alter table JOKERAPP_PRODUCT_MODIFIER add column ADD_PRICE bigint ;
alter table JOKERAPP_PRODUCT_MODIFIER add column SUBTRACT_PRICE bigint ;
