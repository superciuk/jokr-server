package com.joker.jokerapp.service;


import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.joker.jokerapp.entity.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.UUID;

@Service(OrderService.NAME)
public class OrderServiceBean implements OrderService {

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Override
    public String createNewOrder(String tableId, String actualSeats) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableId)).view("tableItem-view").one();

        if (tableItem.getTableStatus().equals(TableItemStatus.free)) {

            tableItem.setCurrentOrder(metadata.create(Order.class));
            tableItem.getCurrentOrder().setStatus(OrderStatus.open);
            tableItem.getCurrentOrder().setTableItemCaption(tableItem.getTableCaption());
            tableItem.getCurrentOrder().setActualSeats(Integer.parseInt(actualSeats));
            tableItem.getCurrentOrder().setCharge(BigDecimal.valueOf(0));
            tableItem.getCurrentOrder().setTaxes(BigDecimal.valueOf(0));

            if (tableItem.getWithServiceByDefault()) tableItem.getCurrentOrder().setWithService(true);
            else tableItem.getCurrentOrder().setWithService(false);

            tableItem.setTableStatus(TableItemStatus.open);

        }
        dataManager.commit(tableItem);
        return tableItem.getCurrentOrder().getId().toString();

    }

    @Override
    public boolean addToOrder(String orderId, String productItemId) {

        Ticket currentTicket=null;
        int max = 0;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        ProductItem productItemToAdd = dataManager.load(ProductItem.class).id(UUID.fromString(productItemId)).view("productItem-view").one();

        if (order.getTickets() != null) {
            for (Ticket ticket: order.getTickets()) {
                if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {currentTicket = ticket;}
                for (OrderLine line: ticket.getOrderLines()) {
                    if (!line.getIsModifier() && line.getPosition() > max) {
                        max = line.getPosition();
                    }
                }
            }
        }

        max += 100;

        if (currentTicket == null) {

            currentTicket = metadata.create(Ticket.class);
            currentTicket.setOrder(order);
            currentTicket.setTicketStatus(TicketStatus.notSended);
            if (order.getTickets() != null) currentTicket.setTicketNumber(order.getTickets().size() + 1);
            else currentTicket.setTicketNumber(1);
            currentTicket.setSubticketStatus("bn-fn-gn");
            currentTicket.setOrderLines(new ArrayList<>());
            if (order.getTickets() == null) order.setTickets(new ArrayList<>());
            order.getTickets().add(currentTicket);

        } else for (OrderLine line: currentTicket.getOrderLines())
            if (!line.getIsModifier() && !line.getHasModifier() && line.getItemId().toString().equals(productItemId)) {


                line.setQuantity(line.getQuantity() + 1);
                line.setPrice(line.getPrice().setScale(2).add(productItemToAdd.getPrice().setScale(2)));
                order.setCharge(order.getCharge().setScale(2).add(line.getUnitPrice().setScale(2)));

                dataManager.commit(line, currentTicket, order);

                return true;

            }

        OrderLine newOrderLine = metadata.create(OrderLine.class);

        newOrderLine.setQuantity(1);
        newOrderLine.setItemName(productItemToAdd.getName());
        newOrderLine.setItemId(productItemToAdd.getId());
        newOrderLine.setUnitPrice(productItemToAdd.getPrice().setScale(2));
        newOrderLine.setPrice(productItemToAdd.getPrice().setScale(2));
        newOrderLine.setTaxes(BigDecimal.ZERO);
        newOrderLine.setTicket(currentTicket);
        newOrderLine.setPosition(max);
        newOrderLine.setNextModifierPosition(max + 1);
        newOrderLine.setHasModifier(false);
        newOrderLine.setIsModifier(false);
        newOrderLine.setItemToModifyId(null);
        newOrderLine.setPrinterGroup(productItemToAdd.getPrinterGroup());

        if (newOrderLine.getPrinterGroup().equals(PrinterGroup.Bar) && currentTicket.getSubticketStatus().charAt(1) == 'n') {

            currentTicket.setSubticketStatus(currentTicket.getSubticketStatus().replace("bn", "bo"));

        } else if (newOrderLine.getPrinterGroup().equals(PrinterGroup.Fryer) && currentTicket.getSubticketStatus().charAt(4) == 'n') {

            currentTicket.setSubticketStatus(currentTicket.getSubticketStatus().replace("fn", "fo"));

        } else if (newOrderLine.getPrinterGroup().equals(PrinterGroup.Grill) && currentTicket.getSubticketStatus().charAt(7) == 'n') {

            currentTicket.setSubticketStatus(currentTicket.getSubticketStatus().replace("gn", "go"));

        }

        newOrderLine.setChecked(false);
        newOrderLine.setIsReversed(false);

        currentTicket.getOrderLines().add(newOrderLine);
        order.setCharge(order.getCharge().setScale(2).add(newOrderLine.getUnitPrice().setScale(2)));

        dataManager.commit(newOrderLine, currentTicket, order);

        return true;

    }

    @Override
    public boolean plusButtonPressed(String orderId, String orderLineId) {

        OrderLine selectedLine = null;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        if (order.getTickets() != null) {
            for (Ticket ticket: order.getTickets()) {
                for (OrderLine line: ticket.getOrderLines()) {
                    if (line.getId().toString().equals(orderLineId)) selectedLine = line;
                }
            }
        }

        if (selectedLine.getTicket().getTicketStatus().equals(TicketStatus.sended) || selectedLine.getTicket().getTicketStatus().equals(TicketStatus.closed)) {

            addToOrder(orderId, selectedLine.getItemId().toString());

        } else {

            BigDecimal SingleModifiersPrice = (selectedLine.getPrice().setScale(2).subtract(selectedLine.getUnitPrice().setScale(2).
                    multiply(BigDecimal.valueOf(selectedLine.getQuantity()))).divide(BigDecimal.
                    valueOf(selectedLine.getQuantity()), RoundingMode.FLOOR));

            selectedLine.setQuantity(selectedLine.getQuantity() + 1);
            selectedLine.setPrice(selectedLine.getPrice().setScale(2).add(selectedLine.getUnitPrice()).add(SingleModifiersPrice).setScale(2));

            order.setCharge(order.getCharge().setScale(2).add(selectedLine.getUnitPrice().setScale(2)));

            dataManager.commit(selectedLine, order);

    }
        return true;
    }

    @Override
    public boolean minusButtonPressed(String orderId, String orderLineId) {

        OrderLine selectedLine = null;
        Ticket currentTicket = null;

        CommitContext commitContext= new CommitContext();

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        for (Ticket ticket: order.getTickets()) {
            for (OrderLine line: ticket.getOrderLines()) {
                if (line.getId().toString().equals(orderLineId)) {selectedLine = line; currentTicket=selectedLine.getTicket();}
            }
        }

        if (selectedLine.getQuantity().equals(1)) {
            if (selectedLine.getHasModifier()) {
                for (OrderLine line: currentTicket.getOrderLines())
                    if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedLine.getId()))
                        commitContext.addInstanceToRemove(line);
            }
            order.setCharge(order.getCharge().setScale(2).subtract(selectedLine.getPrice().setScale(2)));
            commitContext.addInstanceToRemove(selectedLine);

        } else {

            order.setCharge(order.getCharge().setScale(2).subtract(selectedLine.getPrice().divide(BigDecimal.valueOf(selectedLine.getQuantity()))));
            selectedLine.setPrice(selectedLine.getPrice().setScale(2).subtract(selectedLine.getPrice().divide(BigDecimal.valueOf(selectedLine.getQuantity()))));
            selectedLine.setQuantity(selectedLine.getQuantity() - 1);
            commitContext.addInstanceToCommit(selectedLine);
        }

        commitContext.addInstanceToCommit(order);
        dataManager.commit(commitContext);
        return true;

    }

    @Override
    public boolean sendOrder(String orderId) {

        //removeEmptyTickets();

        Ticket currentTicket=null;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        if (order.getTickets() != null)
            for (Ticket ticket: order.getTickets())
                if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {currentTicket = ticket;}

        if (currentTicket != null) {
            //if (!doNotPrint) printTicket(currentTicketDc.getItem());
            currentTicket.setTicketStatus(TicketStatus.sended);
            dataManager.commit(currentTicket);
        }

        return true;

    }

}