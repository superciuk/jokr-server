package com.joker.jokerapp.service;

import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.Ticket;

public interface PrinterService {

    String NAME = "jokerapp_PrinterService";

    void printTicket(TableItem tableToPrint, Ticket ticketToPrint, boolean reprint);

    boolean printBill(TableItem tableToPrint);

}