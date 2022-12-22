-- update JOKERAPP_USER set WORKPLACE_ID = <default_value> where WORKPLACE_ID is null ;
alter table JOKERAPP_USER alter column WORKPLACE_ID set not null ;
