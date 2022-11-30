alter table JOKERAPP_TABLE_ITEM rename column table_hall to table_hall__u29548 ;
alter table JOKERAPP_TABLE_ITEM add column TABLE_AREA integer ^
update JOKERAPP_TABLE_ITEM set TABLE_AREA = 0 where TABLE_AREA is null ;
alter table JOKERAPP_TABLE_ITEM alter column TABLE_AREA set not null ;
