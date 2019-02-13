alter table JOKERAPP_TABLE_ITEM rename column table_number to table_number__u83842 ;
alter table JOKERAPP_TABLE_ITEM alter column table_number__u83842 drop not null ;
alter table JOKERAPP_TABLE_ITEM add column TEBLE_CAPTION varchar(255) ;
-- alter table JOKERAPP_TABLE_ITEM add column TABLE_NUMBER integer ^
-- update JOKERAPP_TABLE_ITEM set TABLE_NUMBER = <default_value> ;
-- alter table JOKERAPP_TABLE_ITEM alter column table_number set not null ;
alter table JOKERAPP_TABLE_ITEM add column TABLE_NUMBER integer ;
