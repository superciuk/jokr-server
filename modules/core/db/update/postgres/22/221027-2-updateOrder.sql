-- alter table JOKERAPP_ORDER add column USER_ID uuid ^
-- update JOKERAPP_ORDER set USER_ID = <default_value> ;
-- alter table JOKERAPP_ORDER alter column USER_ID set not null ;
alter table JOKERAPP_ORDER add column USER_ID uuid not null ;
