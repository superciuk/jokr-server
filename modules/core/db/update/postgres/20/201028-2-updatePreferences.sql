alter table JOKERAPP_PREFERENCES rename column pref_value to pref_value__u27671 ;
alter table JOKERAPP_PREFERENCES rename column pref_key to pref_key__u80048 ;
alter table JOKERAPP_PREFERENCES add column SCREEN_ORIENTATION varchar(255) ;
alter table JOKERAPP_PREFERENCES add column UNIT varchar(255) ;
