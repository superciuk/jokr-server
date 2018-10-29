alter table JOKERAPP_TABLE_ITEM rename column charge to charge__u79208 ;
alter table JOKERAPP_TABLE_ITEM rename column discount to discount__u12822 ;
alter table JOKERAPP_TABLE_ITEM rename column tax to tax__u18502 ;
alter table JOKERAPP_TABLE_ITEM add column TAX decimal(12, 2) ;
alter table JOKERAPP_TABLE_ITEM add column DISCOUNT decimal(12, 2) ;
alter table JOKERAPP_TABLE_ITEM add column CHARGE decimal(12, 2) ;
