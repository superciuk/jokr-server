alter table JOKERAPP_ORDER add constraint FK_JOKERAPP_ORDER_USER foreign key (USER_ID) references JOKERAPP_USER(ID);
create index IDX_JOKERAPP_ORDER_USER on JOKERAPP_ORDER (USER_ID);
