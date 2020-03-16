alter table JOKERAPP_TABLE_ITEM rename column order_id to order_id__u38749 ;
alter table JOKERAPP_TABLE_ITEM rename column number_ to number___u42191 ;
alter table JOKERAPP_TABLE_ITEM alter column number___u42191 drop not null ;
-- alter table JOKERAPP_TABLE_ITEM add column TABLE_NUMBER integer ^
-- update JOKERAPP_TABLE_ITEM set TABLE_NUMBER = <default_value> ;
-- alter table JOKERAPP_TABLE_ITEM alter column TABLE_NUMBER set not null ;
alter table JOKERAPP_TABLE_ITEM add column TABLE_NUMBER integer ;
alter table JOKERAPP_TABLE_ITEM add column ORDER_ID uuid ;
