-- update JOKERAPP_TABLE_ITEM set TABLE_NUMBER = <default_value> where TABLE_NUMBER is null ;
alter table JOKERAPP_TABLE_ITEM alter column TABLE_NUMBER set not null ;
