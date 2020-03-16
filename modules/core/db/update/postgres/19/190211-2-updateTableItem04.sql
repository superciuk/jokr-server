update JOKERAPP_TABLE_ITEM set CHECKED = false where CHECKED is null ;
alter table JOKERAPP_TABLE_ITEM alter column CHECKED set not null ;
