alter table JOKERAPP_PREFERENCES add constraint FK_JOKERAPP_PREFERENCES_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_JOKERAPP_PREFERENCES_USER on JOKERAPP_PREFERENCES (USER_ID);