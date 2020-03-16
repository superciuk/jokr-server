alter table JOKERAPP_ORDER_LINE rename column is_printed to is_printed__u78306 ;
alter table JOKERAPP_ORDER_LINE alter column is_printed__u78306 drop not null ;
alter table JOKERAPP_ORDER_LINE add column IS_SENDED boolean ^
update JOKERAPP_ORDER_LINE set IS_SENDED = false where IS_SENDED is null ;
alter table JOKERAPP_ORDER_LINE alter column IS_SENDED set not null ;
