alter table JOKERAPP_ORDER_LINE add column HAS_MODIFIER boolean ^
update JOKERAPP_ORDER_LINE set HAS_MODIFIER = false where HAS_MODIFIER is null ;
alter table JOKERAPP_ORDER_LINE alter column HAS_MODIFIER set not null ;
alter table JOKERAPP_ORDER_LINE add column IS_PRINTED boolean ^
update JOKERAPP_ORDER_LINE set IS_PRINTED = false where IS_PRINTED is null ;
alter table JOKERAPP_ORDER_LINE alter column IS_PRINTED set not null ;
