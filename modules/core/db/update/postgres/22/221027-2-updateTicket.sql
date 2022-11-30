-- alter table JOKERAPP_TICKET add column USER_ID uuid ^
-- update JOKERAPP_TICKET set USER_ID = <default_value> ;
-- alter table JOKERAPP_TICKET alter column USER_ID set not null ;
alter table JOKERAPP_TICKET add column USER_ID uuid not null ;
