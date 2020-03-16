alter table JOKERAPP_PRODUCT_ITEM rename column price to price__u20088 ;
alter table JOKERAPP_PRODUCT_ITEM alter column price__u20088 drop not null ;
alter table JOKERAPP_PRODUCT_ITEM add column PRICE bigint ^
update JOKERAPP_PRODUCT_ITEM set PRICE = 0 where PRICE is null ;
alter table JOKERAPP_PRODUCT_ITEM alter column PRICE set not null ;
