alter table JOKERAPP_TABLE_ITEM_AREA rename column table_number to table_number__u70543 ;
-- alter table JOKERAPP_TABLE_ITEM_AREA add column AREA_NUMBER integer ^
-- update JOKERAPP_TABLE_ITEM_AREA set AREA_NUMBER = <default_value> ;
-- alter table JOKERAPP_TABLE_ITEM_AREA alter column AREA_NUMBER set not null ;
alter table JOKERAPP_TABLE_ITEM_AREA add column AREA_NUMBER integer ;
