alter table JOKERAPP_ORDER rename column status to status__u86042 ;
alter table JOKERAPP_ORDER alter column status__u86042 drop not null ;
alter table JOKERAPP_ORDER add column PREVIOUS_STATUS varchar(50) ;
alter table JOKERAPP_ORDER add column CURRENT_STATUS varchar(50) ^
update JOKERAPP_ORDER set CURRENT_STATUS = 'empty' where CURRENT_STATUS is null ;
alter table JOKERAPP_ORDER alter column CURRENT_STATUS set not null ;
