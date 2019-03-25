package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.joker.jokerapp.entity.OrderLine;
import com.joker.jokerapp.entity.PrinterGroup;
import com.joker.jokerapp.entity.Ticket;
import com.joker.jokerapp.entity.TicketStatus;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class KitchenDisplay extends AbstractWindow {

    @Named("kitchenDisplayMainBox")
    private ScrollBoxLayout kitchenDisplayMainBox;

    @Named("showBarTicketsBtn")
    private Button showBarTicketsBtn;

    @Named("showFryerTicketsBtn")
    private Button showFryerTicketsBtn;

    @Named("showGrillTicketsBtn")
    private Button showGrillTicketsBtn;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private CollectionDatasource<Ticket, UUID> ticketsDs;

    private Boolean showBarTickets = true;
    private Boolean showFryerTickets = true;
    private Boolean showGrillTickets = true;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        Timer clockTimer = componentsFactory.createTimer();
        addTimer(clockTimer);
        clockTimer.setDelay(1000);
        clockTimer.setRepeating(true);
        clockTimer.addActionListener(timer -> refreshData());

        clockTimer.start();

        ticketsDs.refresh();

        drawTickets();

    }

    private HBoxLayout createOrderLineHBox(OrderLine orderLine) {

        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

        hBoxLayout.setId("hBoxLayout".concat(orderLine.getId().toString()));

        Label quantity = componentsFactory.createComponent(Label.class);
        Button itemName = componentsFactory.createComponent(Button.class);

        quantity.setWidth("20px");
        itemName.setWidth("475px");

        quantity.setHeight("40px");
        itemName.setHeight("40px");

        quantity.setId("quantity".concat(orderLine.getId().toString()));
        itemName.setId("itemName".concat(orderLine.getId().toString()));

        quantity.setAlignment(Alignment.MIDDLE_LEFT);

        if (!orderLine.getIsModifier()) quantity.setValue(orderLine.getQuantity());

        if (orderLine.getItemName().length() < 50) itemName.setCaption(orderLine.getItemName());
        else
            itemName.setCaption(orderLine.getItemName().substring(0, 27).concat("...").concat(orderLine.getItemName().substring(orderLine.getItemName().length() - 20)));

        hBoxLayout.add(quantity);
        hBoxLayout.add(itemName);

        return hBoxLayout;

    }

    private void setOrderLineStyle(OrderLine orderLine, ScrollBoxLayout scrollBox) {

        if (scrollBox.getOwnComponent("hBoxLayout".concat(orderLine.getId().toString())) != null) {

            Label quantity = (Label) scrollBox.getComponent("quantity".concat(orderLine.getId().toString()));
            Button itemName = (Button) scrollBox.getComponent("itemName".concat(orderLine.getId().toString()));

            if (orderLine.getTicket().getTicketStatus().equals(TicketStatus.sended)) {

                if (orderLine.getIsModifier()) {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                            quantity.setStyleName("gridItem-label-isModifier-isSended-isReversed-even");
                            itemName.setStyleName("gridItem-button-isModifier-isSended-isReversed-even");

                        } else {

                            quantity.setStyleName("gridItem-label-isModifier-isSended-isReversed-odd");
                            itemName.setStyleName("gridItem-button-isModifier-isSended-isReversed-odd");

                        }

                    } else {

                        if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                            quantity.setStyleName("gridItem-label-isModifier-isSended-even");
                            itemName.setStyleName("gridItem-button-isModifier-isSended-even");

                        } else {

                            quantity.setStyleName("gridItem-label-isModifier-isSended-odd");
                            itemName.setStyleName("gridItem-button-isModifier-isSended-odd");

                        }

                    }

                } else {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                            quantity.setStyleName("gridItem-label-isSended-isReversed-even");
                            itemName.setStyleName("gridItem-button-isSended-isReversed-even");

                        } else {

                            quantity.setStyleName("gridItem-label-isSended-isReversed-odd");
                            itemName.setStyleName("gridItem-button-isSended-isReversed-odd");

                        }

                    } else {

                        if (orderLine.getTicket().getTicketNumber() % 2 == 0) {

                            quantity.setStyleName("gridItem-label-isSended-even");
                            itemName.setStyleName("gridItem-button-isSended-even");

                        } else {

                            quantity.setStyleName("gridItem-label-isSended-odd");
                            itemName.setStyleName("gridItem-button-isSended-odd");

                        }

                    }

                }

            } else {

                if (orderLine.getIsModifier()) {

                    quantity.setStyleName("gridItem-label-isModifier");
                    itemName.setStyleName("gridItem-button-isModifier");

                } else {

                    quantity.setStyleName("gridItem-label");
                    itemName.setStyleName("gridItem-button");

                }

            }

        }

    }


    public void onBarBtnClick() {

        if (showBarTickets) {

            showBarTicketsBtn.setStyleName("doNotPrintBtnNotPushed");
            //doNotPrintBtn.setCaption("STAMPA LE<br>COMANDE");
            showBarTickets = false;

        } else {

            showBarTicketsBtn.setStyleName("doNotPrintBtnPushed");
            //doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");
            showBarTickets = true;
        }

    }

    public void onFryerBtnClick() {

        if (showFryerTickets) {

            showFryerTicketsBtn.setStyleName("doNotPrintBtnNotPushed");
            //doNotPrintBtn.setCaption("STAMPA LE<br>COMANDE");
            showFryerTickets = false;

        } else {

            showFryerTicketsBtn.setStyleName("doNotPrintBtnPushed");
            //doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");
            showFryerTickets = true;
        }

    }

    public void onGrillBtnClick() {

        if (showGrillTickets) {

            showGrillTicketsBtn.setStyleName("doNotPrintBtnNotPushed");
            //doNotPrintBtn.setCaption("STAMPA LE<br>COMANDE");
            showGrillTickets = false;

        } else {

            showGrillTicketsBtn.setStyleName("doNotPrintBtnPushed");
            //doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");
            showGrillTickets = true;
        }

    }

    private void refreshData() {

        //currentTimeField.setValue(Date.from(Instant.now()));

        ticketsDs.refresh();

        kitchenDisplayMainBox.removeAll();

        drawTickets();

    }

    private void drawTickets() {

        for (Ticket ticket: ticketsDs.getItems()) {

            if (ticket.getTicketStatus().equals(TicketStatus.sended)) {

                ScrollBoxLayout ticketScrollBox = componentsFactory.createComponent(ScrollBoxLayout.class);
                ticketScrollBox.setId("ticketScrollBox".concat(ticket.getId().toString()));
                ticketScrollBox.setHeightFull();
                ticketScrollBox.setWidth("500px");

                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

                hBoxLayout.setId("hBoxLayout".concat(ticket.getOrder().getTableItemCaption()));

                Button tableName = componentsFactory.createComponent(Button.class);

                tableName.setWidth("475px");

                tableName.setHeight("40px");

                tableName.setId("tableName".concat(ticket.getOrder().getTableItemCaption()));

                tableName.setCaption("TAVOLO ".concat(ticket.getOrder().getTableItemCaption()));

                hBoxLayout.add(tableName);

                ticketScrollBox.add(hBoxLayout);

                for (OrderLine orderLine: ticket.getOrderLines()) {

                    if (orderLine.getPrinterGroup().equals("Bar") && showBarTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals("Fryer") && showFryerTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals("Grill") && showGrillTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                }

                if (ticketScrollBox.getOwnComponents().size() > 1) kitchenDisplayMainBox.add(ticketScrollBox);

            }

        }

    }

}