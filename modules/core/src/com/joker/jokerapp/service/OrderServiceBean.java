package com.joker.jokerapp.service;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.joker.jokerapp.entity.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service(OrderService.NAME)
public class OrderServiceBean implements OrderService {

    @Inject
    private PrinterService printerService;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Override
    public boolean checkConnection() { return true; }

    @Override
    public String userLogin(String username, String userPassword) {

        User user;
        try {user = dataManager.load(User.class)
                .query("e.username like :username")
                .parameter("username", username)
                .one();
        } catch (Exception e) {
            return "Wrong User";
        }
        if (user.getEncryptedUserPassword().equals(userPassword)) return user.getId().toString();
        else return "Wrong Password";
    }

    @Override
    public boolean setNotificationToken (String userId, String token, String task) {

        switch (task) {
            case "set": {

                User user = dataManager.load(User.class).id(UUID.fromString(userId)).view("user-view").one();
                user.setNotificationToken(token);
                dataManager.commit(user);

                break;
            }
            case "unset": {

                User user = dataManager.load(User.class).id(UUID.fromString(userId)).view("user-view").one();
                user.setNotificationToken(null);
                dataManager.commit(user);

                break;
            }
            case "unsetAll":

                CommitContext commitContext= new CommitContext();
                List<User> users = dataManager.load(User.class).view("user-view").list();
                users.forEach(user -> {user.setNotificationToken(null); commitContext.addInstanceToCommit(user);});
                dataManager.commit(commitContext);

                break;
        }

        return true;

    }

