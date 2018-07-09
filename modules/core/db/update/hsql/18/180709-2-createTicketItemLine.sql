alter table JOKERAPP_TICKET_ITEM_LINE add constraint FK_JOKERAPP_TICKET_ITEM_LINE_PRODUCT_ITEM foreign key (PRODUCT_ITEM_ID) references JOKERAPP_PRODUCT_ITEM(ID);
create index IDX_JOKERAPP_TICKET_ITEM_LINE_PRODUCT_ITEM on JOKERAPP_TICKET_ITEM_LINE (PRODUCT_ITEM_ID);
