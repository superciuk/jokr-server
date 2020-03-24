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
    public String addToOrder(String orderId, String productItemId, String isModifier, String selectedOrderLineId, String plusOrMinus, String manualModifierText, String manualModifierPrice, String withModifiers) {

        Ticket currentTicket = null;
        int max = 0;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        ProductItem productItemToAdd = null;
        OrderLine selectedOrderLine = null;

        if (!productItemId.equals("") && isModifier.equals("false")) productItemToAdd = dataManager.load(ProductItem.class).id(UUID.fromString(productItemId)).view("productItem-view").one();

        if (!selectedOrderLineId.equals("")) for (Ticket ticket: order.getTickets()) for (OrderLine line: ticket.getOrderLines()) if (line.getId().toString().equals(selectedOrderLineId)) {selectedOrderLine = line; break;}

        if (isModifier.equals("true")) {

            OrderLine newLine = metadata.create(OrderLine.class);

            newLine.setQuantity(1);

            if (!productItemId.equals("")) {

                ProductModifier productModifierToAdd = dataManager.load(ProductModifier.class).id(UUID.fromString(productItemId)).view("productModifier-view").one();
                if (plusOrMinus.equals("plus")) {

                    newLine.setItemName(" + ".concat(productModifierToAdd.getName()));
                    newLine.setUnitPrice(productModifierToAdd.getAddPrice());
                    newLine.setPrice(productModifierToAdd.getAddPrice());
                    selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).add(productModifierToAdd.getAddPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));
                    order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(productModifierToAdd.getAddPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));

                } else {

                    newLine.setItemName(" - ".concat(productModifierToAdd.getName()));
                    newLine.setUnitPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));
                    newLine.setPrice(BigDecimal.valueOf(-((productModifierToAdd.getSubtractPrice()).doubleValue())));
                    selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).subtract(productModifierToAdd.getSubtractPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));
                    order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(productModifierToAdd.getSubtractPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));

                }
                newLine.setItemId(productModifierToAdd.getId());

            } else {

                newLine.setItemName(" ".concat(manualModifierText));
                newLine.setUnitPrice(new BigDecimal(manualModifierPrice));
                newLine.setPrice(newLine.getUnitPrice());
                selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).add(newLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));
                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(newLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));

            }

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

            if (order.getTickets() != null) for (Ticket ticket: order.getTickets()) {
                for (OrderLine line: ticket.getOrderLines()) if (!line.getIsModifier() && line.getPosition() > max) {max = line.getPosition();}
                if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {currentTicket = ticket;break;}
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

            } else {

                OrderLine existentLine=null;
                for (OrderLine line : currentTicket.getOrderLines()) if (!line.getIsModifier() && !line.getHasModifier() && line.getItemId().toString().equals(productItemId) && withModifiers.equals("false"))
                    if (selectedOrderLine==null && line.getUnitPrice().equals(productItemToAdd.getPrice())) existentLine=line;
                    else if (selectedOrderLine!=null && line.getUnitPrice().equals(selectedOrderLine.getUnitPrice())) existentLine=line;
                    if (existentLine!=null) {

                        existentLine.setQuantity(existentLine.getQuantity() + 1);
                        existentLine.setPrice(existentLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).add(existentLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN)));

                        order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(existentLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN)));
                        dataManager.commit(existentLine, currentTicket, order);
                        return existentLine.getId().toString();

                    }

            }

            OrderLine newOrderLine = metadata.create(OrderLine.class);

            newOrderLine.setQuantity(1);
            newOrderLine.setItemName(productItemToAdd.getName());
            newOrderLine.setItemId(productItemToAdd.getId());
            newOrderLine.setUnitPrice(productItemToAdd.getPrice().setScale(1, RoundingMode.HALF_DOWN));
            newOrderLine.setPrice(productItemToAdd.getPrice().setScale(1, RoundingMode.HALF_DOWN));
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
            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(newOrderLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN)));

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

            String addedOrderLineId;
            OrderLine addedOrderLine=null;

            if(selectedOrderLine.getHasModifier()) {

                addedOrderLineId = addToOrder(orderId, selectedOrderLine.getItemId().toString(), "false", "", "", "", "", "true");

                order = dataManager.reload(order, "order-view");

                String plusOrMinus= "";

                for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                    if (line.getItemToModifyId()!=null && line.getItemToModifyId().toString().equals(selectedOrderLineId)) {
                        if (line.getItemName().startsWith(" + ")) plusOrMinus = "plus"; else if(line.getItemName().startsWith(" - ")) plusOrMinus = "minus";
                        if (line.getItemId() != null) addToOrder(orderId, line.getItemId().toString(), "true", addedOrderLineId, plusOrMinus, "", "", "false");
                        else addToOrder(orderId, "", "true", addedOrderLineId, plusOrMinus, line.getItemName(), line.getUnitPrice().toString(), "false");
                    }
                } else addedOrderLineId = addToOrder(orderId, selectedOrderLine.getItemId().toString(), "false", selectedOrderLineId, "", "", "","false");

            order = dataManager.reload(order, "order-view");
            for (Ticket ticket: order.getTickets()) {
                for (OrderLine line: ticket.getOrderLines()) {
                    if (line.getId().toString().equals(addedOrderLineId)) {addedOrderLine = line; break;}
                }
            }

            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(addedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
            addedOrderLine.setUnitPrice(selectedOrderLine.getUnitPrice());
            addedOrderLine.setPrice(addedOrderLine.getUnitPrice().multiply(BigDecimal.valueOf(addedOrderLine.getQuantity())).setScale(1, RoundingMode.HALF_DOWN));
            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(addedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));

            dataManager.commit(addedOrderLine, order);

        } else {

            selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));
            selectedOrderLine.setQuantity(selectedOrderLine.getQuantity() + 1);
            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));

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

            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().multiply(BigDecimal.valueOf(selectedOrderLineParent.getQuantity()))));
            selectedOrderLineParent.setPrice(selectedOrderLineParent.getPrice().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().multiply(BigDecimal.valueOf(selectedOrderLineParent.getQuantity()))));
            commitContext.addInstanceToRemove(selectedOrderLine);
            commitContext.addInstanceToCommit(selectedOrderLineParent);

        } else {

            if (selectedOrderLine.getQuantity().equals(1)) {
                if (selectedOrderLine.getHasModifier()) {
                    for (OrderLine line: currentTicket.getOrderLines())
                        if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId()))
                            commitContext.addInstanceToRemove(line);
                }
                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
                commitContext.addInstanceToRemove(selectedOrderLine);

            } else {

                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));
                selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));
                selectedOrderLine.setQuantity(selectedOrderLine.getQuantity() - 1);
                commitContext.addInstanceToCommit(selectedOrderLine);
            }

        }

        commitContext.addInstanceToCommit(order);
        dataManager.commit(commitContext);
        return true;

    }

    @Override
    public boolean quantityButtonPressed(String orderId, String selectedOrderLineId) {

        OrderLine selectedOrderLine = null;

        CommitContext commitContext= new CommitContext();

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        for (Ticket ticket: order.getTickets()) for (OrderLine line: ticket.getOrderLines()) if (line.getId().toString().equals(selectedOrderLineId)) {selectedOrderLine = line; break;}

        if (selectedOrderLine.getTicket().getTicketStatus().equals(TicketStatus.notSended)) {

            if (selectedOrderLine.getHasModifier()) for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId())) commitContext.addInstanceToRemove(line);

            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
            commitContext.addInstanceToRemove(selectedOrderLine);

        } else {

            if (selectedOrderLine.getIsReversed()) {

                if (selectedOrderLine.getHasModifier()) for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                    if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId())) {line.setIsReversed(false); commitContext.addInstanceToCommit(line);}

                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
                selectedOrderLine.setIsReversed(false);

            } else {

                if (selectedOrderLine.getHasModifier()) for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                    if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId())) {line.setIsReversed(true); commitContext.addInstanceToCommit(line);}

                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
                selectedOrderLine.setIsReversed(true);

            }

            commitContext.addInstanceToCommit(selectedOrderLine);

        }

        commitContext.addInstanceToCommit(order);
        dataManager.commit(commitContext);
        return true;

    }

    @Override
    public boolean priceButtonPressed(String orderId, String selectedOrderLineId, String price) {

        OrderLine selectedOrderLine = null;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        for (Ticket ticket: order.getTickets()) for (OrderLine line: ticket.getOrderLines()) if (line.getId().toString().equals(selectedOrderLineId)) {selectedOrderLine = line; break;}

        order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
        selectedOrderLine.setPrice(new BigDecimal(price));
        selectedOrderLine.setUnitPrice(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN));
        order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));

        dataManager.commit(selectedOrderLine, order);
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
    public boolean freeTable(String tableItemId) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();
        Order currentOrder = tableItem.getCurrentOrder();

        currentOrder.setStatus(OrderStatus.closed);
        tableItem.setCurrentOrder(null);
        tableItem.setTableStatus(TableItemStatus.free);

        dataManager.commit(currentOrder, tableItem);

        return true;

    }

    @Override
    public boolean reopenTable(String tableItemId) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();

        tableItem.getCurrentOrder().setStatus(OrderStatus.open);
        tableItem.setTableStatus(TableItemStatus.open);

        dataManager.commit(tableItem.getCurrentOrder(), tableItem);

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