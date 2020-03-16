alter table JOKERAPP_ORDER_LINE rename column order_id to order_id__u94708 ;
drop index IDX_JOKERAPP_ORDER_LINE_ON_ORDER ;
alter table JOKERAPP_ORDER_LINE drop constraint FK_JOKERAPP_ORDER_LINE_ON_ORDER ;
