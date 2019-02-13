alter table JOKERAPP_ORDER add column TAKING_THE_ORDER boolean ^
update JOKERAPP_ORDER set TAKING_THE_ORDER = false where TAKING_THE_ORDER is null ;
alter table JOKERAPP_ORDER alter column TAKING_THE_ORDER set not null ;
