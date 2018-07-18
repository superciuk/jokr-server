-- begin JOKERAPP_PRODUCT_ITEM
alter table JOKERAPP_PRODUCT_ITEM add constraint FK_JOKERAPP_PRODUCT_ITEM_PRINTER_GROUP foreign key (PRINTER_GROUP_ID) references JOKERAPP_PRINTER_GROUP(ID)^
alter table JOKERAPP_PRODUCT_ITEM add constraint FK_JOKERAPP_PRODUCT_ITEM_CATEGORY foreign key (CATEGORY_ID) references JOKERAPP_PRODUCT_ITEM_CATEGORY(ID)^
create index IDX_JOKERAPP_PRODUCT_ITEM_PRINTER_GROUP on JOKERAPP_PRODUCT_ITEM (PRINTER_GROUP_ID)^
create index IDX_JOKERAPP_PRODUCT_ITEM_CATEGORY on JOKERAPP_PRODUCT_ITEM (CATEGORY_ID)^
-- end JOKERAPP_PRODUCT_ITEM
-- begin JOKERAPP_TABLE_ITEM
alter table JOKERAPP_TABLE_ITEM add constraint FK_JOKERAPP_TABLE_ITEM_AREA foreign key (AREA_ID) references JOKERAPP_TABLE_ITEM_AREA(ID)^
create index IDX_JOKERAPP_TABLE_ITEM_AREA on JOKERAPP_TABLE_ITEM (AREA_ID)^
-- end JOKERAPP_TABLE_ITEM
-- begin JOKERAPP_TICKET_ITEM_LINE
alter table JOKERAPP_TICKET_ITEM_LINE add constraint FK_JOKERAPP_TICKET_ITEM_LINE_PRODUCT_ITEM foreign key (PRODUCT_ITEM_ID) references JOKERAPP_PRODUCT_ITEM(ID)^
alter table JOKERAPP_TICKET_ITEM_LINE add constraint FK_JOKERAPP_TICKET_ITEM_LINE_TICKET_ITEM foreign key (TICKET_ITEM_ID) references JOKERAPP_TICKET_ITEM(ID)^
create index IDX_JOKERAPP_TICKET_ITEM_LINE_PRODUCT_ITEM on JOKERAPP_TICKET_ITEM_LINE (PRODUCT_ITEM_ID)^
create index IDX_JOKERAPP_TICKET_ITEM_LINE_TICKET_ITEM on JOKERAPP_TICKET_ITEM_LINE (TICKET_ITEM_ID)^
-- end JOKERAPP_TICKET_ITEM_LINE
-- begin JOKERAPP_PRODUCT_MODIFIER
alter table JOKERAPP_PRODUCT_MODIFIER add constraint FK_JOKERAPP_PRODUCT_MODIFIER_CATEGORY foreign key (CATEGORY_ID) references JOKERAPP_PRODUCT_MODIFIER_CATEGORY(ID)^
create index IDX_JOKERAPP_PRODUCT_MODIFIER_CATEGORY on JOKERAPP_PRODUCT_MODIFIER (CATEGORY_ID)^
-- end JOKERAPP_PRODUCT_MODIFIER
-- begin JOKERAPP_TICKET_ITEM
alter table JOKERAPP_TICKET_ITEM add constraint FK_JOKERAPP_TICKET_ITEM_TABLE_ITEM foreign key (TABLE_ITEM_ID) references JOKERAPP_TABLE_ITEM(ID)^
create index IDX_JOKERAPP_TICKET_ITEM_TABLE_ITEM on JOKERAPP_TICKET_ITEM (TABLE_ITEM_ID)^
-- end JOKERAPP_TICKET_ITEM
-- begin JOKERAPP_PRINTER_GROUP
alter table JOKERAPP_PRINTER_GROUP add constraint FK_JOKERAPP_PRINTER_GROUP_PRINTER foreign key (PRINTER_ID) references JOKERAPP_PRINTER(ID)^
create index IDX_JOKERAPP_PRINTER_GROUP_PRINTER on JOKERAPP_PRINTER_GROUP (PRINTER_ID)^
-- end JOKERAPP_PRINTER_GROUP