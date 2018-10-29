alter table JOKERAPP_ORDER rename column status to status__u24451 ;
alter table JOKERAPP_ORDER alter column status__u24451 drop not null ;
