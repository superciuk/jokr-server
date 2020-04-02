package com.joker.jokerapp.service;

import com.haulmont.cuba.core.global.DataManager;
import com.joker.jokerapp.entity.OrderLine;
import com.joker.jokerapp.entity.PrinterGroup;
import com.joker.jokerapp.entity.Ticket;
import com.joker.jokerapp.entity.TicketStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Service(KitchenService.NAME)
public class KitchenServiceBean implements KitchenService {

    @Inject
    private DataManager dataManager;

    @Override
    public boolean bumpLine(String ticketId, String orderLineId) {

        boolean allCheckedBar=true;
        boolean allCheckedFryer=true;
        boolean allCheckedGrill=true;

        Ticket ticket = dataManager.load(Ticket.class).id(UUID.fromString(ticketId)).view("ticket-view").one();

        for (OrderLine line: ticket.getOrderLines()) if (line.getId().toString().equals(orderLineId)) {line.setChecked(!line.getChecked()); dataManager.commit(line); break;}
        for (OrderLine line: ticket.getOrderLines()) {
            if (!line.getIsModifier() && !line.getChecked() && !line.getIsReversed() && line.getPrinterGroup().equals(PrinterGroup.Bar)) allCheckedBar = false;
            else if (!line.getIsModifier() && !line.getChecked() && !line.getIsReversed() && line.getPrinterGroup().equals(PrinterGroup.Fryer)) allCheckedFryer = false;
            else if (!line.getIsModifier() && !line.getChecked() && !line.getIsReversed() && line.getPrinterGroup().equals(PrinterGroup.Grill)) allCheckedGrill = false;
            if (!allCheckedBar && !allCheckedFryer && !allCheckedGrill) break;
        }
        if (allCheckedBar) ticket.setSubticketStatus(ticket.getSubticketStatus().replace("bo", "bc")); else
                                ticket.setSubticketStatus(ticket.getSubticketStatus().replace("bc", "bo"));
        if (allCheckedFryer) ticket.setSubticketStatus(ticket.getSubticketStatus().replace("fo", "fc")); else
                                ticket.setSubticketStatus(ticket.getSubticketStatus().replace("fc", "fo"));
        if (allCheckedGrill) ticket.setSubticketStatus(ticket.getSubticketStatus().replace("go", "gc")); else
                                ticket.setSubticketStatus(ticket.getSubticketStatus().replace("gc", "go"));

        if ((allCheckedBar || ticket.getSubticketStatus().charAt(1) == 'n') &&
                (allCheckedFryer || ticket.getSubticketStatus().charAt(4) == 'n') &&
                (allCheckedGrill || ticket.getSubticketStatus().charAt(7) == 'n')) ticket.setTicketStatus(TicketStatus.closed);

        dataManager.commit(ticket);

        return true;

    };

}