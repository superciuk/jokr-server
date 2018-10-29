alter table JOKERAPP_PRODUCT_MODIFIER rename column subtract_price to subtract_price__u74471 ;
alter table JOKERAPP_PRODUCT_MODIFIER rename column add_price to add_price__u81669 ;
alter table JOKERAPP_PRODUCT_MODIFIER add column ADD_PRICE decimal(12, 2) ;
alter table JOKERAPP_PRODUCT_MODIFIER add column SUBTRACT_PRICE decimal(12, 2) ;
