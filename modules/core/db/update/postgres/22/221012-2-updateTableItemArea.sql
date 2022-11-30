-- update JOKERAPP_TABLE_ITEM_AREA set AREA_NUMBER = <default_value> where AREA_NUMBER is null ;
alter table JOKERAPP_TABLE_ITEM_AREA alter column AREA_NUMBER set not null ;
