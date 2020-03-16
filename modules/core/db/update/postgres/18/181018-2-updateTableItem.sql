alter table JOKERAPP_TABLE_ITEM rename column price to price__u84390 ;
alter table JOKERAPP_TABLE_ITEM add column CHARGE bigint ;
alter table JOKERAPP_TABLE_ITEM alter column STATUS drop not null ;
