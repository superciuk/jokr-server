update JOKERAPP_TABLE_ITEM set TABLE_STATUS = '0' where TABLE_STATUS is null ;
alter table JOKERAPP_TABLE_ITEM alter column TABLE_STATUS set not null ;
