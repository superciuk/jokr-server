alter table JOKERAPP_TICKET_ITEM_LINE add constraint FK_JOKERAPP_TICKET_ITEM_LINE_TICKET_ITEM foreign key (TICKET_ITEM_ID) references JOKERAPP_TICKET_ITEM(ID);
create index IDX_JOKERAPP_TICKET_ITEM_LINE_TICKET_ITEM on JOKERAPP_TICKET_ITEM_LINE (TICKET_ITEM_ID);