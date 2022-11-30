alter table JOKERAPP_TABLE_ITEM rename column table_area to table_area__u21752 ;
alter table JOKERAPP_TABLE_ITEM alter column table_area__u21752 drop not null ;
alter table JOKERAPP_TABLE_ITEM add column TABLE_AREA_ID uuid ;
