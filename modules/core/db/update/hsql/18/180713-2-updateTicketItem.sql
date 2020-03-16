-- alter table JOKERAPP_TICKET_ITEM add column TABLE_ITEM_ID varchar(36) ^
-- update JOKERAPP_TICKET_ITEM set TABLE_ITEM_ID = <default_value> ;
-- alter table JOKERAPP_TICKET_ITEM alter column TABLE_ITEM_ID set not null ;
alter table JOKERAPP_TICKET_ITEM add column TABLE_ITEM_ID varchar(36) not null ;
