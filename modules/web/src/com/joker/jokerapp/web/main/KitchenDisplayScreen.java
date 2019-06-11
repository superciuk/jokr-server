package com.joker.jokerapp.web.main;

import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.joker.jokerapp.entity.OrderLine;
import com.joker.jokerapp.entity.PrinterGroup;
import com.joker.jokerapp.entity.Ticket;
import com.joker.jokerapp.entity.TicketStatus;
import com.vaadin.ui.HorizontalSplitPanel;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Instant;
import java.util.*;

@UiController("jokerapp_KitchenDisplayScreen")
@UiDescriptor("kitchen-display-screen.xml")
public class KitchenDisplayScreen extends Screen {

    @Inject
    private CollectionContainer<Ticket> ticketsDc;

    @Inject
    private UiComponents uiComponents;

    @Inject
    private DataContext dataContext;

    @Named("kitchenDisplayMainBox")
    private ScrollBoxLayout kitchenDisplayMainBox;

    @Named("showBarTicketsBtn")
    private Button showBarTicketsBtn;

    @Named("showFryerTicketsBtn")
    private Button showFryerTicketsBtn;

    @Named("showGrillTicketsBtn")
    private Button showGrillTicketsBtn;

    @Named("openTicketsCounterBtn")
    private Button openTicketsCounterBtn;

    @Named("closedTicketsCounterBtn")
    private Button closedTicketsCounterBtn;

    @Named("closeTicketBtn")
    private Button closeTicketBtn;

    @Named("checkAllBtn")
    private Button checkAllBtn;

    private Boolean showBarTickets = true;
    private Boolean showFryerTickets = true;
    private Boolean showGrillTickets = true;

    private Boolean checkAll = false;

    private Boolean closeTicket = false;

    private int openTicketsCounter = 0;

    private int closedTicketsCounter = 0;

    private OrderLine selectedLine = null;

    private ArrayList<Ticket> localTicketList = new ArrayList<>();

    @Subscribe
    protected void onInit(InitEvent event) {

        getScreenData().loadAll();

        for (Ticket ticket: ticketsDc.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) {

            localTicketList.add(ticket);

            openTicketsCounter++;

        } else if (ticket.getTicketStatus().equals(TicketStatus.closed)) closedTicketsCounter++;

        openTicketsCounterBtn.setCaption("Open Tickets:<br>".concat(String.valueOf(openTicketsCounter)));
        closedTicketsCounterBtn.setCaption("Closed Tickets:<br>".concat(String.valueOf(closedTicketsCounter)));

        drawTickets(null, null);

    }

