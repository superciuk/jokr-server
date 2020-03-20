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
    private PrinterService printerService;

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
    public String addToOrder(String orderId, String productItemId, String isModifier, String selectedOrderLineId, String plusOrMinus, String withModifiers) {

        Ticket currentTicket = null;
        int max = 0;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        if (isModifier.equals("true")) {

            OrderLine selectedOrderLine = null;

            for (Ticket ticket: order.getTickets()) {
                for (OrderLine line: ticket.getOrderLines()) {
                    if (line.getId().toString().equals(selectedOrderLineId)) {selectedOrderLine = line; break;}
                }
            }

            ProductModifier productModifierToAdd = dataManager.load(ProductModifier.class).id(UUID.fromString(productItemId)).view("productModifier-view").one();

            OrderLine newLine = metadata.create(OrderLine.class);

            newLine.setQuantity(1);

            if (plusOrMinus.equals("plus")) {

                newLine.setItemName(" + ".concat(productModifierToAdd.getName()));
                newLine.setUnitPrice(productModifierToAdd.getAddPrice());
                newLine.setPrice(productModifierToAdd.getAddPrice());
                selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(2).add(productModifierToAdd.getAddPrice().setScale(2).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(2))));
                order.setCharge(order.getCharge().setScale(2).add(productModifierToAdd.getAddPrice().setScale(2).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(2))));

            } else {

                newLine.setItemName(" - ".concat(productModifierToAdd.getName()));
                newLine.setUnitPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));
                newLine.setPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));
                selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(2).subtract(productModifierToAdd.getSubtractPrice().setScale(2).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(2))));
                order.setCharge(order.getCharge().setScale(2).subtract(productModifierToAdd.getSubtractPrice().setScale(2).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(2))));

            }

            newLine.setItemId(productModifierToAdd.getId());
            newLine.setTaxes(BigDecimal.ZERO);
            newLine.setTicket(selectedOrderLine.getTicket());
            newLine.setPosition(selectedOrderLine.getNextModifierPosition());
            selectedOrderLine.setNextModifierPosition(selectedOrderLine.getNextModifierPosition()+1);
            newLine.setHasModifier(false);
            newLine.setIsModifier(Boolean.TRUE);
            newLine.setItemToModifyId(selectedOrderLine.getId());
            newLine.setPrinterGroup(selectedOrderLine.getPrinterGroup());
            newLine.setChecked(false);
            newLine.setIsReversed(false);

            if (selectedOrderLine.getHasModifier().equals(Boolean.FALSE)) selectedOrderLine.setHasModifier(Boolean.TRUE);

            dataManager.commit(newLine, selectedOrderLine, order);

            return newLine.getId().toString();

        } else {

            ProductItem productItemToAdd = dataManager.load(ProductItem.class).id(UUID.fromString(productItemId)).view("productItem-view").one();

            if (order.getTickets() != null) {
                for (Ticket ticket : order.getTickets()) {
                    for (OrderLine line : ticket.getOrderLines()) {
                        if (!line.getIsModifier() && line.getPosition() > max) {
                            max = line.getPosition();
                        }
                    }
                    if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {currentTicket = ticket;break;}
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

            } else for (OrderLine line : currentTicket.getOrderLines())
                if (!line.getIsModifier() && !line.getHasModifier() && line.getItemId().toString().equals(productItemId) && withModifiers.equals("false")) {


                    line.setQuantity(line.getQuantity() + 1);
                    line.setPrice(line.getPrice().setScale(2).add(productItemToAdd.getPrice().setScale(2)));
                    order.setCharge(order.getCharge().setScale(2).add(line.getUnitPrice().setScale(2)));

                    dataManager.commit(line, currentTicket, order);

                    return line.getId().toString();

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
            return newOrderLine.getId().toString();

        }

    }

    @Override
    public boolean plusButtonPressed(String orderId, String selectedOrderLineId) {

        OrderLine selectedOrderLine = null;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        for (Ticket ticket: order.getTickets()) {
            for (OrderLine line: ticket.getOrderLines()) {
                if (line.getId().toString().equals(selectedOrderLineId)) {selectedOrderLine = line; break;}
            }
        }

        if (!selectedOrderLine.getTicket().getTicketStatus().equals(TicketStatus.notSended)) {

            if(selectedOrderLine.getHasModifier()) {

                String newOrderLineId = addToOrder(orderId, selectedOrderLine.getItemId().toString(), "false", null, null, "true");

                dataManager.reload(order, "order-view");

                String plusOrMinus = "";

                for (OrderLine line: selectedOrderLine.getTicket().getOrderLines()) {
                        if ( line.getItemToModifyId()!=null && line.getItemToModifyId().toString().equals(selectedOrderLineId)) {
                            if (line.getItemName().startsWith(" + ")) plusOrMinus = "plus"; else plusOrMinus = "minus";
                            addToOrder(orderId, line.getItemId().toString(), "true", newOrderLineId, plusOrMinus, "false");
                        }
                    }
                } else addToOrder(orderId, selectedOrderLine.getItemId().toString(), "false", null, null, "false");

        } else {

            selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(2).add(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), RoundingMode.FLOOR).setScale(2)));
            selectedOrderLine.setQuantity(selectedOrderLine.getQuantity() + 1);

            order.setCharge(order.getCharge().setScale(2).add(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), RoundingMode.FLOOR).setScale(2)));

            dataManager.commit(selectedOrderLine, order);
        }

        return true;
    }

    @Override
    public boolean minusButtonPressed(String orderId, String selectedOrderLineId) {

        OrderLine selectedOrderLine = null;
        Ticket currentTicket = null;

        CommitContext commitContext= new CommitContext();

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        for (Ticket ticket: order.getTickets()) {
            for (OrderLine line: ticket.getOrderLines()) {
                if (line.getId().toString().equals(selectedOrderLineId)) {selectedOrderLine = line; break;}
            }
            if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {currentTicket = ticket;break;}
        }

        if (selectedOrderLine.getIsModifier()) {

            OrderLine selectedOrderLineParent = null;

            for (OrderLine line: currentTicket.getOrderLines()) {
                if (line.getId().equals(selectedOrderLine.getItemToModifyId())) {selectedOrderLineParent = line; break;}
            }

            order.setCharge(order.getCharge().setScale(2).subtract(selectedOrderLine.getPrice().multiply(BigDecimal.valueOf(selectedOrderLineParent.getQuantity()))));
            selectedOrderLineParent.setPrice(selectedOrderLineParent.getPrice().setScale(2).subtract(selectedOrderLine.getPrice().multiply(BigDecimal.valueOf(selectedOrderLineParent.getQuantity()))));
            commitContext.addInstanceToRemove(selectedOrderLine);
            commitContext.addInstanceToCommit(selectedOrderLineParent);

        } else {

            if (selectedOrderLine.getQuantity().equals(1)) {
                if (selectedOrderLine.getHasModifier()) {
                    for (OrderLine line: currentTicket.getOrderLines())
                        if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId()))
                            commitContext.addInstanceToRemove(line);
                }
                order.setCharge(order.getCharge().setScale(2).subtract(selectedOrderLine.getPrice().setScale(2)));
                commitContext.addInstanceToRemove(selectedOrderLine);

            } else {

                order.setCharge(order.getCharge().setScale(2).subtract(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()))));
                selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(2).subtract(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()))));
                selectedOrderLine.setQuantity(selectedOrderLine.getQuantity() - 1);
                commitContext.addInstanceToCommit(selectedOrderLine);
            }

        }

        commitContext.addInstanceToCommit(order);
        dataManager.commit(commitContext);
        return true;

    }

    @Override
    public boolean sendOrder(String tableItemId) {

        //removeEmptyTickets();

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();

        Ticket currentTicket=null;

        Order order = tableItem.getCurrentOrder();

        if (order.getTickets() != null)
            for (Ticket ticket: order.getTickets())
                if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {currentTicket = ticket; break;}

        if (currentTicket != null) {

            printerService.printTicket(tableItem, currentTicket);
            //if (!doNotPrint) printTicket(currentTicketDc.getItem());
            currentTicket.setTicketStatus(TicketStatus.sended);
            dataManager.commit(currentTicket);

        }

        return true;

    }

    @Override
    public boolean printBill(String tableItemId) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();

        if (printerService.printBill(tableItem)) {

            tableItem.getCurrentOrder().setStatus(OrderStatus.bill);
            tableItem.setChecked(false);

            dataManager.commit(tableItem.getCurrentOrder(), tableItem);

        }

        return true;

    }

}