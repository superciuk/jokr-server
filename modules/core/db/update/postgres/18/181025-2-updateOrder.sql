alter table JOKERAPP_ORDER rename column tax_amount to tax_amount__u29859 ;
alter table JOKERAPP_ORDER rename column item_price to item_price__u58570 ;
alter table JOKERAPP_ORDER add column ITEM_PRICE decimal(12, 2) ;
alter table JOKERAPP_ORDER add column TAX_AMOUNT decimal(12, 2) ;
