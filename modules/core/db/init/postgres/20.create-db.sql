-- begin JOKERAPP_PRODUCT_ITEM
alter table JOKERAPP_PRODUCT_ITEM add constraint FK_JOKERAPP_PRODUCT_ITEM_CATEGORY foreign key (CATEGORY_ID) references JOKERAPP_PRODUCT_ITEM_CATEGORY(ID)^
alter table JOKERAPP_PRODUCT_ITEM add constraint FK_JOKERAPP_PRODUCT_ITEM_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID)^
create unique index IDX_JOKERAPP_PRODUCT_ITEM_UK_NAME on JOKERAPP_PRODUCT_ITEM (NAME) where DELETE_TS is null ^
create index IDX_JOKERAPP_PRODUCT_ITEM_CATEGORY on JOKERAPP_PRODUCT_ITEM (CATEGORY_ID)^
create index IDX_JOKERAPP_PRODUCT_ITEM_IMAGE on JOKERAPP_PRODUCT_ITEM (IMAGE_ID)^
-- end JOKERAPP_PRODUCT_ITEM
-- begin JOKERAPP_PRODUCT_MODIFIER
alter table JOKERAPP_PRODUCT_MODIFIER add constraint FK_JOKERAPP_PRODUCT_MODIFIER_ON_CATEGORY foreign key (CATEGORY_ID) references JOKERAPP_PRODUCT_MODIFIER_CATEGORY(ID)^
create index IDX_JOKERAPP_PRODUCT_MODIFIER_ON_CATEGORY on JOKERAPP_PRODUCT_MODIFIER (CATEGORY_ID)^
-- end JOKERAPP_PRODUCT_MODIFIER

-- begin JOKERAPP_TABLE_ITEM
alter table JOKERAPP_TABLE_ITEM add constraint FK_JOKERAPP_TABLE_ITEM_TABLE_AREA foreign key (TABLE_AREA_ID) references JOKERAPP_TABLE_ITEM_AREA(ID)^
alter table JOKERAPP_TABLE_ITEM add constraint FK_JOKERAPP_TABLE_ITEM_CURRENT_ORDER foreign key (CURRENT_ORDER_ID) references JOKERAPP_ORDER(ID)^
alter table JOKERAPP_TABLE_ITEM add constraint FK_JOKERAPP_TABLE_ITEM_LAST_ORDER foreign key (LAST_ORDER_ID) references JOKERAPP_ORDER(ID)^
create unique index IDX_JOKERAPP_TABLE_ITEM_UK_TABLE_NUMBER on JOKERAPP_TABLE_ITEM (TABLE_NUMBER) where DELETE_TS is null ^
create unique index IDX_JOKERAPP_TABLE_ITEM_UK_CURRENT_ORDER_ID on JOKERAPP_TABLE_ITEM (CURRENT_ORDER_ID) where DELETE_TS is null ^
create unique index IDX_JOKERAPP_TABLE_ITEM_UK_LAST_ORDER_ID on JOKERAPP_TABLE_ITEM (LAST_ORDER_ID) where DELETE_TS is null ^
create index IDX_JOKERAPP_TABLE_ITEM_TABLE_AREA on JOKERAPP_TABLE_ITEM (TABLE_AREA_ID)^
-- end JOKERAPP_TABLE_ITEM
-- begin JOKERAPP_ORDER_LINE
alter table JOKERAPP_ORDER_LINE add constraint FK_JOKERAPP_ORDER_LINE_TICKET foreign key (TICKET_ID) references JOKERAPP_TICKET(ID)^
create index IDX_JOKERAPP_ORDER_LINE_TICKET on JOKERAPP_ORDER_LINE (TICKET_ID)^
-- end JOKERAPP_ORDER_LINE
-- begin JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK
alter table JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK add constraint FK_PROITEPROMODCAT_ON_PRODUCT_MODIFIER_CATEGORY foreign key (PRODUCT_MODIFIER_CATEGORY_ID) references JOKERAPP_PRODUCT_MODIFIER_CATEGORY(ID)^
alter table JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK add constraint FK_PROITEPROMODCAT_ON_PRODUCT_ITEM foreign key (PRODUCT_ITEM_ID) references JOKERAPP_PRODUCT_ITEM(ID)^
-- end JOKERAPP_PRODUCT_ITEM_PRODUCT_MODIFIER_CATEGORY_LINK
-- begin JOKERAPP_TICKET
alter table JOKERAPP_TICKET add constraint FK_JOKERAPP_TICKET_ORDER foreign key (ORDER_ID) references JOKERAPP_ORDER(ID)^
alter table JOKERAPP_TICKET add constraint FK_JOKERAPP_TICKET_USER foreign key (USER_ID) references JOKERAPP_USER(ID)^
create index IDX_JOKERAPP_TICKET_ORDER on JOKERAPP_TICKET (ORDER_ID)^
create index IDX_JOKERAPP_TICKET_USER on JOKERAPP_TICKET (USER_ID)^
-- end JOKERAPP_TICKET
-- begin JOKERAPP_PREFERENCES
alter table JOKERAPP_PREFERENCES add constraint FK_JOKERAPP_PREFERENCES_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_JOKERAPP_PREFERENCES_USER on JOKERAPP_PREFERENCES (USER_ID)^
-- end JOKERAPP_PREFERENCES
-- begin JOKERAPP_TABLE_ITEM_AREA
create unique index IDX_JOKERAPP_TABLE_ITEM_AREA_UK_AREA_NUMBER on JOKERAPP_TABLE_ITEM_AREA (AREA_NUMBER) where DELETE_TS is null ^
create unique index IDX_JOKERAPP_TABLE_ITEM_AREA_UK_AREA_NAME on JOKERAPP_TABLE_ITEM_AREA (AREA_NAME) where DELETE_TS is null ^
-- end JOKERAPP_TABLE_ITEM_AREA
-- begin JOKERAPP_USER
create unique index IDX_JOKERAPP_USER_UK_USERNAME on JOKERAPP_USER (USERNAME) where DELETE_TS is null ^
-- end JOKERAPP_USER
-- begin JOKERAPP_ORDER
alter table JOKERAPP_ORDER add constraint FK_JOKERAPP_ORDER_USER foreign key (USER_ID) references JOKERAPP_USER(ID)^
create index IDX_JOKERAPP_ORDER_USER on JOKERAPP_ORDER (USER_ID)^
-- end JOKERAPP_ORDER
