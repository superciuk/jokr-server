alter table JOKERAPP_TABLE_ITEM rename column table_area to table_area__u30482 ;
alter table JOKERAPP_TABLE_ITEM alter column table_area__u30482 drop not null ;
alter table JOKERAPP_TABLE_ITEM add column TABLE_AREA varchar(255) ^
update JOKERAPP_TABLE_ITEM set TABLE_AREA = '' where TABLE_AREA is null ;
alter table JOKERAPP_TABLE_ITEM alter column TABLE_AREA set not null ;
