alter table JOKERAPP_TABLE_ITEM add column WITH_SERVICE_BY_DEFAULT boolean ^
update JOKERAPP_TABLE_ITEM set WITH_SERVICE_BY_DEFAULT = false where WITH_SERVICE_BY_DEFAULT is null ;
alter table JOKERAPP_TABLE_ITEM alter column WITH_SERVICE_BY_DEFAULT set not null ;
