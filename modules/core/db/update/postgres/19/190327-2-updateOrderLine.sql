alter table JOKERAPP_ORDER_LINE rename column is_sended to is_sended__u08139 ;
alter table JOKERAPP_ORDER_LINE alter column is_sended__u08139 drop not null ;
alter table JOKERAPP_ORDER_LINE add column ISDONE boolean ;