    @Override
    public String createNewOrder(String tableId, String actualSeats, String userId, String orderInProgress) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableId)).view("tableItem-view").one();

        tableItem.setCurrentOrder(metadata.create(Order.class));
        tableItem.getCurrentOrder().setCurrentStatus(OrderStatus.empty);
        tableItem.getCurrentOrder().setTableItemCaption(tableItem.getTableCaption());
        tableItem.getCurrentOrder().setActualSeats(Integer.parseInt(actualSeats));
        tableItem.getCurrentOrder().setUser(dataManager.load(User.class).id(UUID.fromString(userId)).one());
        tableItem.getCurrentOrder().setCharge(BigDecimal.ZERO);
        tableItem.getCurrentOrder().setTaxes(BigDecimal.ZERO);
        tableItem.getCurrentOrder().setNextTicketNumber(1);
        tableItem.getCurrentOrder().setWithService(tableItem.getWithServiceByDefault());
        if (tableItem.getTableStatus().equals(TableItemStatus.reserved)) tableItem.setTableStatus(TableItemStatus.busyAndReserved);
        else tableItem.setTableStatus(TableItemStatus.busy);

        tableItem.getCurrentOrder().setOrderInProgress(orderInProgress.equals("true"));

        dataManager.commit(tableItem);
        return tableItem.getCurrentOrder().getId().toString();

    }

    @Override
    public String addToOrder(String orderId, String userId, String productItemId, String isModifier, String selectedOrderLineId, String plusOrMinus, String manualModifierText, String manualModifierPrice, String withModifiers) {

        Ticket currentTicket = null;
        int max = 0;

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        ProductItem productItemToAdd = null;
        OrderLine selectedOrderLine = null;

        if (!productItemId.equals("") && isModifier.equals("false")) productItemToAdd = dataManager.load(ProductItem.class).id(UUID.fromString(productItemId)).view("productItem-view").one();

        if (!selectedOrderLineId.equals("")) selectedOrderLine = dataManager.load(OrderLine.class).id(UUID.fromString(selectedOrderLineId)).view("order-line-view").one();

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

                setOrderTaxes(order);
                newLine.setItemId(productModifierToAdd.getId());

            } else {

                newLine.setItemName(" ".concat(manualModifierText));
                newLine.setUnitPrice(new BigDecimal(manualModifierPrice));
                newLine.setPrice(newLine.getUnitPrice());
                selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).add(newLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));
                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(newLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity()).setScale(1, RoundingMode.HALF_DOWN))));
                setOrderTaxes(order);

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
                currentTicket.setUser(dataManager.load(User.class).id(UUID.fromString(userId)).one());
                currentTicket.setTicketStatus(TicketStatus.notSended);
                currentTicket.setTicketNumber(order.getNextTicketNumber());
                currentTicket.setSubticketStatus("bn-fn-gn");
                currentTicket.setOrderLines(new ArrayList<>());
                if (order.getTickets() == null) order.setTickets(new ArrayList<>());
                order.getTickets().add(currentTicket);
                order.setNextTicketNumber(order.getNextTicketNumber()+1);

            } else {

                OrderLine existentLine=null;
                for (OrderLine line : currentTicket.getOrderLines()) if (!line.getIsModifier() && !line.getHasModifier() && line.getItemId().toString().equals(productItemId) && withModifiers.equals("false"))
                    if (selectedOrderLine==null && line.getUnitPrice().equals(productItemToAdd.getPrice())) existentLine=line;
                    else if (selectedOrderLine!=null && line.getUnitPrice().equals(selectedOrderLine.getUnitPrice())) existentLine=line;
                    if (existentLine!=null) {

                        existentLine.setQuantity(existentLine.getQuantity() + 1);
                        existentLine.setPrice(existentLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).add(existentLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN)));

                        order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(existentLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN)));
                        setOrderTaxes(order);
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
            newOrderLine.setIsBeverage(productItemToAdd.getCategory().getIsBeverage());
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

            order.setPreviousStatus(order.getCurrentStatus());
            order.setCurrentStatus(calculateNewOrderStatus(order, productItemToAdd));
            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(newOrderLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN)));
            setOrderTaxes(order);

            dataManager.commit(newOrderLine, currentTicket, order);
            return newOrderLine.getId().toString();

        }

    }

    @Override
    public boolean plusButtonPressed(String orderId, String userId, String selectedOrderLineId) {

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        OrderLine selectedOrderLine = dataManager.load(OrderLine.class).id(UUID.fromString(selectedOrderLineId)).view("order-line-view").one();

        if (!selectedOrderLine.getTicket().getTicketStatus().equals(TicketStatus.notSended)) {

            String addedOrderLineId;
            OrderLine addedOrderLine=null;

            if(selectedOrderLine.getHasModifier()) {

                addedOrderLineId = addToOrder(orderId, userId, selectedOrderLine.getItemId().toString(), "false", "", "", "", "", "true");

                order = dataManager.reload(order, "order-view");

                String plusOrMinus= "";

                for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                    if (line.getItemToModifyId()!=null && line.getItemToModifyId().equals(selectedOrderLine.getId())) {
                        if (line.getItemName().startsWith(" + ")) plusOrMinus = "plus"; else if(line.getItemName().startsWith(" - ")) plusOrMinus = "minus";
                        if (line.getItemId() != null) addToOrder(orderId, userId, line.getItemId().toString(), "true", addedOrderLineId, plusOrMinus, "", "", "false");
                        else addToOrder(orderId, userId, "", "true", addedOrderLineId, plusOrMinus, line.getItemName(), line.getUnitPrice().toString(), "false");
                    }
                } else addedOrderLineId = addToOrder(orderId, userId, selectedOrderLine.getItemId().toString(), "false", selectedOrderLineId, "", "", "","false");

            order = dataManager.reload(order, "order-view");
            for (Ticket ticket: order.getTickets()) {
                for (OrderLine line: ticket.getOrderLines()) {
                    if (line.getId().toString().equals(addedOrderLineId)) {addedOrderLine = line; break;}
                }
            }


            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(addedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
            addedOrderLine.setPrice(addedOrderLine.getPrice().subtract(addedOrderLine.getUnitPrice().multiply(BigDecimal.valueOf(addedOrderLine.getQuantity()))));
            addedOrderLine.setUnitPrice(selectedOrderLine.getUnitPrice());
            addedOrderLine.setPrice(addedOrderLine.getPrice().add(addedOrderLine.getUnitPrice().multiply(BigDecimal.valueOf(addedOrderLine.getQuantity())).setScale(1, RoundingMode.HALF_DOWN)));
            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(addedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
            setOrderTaxes(order);

            dataManager.commit(addedOrderLine, order);

        } else {

            selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));
            selectedOrderLine.setQuantity(selectedOrderLine.getQuantity() + 1);
            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));
            setOrderTaxes(order);

            dataManager.commit(selectedOrderLine, order);
        }

        return true;
    }

    @Override
    public boolean minusButtonPressed(String orderId, String selectedOrderLineId) {

        CommitContext commitContext= new CommitContext();

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        OrderLine selectedOrderLine = dataManager.load(OrderLine.class).id(UUID.fromString(selectedOrderLineId)).view("order-line-view").one();

        Ticket currentTicket = selectedOrderLine.getTicket();

        if (selectedOrderLine.getIsModifier()) {

            boolean hasMoreModifiers = false;

            OrderLine selectedOrderLineParent = dataManager.load(OrderLine.class).id(selectedOrderLine.getItemToModifyId()).view("order-line-view").one();

            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().multiply(BigDecimal.valueOf(selectedOrderLineParent.getQuantity()))));
            setOrderTaxes(order);
            selectedOrderLineParent.setPrice(selectedOrderLineParent.getPrice().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().multiply(BigDecimal.valueOf(selectedOrderLineParent.getQuantity()))));

            for (OrderLine line: currentTicket.getOrderLines()) {
                if (line.getIsModifier() && line.getItemToModifyId().equals(selectedOrderLineParent.getId()) && !line.getId().equals(selectedOrderLine.getId())) {hasMoreModifiers = true; break;}
            }

            if (!hasMoreModifiers) selectedOrderLineParent.setHasModifier(false);

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
                setOrderTaxes(order);
                commitContext.addInstanceToRemove(selectedOrderLine);

            } else {

                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));
                setOrderTaxes(order);
                selectedOrderLine.setPrice(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN)));
                selectedOrderLine.setQuantity(selectedOrderLine.getQuantity() - 1);
                commitContext.addInstanceToCommit(selectedOrderLine);
            }

        }

        commitContext.addInstanceToCommit(order);
        dataManager.commit(commitContext);

        order = dataManager.reload(order, "order-view");

        order.setPreviousStatus(order.getCurrentStatus());
        if (removeEmptyTickets(orderId)) {
            order = dataManager.reload(order, "order-view");
            order.setCurrentStatus(OrderStatus.empty);
        } else {
            order.setCurrentStatus(calculateNewOrderStatus(order, null));
        }

        dataManager.commit(order);

        return true;

    }

    @Override
    public boolean quantityButtonPressed(String orderId, String selectedOrderLineId, String operation) {

        CommitContext commitContext= new CommitContext();

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        OrderLine selectedOrderLine = dataManager.load(OrderLine.class).id(UUID.fromString(selectedOrderLineId)).view("order-line-view").one();

        if (operation.equals("remove") || selectedOrderLine.getTicket().getTicketStatus().equals(TicketStatus.notSended)) {

            if (selectedOrderLine.getHasModifier()) for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId())) commitContext.addInstanceToRemove(line);

            order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
            setOrderTaxes(order);
            commitContext.addInstanceToRemove(selectedOrderLine);

        } else {

            if (selectedOrderLine.getIsReversed()) {

                if (selectedOrderLine.getHasModifier()) for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                    if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId())) {line.setIsReversed(false); commitContext.addInstanceToCommit(line);}

                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
                setOrderTaxes(order);
                selectedOrderLine.setIsReversed(false);
                if(selectedOrderLine.getPrinterGroup().equals(PrinterGroup.Bar) && !selectedOrderLine.getChecked() && selectedOrderLine.getTicket().getSubticketStatus().charAt(1) == 'c')
                    selectedOrderLine.getTicket().setSubticketStatus(selectedOrderLine.getTicket().getSubticketStatus().replace("bc", "bo")); else
                if(selectedOrderLine.getPrinterGroup().equals(PrinterGroup.Fryer) && !selectedOrderLine.getChecked() && selectedOrderLine.getTicket().getSubticketStatus().charAt(4) == 'c')
                    selectedOrderLine.getTicket().setSubticketStatus(selectedOrderLine.getTicket().getSubticketStatus().replace("fc", "fo")); else
                if(selectedOrderLine.getPrinterGroup().equals(PrinterGroup.Grill) && !selectedOrderLine.getChecked() && selectedOrderLine.getTicket().getSubticketStatus().charAt(7) == 'c')
                    selectedOrderLine.getTicket().setSubticketStatus(selectedOrderLine.getTicket().getSubticketStatus().replace("gc", "go"));

            } else {

                if (selectedOrderLine.getHasModifier()) for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
                    if (line.getItemToModifyId() != null && (line.getItemToModifyId()).equals(selectedOrderLine.getId())) {line.setIsReversed(true); commitContext.addInstanceToCommit(line);}

                order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
                setOrderTaxes(order);
                selectedOrderLine.setIsReversed(true);

            }

            commitContext.addInstanceToCommit(selectedOrderLine);

        }

        commitContext.addInstanceToCommit(order);
        dataManager.commit(commitContext);

        removeEmptyTickets(orderId);

        return true;

    }

    @Override
    public boolean splitLineButtonPressed(String orderId, String selectedOrderLineId) {

        CommitContext commitContext= new CommitContext();

        OrderLine selectedOrderLine = dataManager.load(OrderLine.class).id(UUID.fromString(selectedOrderLineId)).view("order-line-view").one();

        OrderLine newLine = metadata.create(OrderLine.class);
        newLine.setQuantity(1);
        newLine.setTicket(selectedOrderLine.getTicket());
        newLine.setItemName(selectedOrderLine.getItemName());
        newLine.setItemId(selectedOrderLine.getItemId());
        newLine.setUnitPrice(selectedOrderLine.getUnitPrice());
        newLine.setPrice(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), RoundingMode.HALF_DOWN));
        newLine.setTaxes(selectedOrderLine.getTaxes());

        int addToPosition = 10;
        for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())
            if (selectedOrderLine.getPosition()+addToPosition== line.getPosition()) addToPosition+=10;
        newLine.setPosition(selectedOrderLine.getPosition()+addToPosition);

        newLine.setNextModifierPosition(selectedOrderLine.getNextModifierPosition()+addToPosition);
        newLine.setHasModifier(selectedOrderLine.getHasModifier());
        newLine.setIsModifier(selectedOrderLine.getIsModifier());
        newLine.setItemToModifyId(selectedOrderLine.getItemToModifyId());
        newLine.setChecked(false);
        newLine.setIsReversed(selectedOrderLine.getIsReversed());
        newLine.setPrinterGroup(selectedOrderLine.getPrinterGroup());
        newLine.setIsBeverage(selectedOrderLine.getIsBeverage());

        selectedOrderLine.setPrice(selectedOrderLine.getPrice().subtract(selectedOrderLine.getPrice().divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), RoundingMode.HALF_DOWN)));
        selectedOrderLine.setQuantity(selectedOrderLine.getQuantity()-1);

        commitContext.addInstanceToCommit(selectedOrderLine);
        commitContext.addInstanceToCommit(newLine);

        if (selectedOrderLine.getHasModifier())

            for (OrderLine line: selectedOrderLine.getTicket().getOrderLines())

                if (line.getItemToModifyId()!=null && line.getItemToModifyId().equals(selectedOrderLine.getId())) {

                    OrderLine newModifierLine = metadata.create(OrderLine.class);
                    newModifierLine.setQuantity(1);
                    newModifierLine.setTicket(line.getTicket());
                    newModifierLine.setItemName(line.getItemName());
                    newModifierLine.setItemId(line.getItemId());
                    newModifierLine.setUnitPrice(line.getUnitPrice());
                    newModifierLine.setPrice(line.getUnitPrice());
                    newModifierLine.setTaxes(line.getTaxes());
                    newModifierLine.setPosition(line.getPosition()+addToPosition);
                    newModifierLine.setHasModifier(line.getHasModifier());
                    newModifierLine.setIsModifier(line.getIsModifier());
                    newModifierLine.setItemToModifyId(newLine.getId());
                    newModifierLine.setChecked(false);
                    newModifierLine.setIsReversed(line.getIsReversed());
                    newModifierLine.setPrinterGroup(line.getPrinterGroup());
                    newModifierLine.setIsBeverage(line.getIsBeverage());

                    commitContext.addInstanceToCommit(newModifierLine);
                }

        dataManager.commit(commitContext);

        return true;

        }

    @Override
    public boolean priceButtonPressed(String orderId, String selectedOrderLineId, String price) {

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        OrderLine selectedOrderLine = dataManager.load(OrderLine.class).id(UUID.fromString(selectedOrderLineId)).view("order-line-view").one();

        order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
        BigDecimal modifiersPrice = selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN).subtract(selectedOrderLine.getUnitPrice().setScale(1, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(selectedOrderLine.getQuantity())));
        selectedOrderLine.setPrice(new BigDecimal(price));
        selectedOrderLine.setUnitPrice(selectedOrderLine.getPrice().subtract(modifiersPrice).divide(BigDecimal.valueOf(selectedOrderLine.getQuantity()), 1, RoundingMode.HALF_DOWN));
        order.setCharge(order.getCharge().setScale(1, RoundingMode.HALF_DOWN).add(selectedOrderLine.getPrice().setScale(1, RoundingMode.HALF_DOWN)));
        setOrderTaxes(order);

        dataManager.commit(selectedOrderLine, order);

        return true;

    }

    @Override
    public boolean sendOrder(String tableItemId, String ticketId, String sendAndClose, String printTicket) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();

        if (!ticketId.equals("") && printTicket.equals("true")) {
            Ticket ticketToPrint = dataManager.load(Ticket.class).id(UUID.fromString(ticketId)).view("ticket-view").one();
            printerService.printTicket(tableItem, ticketToPrint, true);
        } else {

            Ticket currentTicket=null;

            Order order = tableItem.getCurrentOrder();

            removeEmptyTickets(order.getId().toString());

            if (order.getTickets() != null)
                for (Ticket ticket: order.getTickets())
                    if (ticket.getTicketStatus().equals(TicketStatus.notSended)) {currentTicket = ticket; break;}

            if (currentTicket != null) {
                if (printTicket.equals("true")) printerService.printTicket(tableItem, currentTicket, false);
                currentTicket.setTicketStatus(TicketStatus.sended);

                dataManager.commit(currentTicket);

            }
            order = dataManager.reload(order, "order-view");
            if (sendAndClose.equals("true")) order.setOrderInProgress(false);

            dataManager.commit(order);

        }

        return true;
    }

    @Override
    public boolean removeEmptyTickets(String orderId) {

        Order currentOrder = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        CommitContext commitContext = new CommitContext();
        boolean theOrderIsNowEmpty=false;

        for (Ticket ticket: currentOrder.getTickets()) if (ticket.getOrderLines().size() == 0) {
            commitContext.addInstanceToRemove(ticket);
            if (ticket.getTicketStatus().equals(TicketStatus.notSended)) currentOrder.setNextTicketNumber(currentOrder.getNextTicketNumber()-1);
        }

        dataManager.commit(commitContext);

        currentOrder = dataManager.reload(currentOrder, "order-view");

        if (currentOrder.getTickets().size() == 0 ) {

            currentOrder.setPreviousStatus(currentOrder.getCurrentStatus());
            currentOrder.setNextTicketNumber(1);
            currentOrder.setCurrentStatus(OrderStatus.empty);

            dataManager.commit(currentOrder);
            theOrderIsNowEmpty=true;

        }

        return theOrderIsNowEmpty;

    }

    @Override
    public boolean freeTable(String tableItemId) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();
        Order currentOrder = tableItem.getCurrentOrder();

        currentOrder.setPreviousStatus(currentOrder.getCurrentStatus());
        currentOrder.setCurrentStatus(OrderStatus.closed);
        tableItem.setLastOrder(currentOrder);
        tableItem.setCurrentOrder(null);
        if (tableItem.getTableStatus().equals(TableItemStatus.busyAndReserved)) tableItem.setTableStatus(TableItemStatus.reserved);
        else tableItem.setTableStatus(TableItemStatus.free);

        dataManager.commit(currentOrder, tableItem);

        return true;

    }

    @Override
    public boolean reopenTable(String tableItemId, String whichOrder) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();

        if (whichOrder.equals("lastOrder")) {
            tableItem.setCurrentOrder(tableItem.getLastOrder());
            tableItem.setLastOrder(null);
        }

        Order currentOrder = tableItem.getCurrentOrder();
        OrderStatus tempOrderStatus = tableItem.getCurrentOrder().getCurrentStatus();
        currentOrder.setCurrentStatus(tableItem.getCurrentOrder().getPreviousStatus());
        currentOrder.setPreviousStatus(tempOrderStatus);
        if (tableItem.getTableStatus().equals(TableItemStatus.reserved)) tableItem.setTableStatus(TableItemStatus.busyAndReserved);
        else tableItem.setTableStatus(TableItemStatus.busy);

        dataManager.commit(currentOrder, tableItem);

        return true;

    }

    @Override
    public boolean tableReservation(String tableId, String operation, String tableItemReservationName, String tableItemReservationSeats, String tableItemReservationTime, String tableItemReservationPhoneNumber) {

        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableId)).view("tableItem-view").one();

        if (operation.equals("add") || operation.equals("modify")) {
            if (!tableItemReservationName.equals("undefined") && !tableItemReservationName.equals("")) tableItem.setTableReservationName(tableItemReservationName);
            else tableItem.setTableReservationName("N/D");
            if (!tableItemReservationSeats.equals("undefined") && !tableItemReservationSeats.equals("")) tableItem.setTableReservationSeats(tableItemReservationSeats);
            else tableItem.setTableReservationSeats("N/D");
            if (!tableItemReservationTime.equals("undefined") && !tableItemReservationTime.equals("")) tableItem.setTableReservationTime(tableItemReservationTime);
            else tableItem.setTableReservationTime("N/D");
            if (!tableItemReservationPhoneNumber.equals("undefined") && !tableItemReservationPhoneNumber.equals("")) tableItem.setTableReservationPhoneNumber(tableItemReservationPhoneNumber);
            else tableItem.setTableReservationPhoneNumber("N/D");

            if (tableItem.getTableStatus().equals(TableItemStatus.free)) tableItem.setTableStatus(TableItemStatus.reserved);
            if (tableItem.getTableStatus().equals(TableItemStatus.busy)) tableItem.setTableStatus(TableItemStatus.busyAndReserved);
        }
        if (operation.equals("remove")) {
            tableItem.setTableReservationName(null);
            tableItem.setTableReservationSeats(null);
            tableItem.setTableReservationTime(null);
            tableItem.setTableReservationPhoneNumber(null);
            if (tableItem.getTableStatus().equals(TableItemStatus.busyAndReserved)) tableItem.setTableStatus(TableItemStatus.busy);
            if (tableItem.getTableStatus().equals(TableItemStatus.reserved)) tableItem.setTableStatus(TableItemStatus.free);
        }

        dataManager.commit(tableItem);

        return true;
    }

    @Override
    public boolean renameTable(String orderId, String newTableCaption) {

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        order.setTableItemCaption(newTableCaption);

        dataManager.commit(order);

        return true;

    }

    @Override
    public boolean setActualSeats(String orderId, String newActualSeats) {

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();
        order.setActualSeats(Integer.parseInt(newActualSeats));

        dataManager.commit(order);

        return true;

    }

    @Override
    public String setWithService(String orderId, String trueOrFalse) {

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        order.setWithService(trueOrFalse.equals("true"));

        setOrderTaxes(order);

        dataManager.commit(order);

        return order.getTaxes().toString();

    }

    @Override
    public boolean moveTable(String tableItemToMoveId, String newTableItemId) {

        TableItem tableItemToMove = dataManager.load(TableItem.class).id(UUID.fromString(tableItemToMoveId)).view("tableItem-view").one();
        TableItem newTableItem = dataManager.load(TableItem.class).id(UUID.fromString(newTableItemId)).view("tableItem-view").one();

        Order currentOrder = tableItemToMove.getCurrentOrder();

        tableItemToMove.setCurrentOrder(null);
        tableItemToMove.setTableStatus(TableItemStatus.free);

        dataManager.commit(tableItemToMove);

        currentOrder.setWithService(newTableItem.getWithServiceByDefault());
        if (currentOrder.getTableItemCaption().equals(tableItemToMove.getTableCaption())) currentOrder.setTableItemCaption(newTableItem.getTableCaption());

        setOrderTaxes(currentOrder);

        newTableItem.setCurrentOrder(currentOrder);
        newTableItem.setTableStatus(TableItemStatus.busy);

        dataManager.commit(currentOrder, newTableItem);

        return true;

    }

    @Override
    public boolean printBill(String tableItemId) {


        TableItem tableItem = dataManager.load(TableItem.class).id(UUID.fromString(tableItemId)).view("tableItem-view").one();

        if (printerService.printBill(tableItem)) {

            Order currentOrder=tableItem.getCurrentOrder();
            if (!currentOrder.getCurrentStatus().equals(OrderStatus.bill)) {
                currentOrder.setPreviousStatus(tableItem.getCurrentOrder().getCurrentStatus());
                currentOrder.setCurrentStatus(OrderStatus.bill);
                tableItem.setChecked(false);
            }

            dataManager.commit(currentOrder, tableItem);

        }

        return true;

    }

    @Override
    public boolean setOrderInProgress (String orderId, String trueOrFalse) {

        Order order = dataManager.load(Order.class).id(UUID.fromString(orderId)).view("order-view").one();

        if (trueOrFalse.equals("true")) order.setOrderInProgress(true);
        else order.setOrderInProgress(false);

        dataManager.commit(order);

        return true;

    }

    protected void setOrderTaxes (Order currentOrder) {

        if (currentOrder.getWithService()) {

            BigDecimal service = BigDecimal.valueOf(Math.round(currentOrder.getCharge().multiply(BigDecimal.valueOf(0.1)).subtract(BigDecimal.valueOf(0.2)).multiply(BigDecimal.valueOf(2)).doubleValue()) / 2.0f).setScale(2);
            currentOrder.setTaxes(service);

        } else currentOrder.setTaxes(BigDecimal.ZERO);

    }

    protected OrderStatus calculateNewOrderStatus (Order order, ProductItem productItemToAdd) {

        OrderStatus newCurrentStatus=order.getCurrentStatus();

        if (productItemToAdd!=null) {
            if (order.getCurrentStatus().equals(OrderStatus.empty) && productItemToAdd.getCategory().getIsBeverage()) newCurrentStatus=OrderStatus.onlyBeverage;
            else if (order.getCurrentStatus().equals(OrderStatus.empty) && !productItemToAdd.getCategory().getIsBeverage()) newCurrentStatus=OrderStatus.onlyFood;
            else if (order.getCurrentStatus().equals(OrderStatus.onlyBeverage) && !productItemToAdd.getCategory().getIsBeverage()) newCurrentStatus=OrderStatus.foodAndBeverage;
            else if (order.getCurrentStatus().equals(OrderStatus.onlyFood) && productItemToAdd.getCategory().getIsBeverage()) newCurrentStatus=OrderStatus.foodAndBeverage;
        } else {
            boolean hasBeverage = false;
            boolean hasFood = false;
            for (Ticket ticket: order.getTickets()) {for (OrderLine line: ticket.getOrderLines()) {if (!line.getIsModifier()&&(line.getIsBeverage())) hasBeverage=true; else hasFood=true;}}
            if (hasBeverage && !hasFood) newCurrentStatus=OrderStatus.onlyBeverage; else if (!hasBeverage && hasFood) newCurrentStatus=OrderStatus.onlyFood; else newCurrentStatus=OrderStatus.foodAndBeverage;
        }

        return newCurrentStatus;
    }

}