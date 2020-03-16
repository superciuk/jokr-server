alter table JOKERAPP_TABLE_ITEM rename column table_number to table_number__u44081 ;
alter table JOKERAPP_TABLE_ITEM alter column table_number__u44081 drop not null ;
-- alter table JOKERAPP_TABLE_ITEM add column TABLE_NUMBER varchar(255) ^
-- update JOKERAPP_TABLE_ITEM set TABLE_NUMBER = <default_value> ;
-- alter table JOKERAPP_TABLE_ITEM alter column table_number set not null ;
alter table JOKERAPP_TABLE_ITEM add column TABLE_NUMBER varchar(255) ;
