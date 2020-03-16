alter table JOKERAPP_ORDER add column WITH_SERVICE boolean ^
update JOKERAPP_ORDER set WITH_SERVICE = false where WITH_SERVICE is null ;
alter table JOKERAPP_ORDER alter column WITH_SERVICE set not null ;
