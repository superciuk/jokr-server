-- alter table JOKERAPP_TICKET_ITEM_LINE add column TICKET_ITEM_ID varchar(36) ^
-- update JOKERAPP_TICKET_ITEM_LINE set TICKET_ITEM_ID = <default_value> ;
-- alter table JOKERAPP_TICKET_ITEM_LINE alter column TICKET_ITEM_ID set not null ;
alter table JOKERAPP_TICKET_ITEM_LINE add column TICKET_ITEM_ID varchar(36) not null ;
