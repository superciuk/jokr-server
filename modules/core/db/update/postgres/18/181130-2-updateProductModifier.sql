alter table JOKERAPP_PRODUCT_MODIFIER rename column subtract_price to subtract_price__u42618 ;
alter table JOKERAPP_PRODUCT_MODIFIER rename column add_price to add_price__u84772 ;
alter table JOKERAPP_PRODUCT_MODIFIER add column ADD_PRICE double precision ;
alter table JOKERAPP_PRODUCT_MODIFIER add column SUBTRACT_PRICE double precision ;
