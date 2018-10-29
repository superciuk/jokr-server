alter table JOKERAPP_TABLE_ITEM alter column PRICE rename to PRICE__U72305 ^
alter table JOKERAPP_TABLE_ITEM add column CHARGE bigint ;
alter table JOKERAPP_TABLE_ITEM alter column STATUS set null ;
