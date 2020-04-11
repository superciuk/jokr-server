package com.joker.jokerapp.service;

import com.haulmont.cuba.core.global.CommitContext;
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

        if ((ticket.getSubticketStatus().charAt(1) != 'o') && (ticket.getSubticketStatus().charAt(4) != 'o') && (ticket.getSubticketStatus().charAt(7) != 'o')) ticket.setTicketStatus(TicketStatus.closed);
            else ticket.setTicketStatus(TicketStatus.sended);

        dataManager.commit(ticket);

        return true;

    };

    @Override
    public boolean bumpAll(String ticketId, String subticketsToBump) {

        CommitContext commitContext= new CommitContext();
        Ticket ticket = dataManager.load(Ticket.class).id(UUID.fromString(ticketId)).view("ticket-view").one();

        for (OrderLine line: ticket.getOrderLines()) {

            if (!line.getIsModifier() && !line.getIsReversed() && line.getPrinterGroup().equals(PrinterGroup.Bar) && subticketsToBump.charAt(0)=='y') {line.setChecked(true); commitContext.addInstanceToCommit(line);}
            if (!line.getIsModifier() && !line.getIsReversed() && line.getPrinterGroup().equals(PrinterGroup.Fryer) && subticketsToBump.charAt(1)=='y') {line.setChecked(true); commitContext.addInstanceToCommit(line);}
            if (!line.getIsModifier() && !line.getIsReversed() && line.getPrinterGroup().equals(PrinterGroup.Grill) && subticketsToBump.charAt(2)=='y') {line.setChecked(true); commitContext.addInstanceToCommit(line);}

        }

        if (subticketsToBump.charAt(0)=='y') ticket.setSubticketStatus(ticket.getSubticketStatus().replace("bo", "bc"));
        if (subticketsToBump.charAt(1)=='y') ticket.setSubticketStatus(ticket.getSubticketStatus().replace("fo", "fc"));
        if (subticketsToBump.charAt(2)=='y') ticket.setSubticketStatus(ticket.getSubticketStatus().replace("go", "gc"));

        if ((ticket.getSubticketStatus().charAt(1) != 'o') && (ticket.getSubticketStatus().charAt(4) != 'o') && (ticket.getSubticketStatus().charAt(7) != 'o')) ticket.setTicketStatus(TicketStatus.closed);
            else ticket.setTicketStatus(TicketStatus.sended);

        commitContext.addInstanceToCommit(ticket);

        dataManager.commit(commitContext);

        return true;

    };

}