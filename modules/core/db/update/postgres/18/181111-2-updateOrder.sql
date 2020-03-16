alter table JOKERAPP_ORDER rename column table_item_id to table_item_id__u46894 ;
alter table JOKERAPP_ORDER alter column table_item_id__u46894 drop not null ;
drop index IDX_JOKERAPP_ORDER_ON_TABLE_ITEM ;
alter table JOKERAPP_ORDER drop constraint FK_JOKERAPP_ORDER_ON_TABLE_ITEM ;
