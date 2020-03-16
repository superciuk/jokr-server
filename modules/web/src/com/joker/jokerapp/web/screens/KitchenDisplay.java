package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.joker.jokerapp.entity.OrderLine;
import com.joker.jokerapp.entity.PrinterGroup;
import com.joker.jokerapp.entity.Ticket;
import com.joker.jokerapp.entity.TicketStatus;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitchenDisplay extends AbstractWindow {

    @Named("kitchenDisplayMainBox")
    private ScrollBoxLayout kitchenDisplayMainBox;

    @Named("showBarTicketsBtn")
    private Button showBarTicketsBtn;

    @Named("showFryerTicketsBtn")
    private Button showFryerTicketsBtn;

    @Named("showGrillTicketsBtn")
    private Button showGrillTicketsBtn;

    @Named("checkAllBtn")
    private Button checkAllBtn;

    @Inject
    private DataManager dataManager;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private CollectionDatasource<Ticket, UUID> ticketsDs;

    private Boolean showBarTickets = true;
    private Boolean showFryerTickets = true;
    private Boolean showGrillTickets = true;

    private Boolean checkAll = false;

    private ArrayList <Ticket> localTicketList = new ArrayList<>();

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        ticketsDs.refresh();

        for (Ticket ticket: ticketsDs.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) {

            localTicketList.add(ticket);

        }

        drawTickets(null, null);

        Timer clockTimer = componentsFactory.createTimer();
        addTimer(clockTimer);
        clockTimer.setDelay(10000);
        clockTimer.setRepeating(true);
        clockTimer.addActionListener(timer -> refreshData());

        clockTimer.start();

    }

    public void onBarBtnClick() {

        if (showBarTickets) {

            showBarTicketsBtn.setStyleName("kitchenDisplayBtn");
            //doNotPrintBtn.setCaption("STAMPA LE<br>COMANDE");
            showBarTickets = false;

        } else {

            showBarTicketsBtn.setStyleName("kitchenDisplayBtnPressed");
            //doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");
            showBarTickets = true;
        }

        kitchenDisplayMainBox.removeAll();

        drawTickets(null, null);

    }

    public void onFryerBtnClick() {

        if (showFryerTickets) {

            showFryerTicketsBtn.setStyleName("kitchenDisplayBtn");
            //doNotPrintBtn.setCaption("STAMPA LE<br>COMANDE");
            showFryerTickets = false;

        } else {

            showFryerTicketsBtn.setStyleName("kitchenDisplayBtnPressed");
            //doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");
            showFryerTickets = true;
        }

        kitchenDisplayMainBox.removeAll();

        drawTickets(null, null);

    }

    public void onGrillBtnClick() {

        if (showGrillTickets) {

            showGrillTicketsBtn.setStyleName("kitchenDisplayBtn");
            //doNotPrintBtn.setCaption("STAMPA LE<br>COMANDE");
            showGrillTickets = false;

        } else {

            showGrillTicketsBtn.setStyleName("kitchenDisplayBtnPressed");
            //doNotPrintBtn.setCaption("NON STAMPARE<br>LE COMANDE");
            showGrillTickets = true;

        }

        kitchenDisplayMainBox.removeAll();

        drawTickets(null, null);

    }

    private void refreshData() {

        ticketsDs.refresh();

        for (Iterator<Ticket> ticketIterator = localTicketList.iterator(); ticketIterator.hasNext();) {

            Ticket ticket = ticketIterator.next();

            if (!ticketsDs.containsItem(ticket.getUuid())) {

                drawTickets(ticket, "remove");
                ticketIterator.remove();

            }

        }

        for (Ticket ticket: ticketsDs.getItems()) if (!localTicketList.contains(ticket) && ticket.getTicketStatus().equals(TicketStatus.sended)) {

            localTicketList.add(ticket);
            drawTickets(ticket, "add");

        } else drawTickets(ticket, "modify");

    }

    private void drawTickets(Ticket ticketToProcess, String operation) {

        if (ticketToProcess != null) {

            if (operation.equals("add")) {

                GroupBoxLayout ticketGroupBox = componentsFactory.createComponent(GroupBoxLayout.class);

                ticketGroupBox.setId("ticketGroupBox".concat(ticketToProcess.getId().toString()));
                ticketGroupBox.setHeightFull();
                ticketGroupBox.setWidth("628px");
                ticketGroupBox.setOuterMargin(false,true,true,false);

                SplitPanel ticketHorizontalSplitPanel = componentsFactory.createComponent(SplitPanel.class);
                ticketHorizontalSplitPanel.setId("ticketHorizontalSplitPanel".concat(ticketToProcess.getId().toString()));
                ticketHorizontalSplitPanel.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
                ticketHorizontalSplitPanel.setSplitPosition(15, SizeUnit.PERCENTAGE);
                ticketHorizontalSplitPanel.setMaxSplitPosition(15, SizeUnit.PERCENTAGE);
                ticketHorizontalSplitPanel.setMinSplitPosition(15, SizeUnit.PERCENTAGE);
                ticketHorizontalSplitPanel.setHeightFull();
                ticketHorizontalSplitPanel.setWidthFull();

                ticketGroupBox.add(ticketHorizontalSplitPanel);

                VBoxLayout headerBoxLayout = componentsFactory.createComponent(VBoxLayout.class);

                headerBoxLayout.setId("headerBoxLayout".concat(ticketToProcess.getOrder().getId().toString()));

                headerBoxLayout.setHeightFull();
                headerBoxLayout.setWidthFull();
                headerBoxLayout.setAlignment(Alignment.TOP_CENTER);

                Button tableName = componentsFactory.createComponent(Button.class);

                tableName.setWidthFull();
                tableName.setHeight("40px");
                tableName.setId("tableName".concat(ticketToProcess.getId().toString()));
                tableName.setCaption("TAVOLO ".concat(ticketToProcess.getOrder().getTableItemCaption()).concat(" - TCKET ")
                        .concat(ticketToProcess.getTicketNumber().toString()).concat(" - ")
                        .concat(ticketToProcess.getOrder().getActualSeats().toString()).concat(" PAX - ")
                        .concat(ticketToProcess.getCreateTs().toString().substring(11,16)));

                tableName.setStyleName("tableNameBtn");

                tableName.setAction(new ticketAction());

                headerBoxLayout.add(tableName);

                HBoxLayout infoBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                infoBoxLayout.setWidthFull();
                infoBoxLayout.setHeight("40px");
                infoBoxLayout.setId("infoBoxLayout".concat(ticketToProcess.getId().toString()));

                headerBoxLayout.add(infoBoxLayout);

                ButtonsPanel buttonsPanel = componentsFactory.createComponent(ButtonsPanel.class);

                buttonsPanel.setWidthFull();
                buttonsPanel.setHeight("40px");
                buttonsPanel.setId("buttonsPanel".concat(ticketToProcess.getId().toString()));
                buttonsPanel.setAlignment(Alignment.TOP_RIGHT);

                infoBoxLayout.add(buttonsPanel);

                Button barticketStatus = componentsFactory.createComponent(Button.class);
                barticketStatus.setWidth("190px");
                barticketStatus.setHeight("40px");
                barticketStatus.setId("barticketStatus".concat(ticketToProcess.getId().toString()));
                barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                barticketStatus.setCaptionAsHtml(true);
                barticketStatus.setCaption("BAR");

                Button fryerticketStatus = componentsFactory.createComponent(Button.class);
                fryerticketStatus.setWidth("190px");
                fryerticketStatus.setHeight("40px");
                fryerticketStatus.setId("fryerticketStatus".concat(ticketToProcess.getId().toString()));
                fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                fryerticketStatus.setCaptionAsHtml(true);
                fryerticketStatus.setCaption("FRYER");

                Button grillticketStatus = componentsFactory.createComponent(Button.class);
                grillticketStatus.setWidth("190px");
                grillticketStatus.setHeight("40px");
                grillticketStatus.setId("grillticketStatus".concat(ticketToProcess.getId().toString()));
                grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                grillticketStatus.setCaptionAsHtml(true);
                grillticketStatus.setCaption("GRILL");

                buttonsPanel.add(barticketStatus); buttonsPanel.add(fryerticketStatus); buttonsPanel.add(grillticketStatus);

                ticketHorizontalSplitPanel.add(headerBoxLayout);

                ScrollBoxLayout ticketScrollBox = componentsFactory.createComponent(ScrollBoxLayout.class);
                ticketScrollBox.setId("ticketScrollBox".concat(ticketToProcess.getId().toString()));
                ticketScrollBox.setHeightFull();
                ticketScrollBox.setWidth("100%");

                ticketHorizontalSplitPanel.add(ticketScrollBox);

                ticketToProcess.getOrderLines().sort(Comparator.comparing(OrderLine::getPrinterGroup).thenComparing(OrderLine::getPosition));

                for (OrderLine orderLine: ticketToProcess.getOrderLines()) {

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) if (showBarTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) if (showFryerTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) if (showGrillTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                }

                if (ticketToProcess.getSubticketStatus().charAt(1) == 'n')
                { barticketStatus.setEnabled(false); barticketStatus.setCaption("NO BAR"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (ticketToProcess.getSubticketStatus().charAt(1) == 'o')
                { barticketStatus.setEnabled(true); barticketStatus.setCaption("BAR"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (ticketToProcess.getSubticketStatus().charAt(1) == 'c')
                { barticketStatus.setEnabled(true); barticketStatus.setCaption("BAR CHECKED"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (ticketToProcess.getSubticketStatus().charAt(4) == 'n')
                { fryerticketStatus.setEnabled(false); fryerticketStatus.setCaption("NO FRYER"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (ticketToProcess.getSubticketStatus().charAt(4) == 'o')
                { fryerticketStatus.setEnabled(true); fryerticketStatus.setCaption("FRYER"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (ticketToProcess.getSubticketStatus().charAt(4) == 'c')
                { fryerticketStatus.setEnabled(true); fryerticketStatus.setCaption("FRYER CHECKED"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (ticketToProcess.getSubticketStatus().charAt(7) == 'n')
                { grillticketStatus.setEnabled(false); grillticketStatus.setCaption("NO GRILL"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (ticketToProcess.getSubticketStatus().charAt(7) == 'o')
                { grillticketStatus.setEnabled(true); grillticketStatus.setCaption("GRILL"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (ticketToProcess.getSubticketStatus().charAt(7) == 'c')
                { grillticketStatus.setEnabled(true); grillticketStatus.setCaption("GRILL CHECKED"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (ticketScrollBox.getOwnComponents().size() > 0) kitchenDisplayMainBox.add(ticketGroupBox);

            } else if (operation.equals("modify")) {

                Button tableName = (Button) kitchenDisplayMainBox.getComponent("tableName".concat(ticketToProcess.getId().toString()));

                if (tableName == null) return;

                Button barticketStatus = (Button) kitchenDisplayMainBox.getComponent("barticketStatus".concat(ticketToProcess.getId().toString()));
                Button fryerticketStatus = (Button) kitchenDisplayMainBox.getComponent("fryerticketStatus".concat(ticketToProcess.getId().toString()));
                Button grillticketStatus = (Button) kitchenDisplayMainBox.getComponent("grillticketStatus".concat(ticketToProcess.getId().toString()));

                ScrollBoxLayout ticketScrollBox = (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(ticketToProcess.getId().toString()));
                
                tableName.setCaption("TAVOLO ".concat(ticketToProcess.getOrder().getTableItemCaption()).concat(" - TCKET ")
                        .concat(ticketToProcess.getTicketNumber().toString()).concat(" - ")
                        .concat(ticketToProcess.getOrder().getActualSeats().toString()).concat(" PAX - ")
                        .concat(ticketToProcess.getCreateTs().toString().substring(11,16)));

                tableName.setStyleName("tableNameBtn");

                ticketToProcess.getOrderLines().sort(Comparator.comparing(OrderLine::getPrinterGroup).thenComparing(OrderLine::getPosition));

                for (OrderLine orderLine: ticketToProcess.getOrderLines()) {

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) if (showBarTickets) {

                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) if (showFryerTickets) {

                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) if (showGrillTickets) {

                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                }

                if (ticketToProcess.getSubticketStatus().charAt(1) == 'n')
                { barticketStatus.setEnabled(false); barticketStatus.setCaption("NO BAR"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (ticketToProcess.getSubticketStatus().charAt(1) == 'o')
                { barticketStatus.setEnabled(true); barticketStatus.setCaption("BAR"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (ticketToProcess.getSubticketStatus().charAt(1) == 'c')
                { barticketStatus.setEnabled(true); barticketStatus.setCaption("BAR CHECKED"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (ticketToProcess.getSubticketStatus().charAt(4) == 'n')
                { fryerticketStatus.setEnabled(false); fryerticketStatus.setCaption("NO FRYER"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (ticketToProcess.getSubticketStatus().charAt(4) == 'o')
                { fryerticketStatus.setEnabled(true); fryerticketStatus.setCaption("FRYER"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (ticketToProcess.getSubticketStatus().charAt(4) == 'c')
                { fryerticketStatus.setEnabled(true); fryerticketStatus.setCaption("FRYER CHECKED"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (ticketToProcess.getSubticketStatus().charAt(7) == 'n')
                { grillticketStatus.setEnabled(false); grillticketStatus.setCaption("NO GRILL"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (ticketToProcess.getSubticketStatus().charAt(7) == 'o')
                { grillticketStatus.setEnabled(true); grillticketStatus.setCaption("GRILL"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (ticketToProcess.getSubticketStatus().charAt(7) == 'c')
                { grillticketStatus.setEnabled(true); grillticketStatus.setCaption("GRILL CHECKED"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }


            } else if (operation.equals("remove")) {

                GroupBoxLayout ticketGroupBox = (GroupBoxLayout) kitchenDisplayMainBox.getComponent("ticketGroupBox".concat(ticketToProcess.getId().toString()));

                kitchenDisplayMainBox.remove(ticketGroupBox);

            }

        } else {

            for (int i = 0; i < localTicketList.size(); i++) {

                GroupBoxLayout ticketGroupBox = componentsFactory.createComponent(GroupBoxLayout.class);

                ticketGroupBox.setId("ticketGroupBox".concat(localTicketList.get(i).getId().toString()));
                ticketGroupBox.setHeightFull();
                ticketGroupBox.setWidth("628px");
                ticketGroupBox.setOuterMargin(false,true,true,false);

                SplitPanel ticketHorizontalSplitPanel = componentsFactory.createComponent(SplitPanel.class);
                ticketHorizontalSplitPanel.setId("ticketHorizontalSplitPanel".concat(localTicketList.get(i).getId().toString()));
                ticketHorizontalSplitPanel.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
                ticketHorizontalSplitPanel.setSplitPosition(15, SizeUnit.PERCENTAGE);
                ticketHorizontalSplitPanel.setMaxSplitPosition(15, SizeUnit.PERCENTAGE);
                ticketHorizontalSplitPanel.setMinSplitPosition(15, SizeUnit.PERCENTAGE);
                ticketHorizontalSplitPanel.setHeightFull();
                ticketHorizontalSplitPanel.setWidthFull();

                ticketGroupBox.add(ticketHorizontalSplitPanel);

                VBoxLayout headerBoxLayout = componentsFactory.createComponent(VBoxLayout.class);

                headerBoxLayout.setId("headerBoxLayout".concat(localTicketList.get(i).getOrder().getId().toString()));

                headerBoxLayout.setHeightFull();
                headerBoxLayout.setWidthFull();
                headerBoxLayout.setAlignment(Alignment.TOP_CENTER);

                Button tableName = componentsFactory.createComponent(Button.class);

                tableName.setWidthFull();
                tableName.setHeight("40px");
                tableName.setId("tableName".concat(localTicketList.get(i).getId().toString()));
                tableName.setCaption("TAVOLO ".concat(localTicketList.get(i).getOrder().getTableItemCaption()).concat(" - TCKET ")
                        .concat(localTicketList.get(i).getTicketNumber().toString()).concat(" - ")
                        .concat(localTicketList.get(i).getOrder().getActualSeats().toString()).concat(" PAX - ")
                        .concat(localTicketList.get(i).getCreateTs().toString().substring(11,16)));

                tableName.setStyleName("tableNameBtn");

                tableName.setAction(new ticketAction());

                headerBoxLayout.add(tableName);

                HBoxLayout infoBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                infoBoxLayout.setWidthFull();
                infoBoxLayout.setHeight("40px");
                infoBoxLayout.setId("infoBoxLayout".concat(localTicketList.get(i).getId().toString()));

                headerBoxLayout.add(infoBoxLayout);

                ButtonsPanel buttonsPanel = componentsFactory.createComponent(ButtonsPanel.class);

                buttonsPanel.setWidthFull();
                buttonsPanel.setHeight("40px");
                buttonsPanel.setId("buttonsPanel".concat(localTicketList.get(i).getId().toString()));
                buttonsPanel.setAlignment(Alignment.TOP_RIGHT);

                infoBoxLayout.add(buttonsPanel);

                Button barticketStatus = componentsFactory.createComponent(Button.class);
                barticketStatus.setWidth("190px");
                barticketStatus.setHeight("40px");
                barticketStatus.setId("barticketStatus".concat(localTicketList.get(i).getId().toString()));
                barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                barticketStatus.setCaptionAsHtml(true);
                barticketStatus.setCaption("BAR");

                Button fryerticketStatus = componentsFactory.createComponent(Button.class);
                fryerticketStatus.setWidth("190px");
                fryerticketStatus.setHeight("40px");
                fryerticketStatus.setId("fryerticketStatus".concat(localTicketList.get(i).getId().toString()));
                fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                fryerticketStatus.setCaptionAsHtml(true);
                fryerticketStatus.setCaption("FRYER");

                Button grillticketStatus = componentsFactory.createComponent(Button.class);
                grillticketStatus.setWidth("190px");
                grillticketStatus.setHeight("40px");
                grillticketStatus.setId("grillticketStatus".concat(localTicketList.get(i).getId().toString()));
                grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                grillticketStatus.setCaptionAsHtml(true);
                grillticketStatus.setCaption("GRILL");

                buttonsPanel.add(barticketStatus); buttonsPanel.add(fryerticketStatus); buttonsPanel.add(grillticketStatus);

                ticketHorizontalSplitPanel.add(headerBoxLayout);

                ScrollBoxLayout ticketScrollBox = componentsFactory.createComponent(ScrollBoxLayout.class);
                ticketScrollBox.setId("ticketScrollBox".concat(localTicketList.get(i).getId().toString()));
                ticketScrollBox.setHeightFull();
                ticketScrollBox.setWidth("100%");

                ticketHorizontalSplitPanel.add(ticketScrollBox);

                localTicketList.get(i).getOrderLines().sort(Comparator.comparing(OrderLine::getPrinterGroup).thenComparing(OrderLine::getPosition));

                for (OrderLine orderLine: localTicketList.get(i).getOrderLines()) {

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) if (showBarTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) if (showFryerTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) if (showGrillTickets) {

                        ticketScrollBox.add(createOrderLineHBox(orderLine));
                        setOrderLineStyle(orderLine, ticketScrollBox);

                    }

                }

                if (localTicketList.get(i).getSubticketStatus().charAt(1) == 'n')
                    { barticketStatus.setEnabled(false); barticketStatus.setCaption("NO BAR"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (localTicketList.get(i).getSubticketStatus().charAt(1) == 'o')
                    { barticketStatus.setEnabled(true); barticketStatus.setCaption("BAR"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (localTicketList.get(i).getSubticketStatus().charAt(1) == 'c')
                    { barticketStatus.setEnabled(true); barticketStatus.setCaption("BAR CHECKED"); barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (localTicketList.get(i).getSubticketStatus().charAt(4) == 'n')
                { fryerticketStatus.setEnabled(false); fryerticketStatus.setCaption("NO FRYER"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (localTicketList.get(i).getSubticketStatus().charAt(4) == 'o')
                { fryerticketStatus.setEnabled(true); fryerticketStatus.setCaption("FRYER"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (localTicketList.get(i).getSubticketStatus().charAt(4) == 'c')
                { fryerticketStatus.setEnabled(true); fryerticketStatus.setCaption("FRYER CHECKED"); fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (localTicketList.get(i).getSubticketStatus().charAt(7) == 'n')
                { grillticketStatus.setEnabled(false); grillticketStatus.setCaption("NO GRILL"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                else if (localTicketList.get(i).getSubticketStatus().charAt(7) == 'o')
                { grillticketStatus.setEnabled(true); grillticketStatus.setCaption("GRILL"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }
                else if (localTicketList.get(i).getSubticketStatus().charAt(7) == 'c')
                { grillticketStatus.setEnabled(true); grillticketStatus.setCaption("GRILL CHECKED"); grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn"); }

                if (ticketScrollBox.getOwnComponents().size() > 0) kitchenDisplayMainBox.add(ticketGroupBox);

            }

        }

    }

    private HBoxLayout createOrderLineHBox(OrderLine orderLine) {

        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

        hBoxLayout.setId("hBoxLayout".concat(orderLine.getId().toString()));

        Label quantity = componentsFactory.createComponent(Label.class);
        Button itemName = componentsFactory.createComponent(Button.class);

        quantity.setWidth("10px");
        itemName.setWidth("472px");

        quantity.setHeight("40px");
        itemName.setHeight("40px");

        quantity.setId("quantity".concat(orderLine.getId().toString()));
        itemName.setId("itemName".concat(orderLine.getId().toString()));

        quantity.setAlignment(Alignment.MIDDLE_LEFT);

        if (orderLine.getItemName().length() < 50) itemName.setCaption(orderLine.getItemName());
        else itemName.setCaption(orderLine.getItemName().substring(0, 27).concat("...").concat(orderLine.getItemName().substring(orderLine.getItemName().length() - 20)));

        hBoxLayout.add(quantity);
        hBoxLayout.add(itemName);

        if (!orderLine.getIsModifier()) {

            Button check = componentsFactory.createComponent(Button.class);
            check.setWidth("86px");
            check.setHeight("36px");
            check.setId("check".concat(orderLine.getId().toString()));
            check.setAlignment(Alignment.MIDDLE_RIGHT);
            check.setCaption("CHECK");
            check.setAction(new CheckLine());
            hBoxLayout.add(check);

            quantity.setValue(orderLine.getQuantity());

        }

        return hBoxLayout;

    }

    private void setOrderLineStyle(OrderLine orderLine, ScrollBoxLayout scrollBox) {

        if (scrollBox.getOwnComponent("hBoxLayout".concat(orderLine.getId().toString())) != null) {

            Label quantity = (Label) scrollBox.getComponent("quantity".concat(orderLine.getId().toString()));
            Button itemName = (Button) scrollBox.getComponent("itemName".concat(orderLine.getId().toString()));
            Button check = (Button) scrollBox.getComponent("check".concat(orderLine.getId().toString()));

            if (orderLine.getTicket().getTicketStatus().equals(TicketStatus.sended)) {

                if (orderLine.getIsModifier()) {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-isReversed-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-isReversed-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-isReversed-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-isReversed-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-isReversed-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-isReversed-grill");

                        }

                    } else {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-grill");

                        }

                    }

                } else {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-isReversed-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-isReversed-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-isReversed-grill");

                        }

                    } else {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-grill");

                        }

                    }

                }

            } else {

                if (orderLine.getIsModifier()) {

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-bar");
                        itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-bar");

                    } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-fryer");
                        itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-fryer");

                    } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-grill");
                        itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-grill");

                    }

                } else {

                    if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-bar");
                        itemName.setStyleName("kitchenDisplayGridItem-button-bar");

                    } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-fryer");
                        itemName.setStyleName("kitchenDisplayGridItem-button-fryer");

                    } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-grill");
                        itemName.setStyleName("kitchenDisplayGridItem-button-grill");

                    }

                }

            }

            if (orderLine.getChecked()) {

                check.setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

            } else {

                check.setStyleName("kitchenDisplayGridItem-checkBtn");

            }

        }

    }

    public class AudioPlayer implements LineListener {

        boolean playCompleted;

        void play (String audioFilePath) {

            File audioFile = new File(audioFilePath);

            try {

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

                AudioFormat format = audioStream.getFormat();

                DataLine.Info info = new DataLine.Info(Clip.class, format);

                Clip audioClip = (Clip) AudioSystem.getLine(info);

                audioClip.addLineListener(this);

                audioClip.open(audioStream);

                audioClip.start();

                while (!playCompleted) {

                    try {

                        Thread.sleep(1000);

                    } catch (InterruptedException ex) {

                        ex.printStackTrace();

                    }

                }

                audioClip.close();

            } catch (UnsupportedAudioFileException ex) {

                System.out.println("The specified audio file is not supported.");
                ex.printStackTrace();

            } catch (LineUnavailableException ex) {

                System.out.println("Audio line for playing back is unavailable.");
                ex.printStackTrace();

            } catch (IOException ex) {

                System.out.println("Error playing the audio file.");
                ex.printStackTrace();

            }

        }

        @Override
        public void update(LineEvent event) {

            LineEvent.Type type = event.getType();

            if (type == LineEvent.Type.START) {

                System.out.println("Playback started.");

            } else if (type == LineEvent.Type.STOP) {

                playCompleted = true;
                System.out.println("Playback completed.");

            }

        }

    }

    private class CheckLine extends BaseAction {

        public CheckLine() {

            super("CheckLine");

        }

        @Override
        public boolean isPrimary() {

            return true;

        }

        @Override
        public void actionPerform(Component component) {

            Button checkBtn = (Button) component;

            UUID orderLineToCheck = UUID.fromString(checkBtn.getId().substring(5));
            Ticket orderLineToCheckTicket = ticketsDs.getItem(UUID.fromString(checkBtn.getParent().getParent().getId().substring(15)));

            Boolean isLineChecked = null;
            String linePrinterGroup = null;

            for (OrderLine orderLine: orderLineToCheckTicket.getOrderLines()) {

                if (orderLine.getId().equals(orderLineToCheck)) {

                    if (orderLine.getChecked()) {

                        orderLine.setChecked(false);
                        checkBtn.setStyleName("kitchenDisplayGridItem-checkBtn");

                    } else {

                        orderLine.setChecked(true);
                        checkBtn.setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

                    }

                    isLineChecked = orderLine.getChecked();
                    linePrinterGroup = orderLine.getPrinterGroup().toString();

                    dataManager.commit(orderLine);

                    ticketsDs.refresh();

                    localTicketList.clear();

                    for (Ticket ticket:ticketsDs.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) {

                        localTicketList.add(ticket);

                    }

                    break;

                }

            }

            for (OrderLine orderLine: orderLineToCheckTicket.getOrderLines()) if (!orderLine.getIsModifier() && orderLine.getPrinterGroup().toString().equals(linePrinterGroup))
                if ( (isLineChecked && !orderLine.getChecked()) || (!isLineChecked && orderLine.getChecked()) ) {

                    if (linePrinterGroup.equals(PrinterGroup.Bar.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("bc", "bo"));
                    else if (linePrinterGroup.equals(PrinterGroup.Fryer.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("fc", "fo"));
                    else if (linePrinterGroup.equals(PrinterGroup.Grill.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("gc", "go"));

                    dataManager.commit(orderLineToCheckTicket);
                    ticketsDs.refresh();
                    drawTickets(orderLineToCheckTicket, "modify");

                    return;

                }

            if (isLineChecked) {

                if (linePrinterGroup.equals(PrinterGroup.Bar.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("bo", "bc"));
                else if (linePrinterGroup.equals(PrinterGroup.Fryer.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("fo", "fc"));
                else if (linePrinterGroup.equals(PrinterGroup.Grill.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("go", "gc"));

            } else {

                if (linePrinterGroup.equals(PrinterGroup.Bar.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("bc", "bo"));
                else if (linePrinterGroup.equals(PrinterGroup.Fryer.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("fc", "fo"));
                else if (linePrinterGroup.equals(PrinterGroup.Grill.toString())) orderLineToCheckTicket.setSubticketStatus(orderLineToCheckTicket.getSubticketStatus().replace("gc", "go"));

            }

            dataManager.commit(orderLineToCheckTicket);
            ticketsDs.refresh();
            drawTickets(orderLineToCheckTicket, "modify");

        }

    }

    private class ticketAction extends BaseAction {

        public ticketAction() {

            super("ticketAction");

        }

        @Override
        public boolean isPrimary() {

            return true;

        }

        @Override
        public void actionPerform(Component component) {

            Button tableName = (Button) component;

            if (checkAll) {

                Ticket orderLineToBumpTicket = ticketsDs.getItem(UUID.fromString(tableName.getParent().getParent().getId().substring(26)));

                ScrollBoxLayout scrollBoxLayout = (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(orderLineToBumpTicket.getId().toString()));

                for (OrderLine orderLine: orderLineToBumpTicket.getOrderLines()) if (!orderLine.getChecked() && !orderLine.getIsModifier())
                    for (Component hBoxLayout: scrollBoxLayout.getOwnComponents()) if (hBoxLayout.getId().equals("hBoxLayout".concat(orderLine.getId().toString()))) {

                        orderLine.setChecked(true);
                        dataManager.commit(orderLine);
                        scrollBoxLayout.getComponent("check".concat(orderLine.getId().toString())).setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

                    }

                if (showBarTickets) if (orderLineToBumpTicket.getSubticketStatus().charAt(1) == 'o')
                    orderLineToBumpTicket.setSubticketStatus(orderLineToBumpTicket.getSubticketStatus().replace("bo", "bc" ));

                if (showFryerTickets) if (orderLineToBumpTicket.getSubticketStatus().charAt(4) == 'o')
                    orderLineToBumpTicket.setSubticketStatus(orderLineToBumpTicket.getSubticketStatus().replace("fo", "fc" ));

                if (showGrillTickets) if (orderLineToBumpTicket.getSubticketStatus().charAt(7) == 'o')
                    orderLineToBumpTicket.setSubticketStatus(orderLineToBumpTicket.getSubticketStatus().replace("go", "gc" ));

                dataManager.commit(orderLineToBumpTicket);
                ticketsDs.refresh();
                localTicketList.clear();

                for (Ticket ticket:ticketsDs.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) localTicketList.add(ticket);

                checkAllBtn.setStyleName("kitchenDisplayBtn");
                checkAll = false;

                drawTickets(orderLineToBumpTicket, "modify");

            }

        }
        
    }

    public void onCheckAllClick() {

        if (checkAll) {

            checkAllBtn.setStyleName("kitchenDisplayBtn");
            checkAll = false;

        } else {

            checkAllBtn.setStyleName("kitchenDisplayBtnPressed");
            checkAll = true;
            
        }

    }

}

//String audioFilePath = "E:/Test/Audio.wav";
//AudioPlayer player = new AudioPlayer();
//player.play(audioFilePath);