    @Subscribe ("showBarTicketsBtn")
    public void OnShowBarTicketsBtnClick(Button.ClickEvent event) {

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

    @Subscribe ("showFryerTicketsBtn")
    public void onShowFryerTicketsBtnClick(Button.ClickEvent event) {

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

    @Subscribe ("showGrillTicketsBtn")
    public void onShowGrillTicketsBtnClick(Button.ClickEvent event) {

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

    @Subscribe ("checkAllBtn")
    public void onCheckAllBtnClick(Button.ClickEvent event) {

        if (checkAll) {

            checkAllBtn.setStyleName("kitchenDisplayBtn");
            checkAll = false;


        } else {

            checkAllBtn.setStyleName("kitchenDisplayBtnPressed");
            checkAll = true;
            if (closeTicket) { closeTicketBtn.setStyleName("kitchenDisplayBtn"); closeTicket = false; }

        }

    }

    @Subscribe ("closeTicketBtn")
    public void onCloseTicketBtnClick(Button.ClickEvent event) {

        if (closeTicket) {

            closeTicketBtn.setStyleName("kitchenDisplayBtn");
            closeTicket = false;

        } else {

            closeTicketBtn.setStyleName("kitchenDisplayBtnPressed");
            closeTicket = true;
            if (checkAll) { checkAllBtn.setStyleName("kitchenDisplayBtn"); checkAll = false; }

        }
        
    }
    
    private void refreshData() {

        getScreenData().loadAll();

        for (Ticket ticket: localTicketList) if (!ticketsDc.containsItem(ticket.getUuid()) || ticket.getTicketStatus().equals(TicketStatus.closed)) drawTickets(ticket, "removed");

        for (Ticket ticket: ticketsDc.getItems()) if (!localTicketList.contains(ticket) && ticket.getTicketStatus().equals(TicketStatus.sended)) drawTickets(ticket, "added");

        localTicketList.clear();

        openTicketsCounter = 0;
        closedTicketsCounter = 0;

        for (Ticket tkt: ticketsDc.getItems()) if (tkt.getTicketStatus().equals(TicketStatus.sended)) {

            localTicketList.add(tkt);

            openTicketsCounter++;

            if (Instant.now().getEpochSecond() - tkt.getUpdateTs().toInstant().getEpochSecond() > 1) drawTickets(tkt, "modified");

        } else if (tkt.getTicketStatus().equals(TicketStatus.closed)) closedTicketsCounter++;

        openTicketsCounterBtn.setCaption("Open Tickets:<br>".concat(String.valueOf(openTicketsCounter)));
        closedTicketsCounterBtn.setCaption("Closed Tickets:<br>".concat(String.valueOf(closedTicketsCounter)));

    }

    private void drawTickets(Ticket ticketToProcess, String operation) {

        if (ticketToProcess != null) {

            if (operation.equals("added")) {

                GroupBoxLayout ticketGroupBox = createTicketGroupBox(ticketToProcess);

                if (ticketGroupBox != null ) kitchenDisplayMainBox.add(ticketGroupBox);

            } else if (operation.equals("modified")) {

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


            } else if (operation.equals("removed")) {

                GroupBoxLayout ticketGroupBox = (GroupBoxLayout) kitchenDisplayMainBox.getComponent("ticketGroupBox".concat(ticketToProcess.getId().toString()));

                kitchenDisplayMainBox.remove(ticketGroupBox);

            }

        } else {

            for (int i = 0; i < localTicketList.size(); i++) {

                GroupBoxLayout ticketGroupBox = createTicketGroupBox(localTicketList.get(i));

                if (ticketGroupBox != null ) kitchenDisplayMainBox.add(ticketGroupBox);

            }

        }

    }

    private GroupBoxLayout createTicketGroupBox(Ticket ticketToProcess) {

        GroupBoxLayout ticketGroupBox = uiComponents.create(GroupBoxLayout.class);

        ticketGroupBox.setId("ticketGroupBox".concat(ticketToProcess.getId().toString()));
        ticketGroupBox.setHeightFull();
        ticketGroupBox.setWidth("628px");
        ticketGroupBox.setOuterMargin(false,true,true,false);

        SplitPanel ticketHorizontalSplitPanel = uiComponents.create(SplitPanel.class);
        ticketHorizontalSplitPanel.setId("ticketHorizontalSplitPanel".concat(ticketToProcess.getId().toString()));
        ticketHorizontalSplitPanel.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        ticketHorizontalSplitPanel.setSplitPosition(15, SizeUnit.PERCENTAGE);
        ticketHorizontalSplitPanel.setMaxSplitPosition(15, SizeUnit.PERCENTAGE);
        ticketHorizontalSplitPanel.setMinSplitPosition(15, SizeUnit.PERCENTAGE);
        ticketHorizontalSplitPanel.setHeightFull();
        ticketHorizontalSplitPanel.setWidthFull();

        ticketGroupBox.add(ticketHorizontalSplitPanel);

        VBoxLayout headerBoxLayout = uiComponents.create(VBoxLayout.class);

        headerBoxLayout.setId("headerBoxLayout".concat(ticketToProcess.getOrder().getId().toString()));

        headerBoxLayout.setHeightFull();
        headerBoxLayout.setWidthFull();
        headerBoxLayout.setAlignment(Component.Alignment.TOP_CENTER);

        Button tableName = uiComponents.create(Button.class);

        tableName.setWidthFull();
        tableName.setHeight("40px");
        tableName.setId("tableName".concat(ticketToProcess.getId().toString()));
        tableName.setCaption("TAVOLO ".concat(ticketToProcess.getOrder().getTableItemCaption()).concat(" - TCKET ")
                .concat(ticketToProcess.getTicketNumber().toString()).concat(" - ")
                .concat(ticketToProcess.getOrder().getActualSeats().toString()).concat(" PAX - ")
                .concat(ticketToProcess.getCreateTs().toString().substring(11,16)));

        tableName.setStyleName("tableNameBtn");

        tableName.setAction(new KitchenDisplayScreen.ticketAction());

        headerBoxLayout.add(tableName);

        HBoxLayout infoBoxLayout = uiComponents.create(HBoxLayout.class);
        infoBoxLayout.setWidthFull();
        infoBoxLayout.setHeight("40px");
        infoBoxLayout.setId("infoBoxLayout".concat(ticketToProcess.getId().toString()));

        headerBoxLayout.add(infoBoxLayout);

        ButtonsPanel buttonsPanel = uiComponents.create(ButtonsPanel.class);

        buttonsPanel.setWidthFull();
        buttonsPanel.setHeight("40px");
        buttonsPanel.setId("buttonsPanel".concat(ticketToProcess.getId().toString()));
        buttonsPanel.setAlignment(Component.Alignment.TOP_RIGHT);

        infoBoxLayout.add(buttonsPanel);

        Button barticketStatus = uiComponents.create(Button.class);
        barticketStatus.setWidth("190px");
        barticketStatus.setHeight("40px");
        barticketStatus.setId("barticketStatus".concat(ticketToProcess.getId().toString()));
        barticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
        barticketStatus.setCaptionAsHtml(true);
        barticketStatus.setCaption("BAR");

        Button fryerticketStatus = uiComponents.create(Button.class);
        fryerticketStatus.setWidth("190px");
        fryerticketStatus.setHeight("40px");
        fryerticketStatus.setId("fryerticketStatus".concat(ticketToProcess.getId().toString()));
        fryerticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
        fryerticketStatus.setCaptionAsHtml(true);
        fryerticketStatus.setCaption("FRYER");

        Button grillticketStatus = uiComponents.create(Button.class);
        grillticketStatus.setWidth("190px");
        grillticketStatus.setHeight("40px");
        grillticketStatus.setId("grillticketStatus".concat(ticketToProcess.getId().toString()));
        grillticketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
        grillticketStatus.setCaptionAsHtml(true);
        grillticketStatus.setCaption("GRILL");

        buttonsPanel.add(barticketStatus); buttonsPanel.add(fryerticketStatus); buttonsPanel.add(grillticketStatus);

        ticketHorizontalSplitPanel.add(headerBoxLayout);

        ScrollBoxLayout ticketScrollBox = uiComponents.create(ScrollBoxLayout.class);
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

        if (ticketScrollBox.getOwnComponents().size() > 0) return ticketGroupBox;
        else return null;

    }

    private HBoxLayout createOrderLineHBox(OrderLine orderLine) {

        HBoxLayout hBoxLayout = uiComponents.create(HBoxLayout.class);

        hBoxLayout.setId("hBoxLayout".concat(orderLine.getId().toString()));

        Label quantity = uiComponents.create(Label.class);
        Button itemName = uiComponents.create(Button.class);

        quantity.setWidth("10px");
        itemName.setWidth("472px");

        quantity.setHeight("40px");
        itemName.setHeight("40px");

        quantity.setId("quantity".concat(orderLine.getId().toString()));
        itemName.setId("itemName".concat(orderLine.getId().toString()));

        itemName.setAction(new KitchenDisplayScreen.SelectCurrentLineAction());

        quantity.setAlignment(Component.Alignment.MIDDLE_LEFT);

        if (orderLine.getItemName().length() < 50) itemName.setCaption(orderLine.getItemName());
        else itemName.setCaption(orderLine.getItemName().substring(0, 27).concat("...").concat(orderLine.getItemName().substring(orderLine.getItemName().length() - 20)));

        hBoxLayout.add(quantity);
        hBoxLayout.add(itemName);

        if (!orderLine.getIsModifier()) {

            Button check = uiComponents.create(Button.class);
            check.setWidth("86px");
            check.setHeight("36px");
            check.setId("check".concat(orderLine.getId().toString()));
            check.setAlignment(Component.Alignment.MIDDLE_RIGHT);
            check.setCaption("CHECK");
            if (orderLine.getIsReversed()) check.setEnabled(false);
            else check.setAction(new KitchenDisplayScreen.CheckLine());
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

            if (orderLine == selectedLine) {

                if (orderLine.getIsModifier()) {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isModifier-isSended-isReversed-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isModifier-isSended-isReversed-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isModifier-isSended-isReversed-grill");

                        }

                    } else {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isModifier-isSended-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isModifier-isSended-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isModifier-isSended-grill");

                        }

                    }

                } else {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isSended-isReversed-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isSended-isReversed-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isSended-isReversed-grill");

                        }

                    } else {

                        if (orderLine.getPrinterGroup().equals(PrinterGroup.Bar)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isSended-bar");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Fryer)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isSended-fryer");

                        } else if (orderLine.getPrinterGroup().equals(PrinterGroup.Grill)) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-selected-isSended-grill");

                        }

                    }

                }

            } else {

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

            }

            if (!orderLine.getIsModifier()) if (orderLine.getChecked()) {

                check.setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

            } else {

                check.setStyleName("kitchenDisplayGridItem-checkBtn");

            }

        }

    }

    private class SelectCurrentLineAction extends BaseAction {

        public SelectCurrentLineAction() {

            super("SelectCurrentLine");

        }

        @Override
        public boolean isPrimary() {

            return true;

        }

        @Override
        public void actionPerform(Component component) {

            Button itemNameBtn = (Button) component;

            OrderLine newLineToSelect = dataContext.find(OrderLine.class, UUID.fromString(itemNameBtn.getId().substring(8)));

            if (newLineToSelect == selectedLine) {

                selectedLine = null;
                setOrderLineStyle(newLineToSelect, (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(newLineToSelect.getTicket().getId().toString())));

            } else {

                OrderLine newLineToDeselect = selectedLine;

                selectedLine = newLineToSelect;

                setOrderLineStyle(newLineToSelect, (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(newLineToSelect.getTicket().getId().toString())));
                if (newLineToDeselect != null) setOrderLineStyle(newLineToDeselect, (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(newLineToDeselect.getTicket().getId().toString())));

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
            Ticket orderLineToCheckTicket = ticketsDc.getItem(UUID.fromString(checkBtn.getParent().getParent().getId().substring(15)));

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

                    dataContext.commit();

                    localTicketList.clear();

                    for (Ticket ticket:ticketsDc.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) {

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

                    dataContext.commit();

                    drawTickets(orderLineToCheckTicket, "modified");

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

            dataContext.commit();

            drawTickets(orderLineToCheckTicket, "modified");

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

                Ticket ticketToBump = ticketsDc.getItem(UUID.fromString(tableName.getParent().getParent().getId().substring(26)));

                ScrollBoxLayout scrollBoxLayout = (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(ticketToBump.getId().toString()));

                for (OrderLine orderLine: ticketToBump.getOrderLines()) if (!orderLine.getIsModifier() && !orderLine.getChecked())
                    for (Component hBoxLayout: scrollBoxLayout.getOwnComponents()) if (hBoxLayout.getId().equals("hBoxLayout".concat(orderLine.getId().toString()))) {

                        orderLine.setChecked(true);

                        dataContext.commit();

                        scrollBoxLayout.getComponent("check".concat(orderLine.getId().toString())).setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

                    }

                if (showBarTickets) if (ticketToBump.getSubticketStatus().charAt(1) == 'o')
                    ticketToBump.setSubticketStatus(ticketToBump.getSubticketStatus().replace("bo", "bc" ));

                if (showFryerTickets) if (ticketToBump.getSubticketStatus().charAt(4) == 'o')
                    ticketToBump.setSubticketStatus(ticketToBump.getSubticketStatus().replace("fo", "fc" ));

                if (showGrillTickets) if (ticketToBump.getSubticketStatus().charAt(7) == 'o')
                    ticketToBump.setSubticketStatus(ticketToBump.getSubticketStatus().replace("go", "gc" ));

                dataContext.commit();

                localTicketList.clear();

                for (Ticket ticket:ticketsDc.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) localTicketList.add(ticket);

                checkAllBtn.setStyleName("kitchenDisplayBtn");
                checkAll = false;

                drawTickets(ticketToBump, "modified");
                
                return;

            }
            
            if (closeTicket) {

                Ticket ticketToClose = ticketsDc.getItem(UUID.fromString(tableName.getParent().getParent().getId().substring(26)));

                for (OrderLine orderLine: ticketToClose.getOrderLines()) if (!orderLine.getIsModifier() && !orderLine.getChecked()) return;

                kitchenDisplayMainBox.remove(kitchenDisplayMainBox.getComponent("ticketHorizontalSplitPanel".concat(ticketToClose.getId().toString())));

                ticketToClose.setTicketStatus(TicketStatus.closed);

                dataContext.commit();

                localTicketList.clear();

                for (Ticket ticket:ticketsDc.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) localTicketList.add(ticket);

                closeTicketBtn.setStyleName("kitchenDisplayBtn");
                closeTicket = false;

                drawTickets(ticketToClose, "removed");



            }

        }

    }

    public void onKitchenTimerClick(Timer source) {

        refreshData();

    }

}