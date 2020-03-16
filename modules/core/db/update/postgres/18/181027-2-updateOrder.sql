alter table JOKERAPP_ORDER rename column order_id to order_id__u78365 ;
alter table JOKERAPP_ORDER alter column order_id__u78365 drop not null ;
drop index IDX_JOKERAPP_ORDER_ON_ORDER ;
alter table JOKERAPP_ORDER drop constraint FK_JOKERAPP_ORDER_ON_ORDER ;
