alter table JOKERAPP_ORDER_LINE add column IS_MODIFIER boolean ^
update JOKERAPP_ORDER_LINE set IS_MODIFIER = false where IS_MODIFIER is null ;
alter table JOKERAPP_ORDER_LINE alter column IS_MODIFIER set not null ;
