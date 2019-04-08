package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.joker.jokerapp.entity.OrderLine;
import com.joker.jokerapp.entity.Ticket;
import com.joker.jokerapp.entity.TicketStatus;
import com.sun.org.apache.xpath.internal.operations.Bool;

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

        //currentTimeField.setValue(Date.from(Instant.now()));

        ticketsDs.refresh();

        for (Ticket ticket: ticketsDs.getItems()) if (!localTicketList.contains(ticket) && ticket.getTicketStatus().equals(TicketStatus.sended)) {

            localTicketList.add(ticket);
            drawTickets(ticket, "add");

        }

        Iterator<Ticket> ticketIterator = localTicketList.iterator();

        while (ticketIterator.hasNext()) {

            Ticket ticket = ticketIterator.next();

            if (!ticketsDs.containsItem(ticket.getUuid())) {

                localTicketList.remove(ticket);
                drawTickets(ticketIterator.next(), "remove");

            }

        }

    }

    private void drawTickets(Ticket ticketToProcess, String operation) {

        if (ticketToProcess != null) {

            if (operation.equals("add")) {

                ScrollBoxLayout ticketScrollBox = componentsFactory.createComponent(ScrollBoxLayout.class);
                ticketScrollBox.setId("ticketScrollBox".concat(ticketToProcess.getId().toString()));
                ticketScrollBox.setHeightFull();
                ticketScrollBox.setWidth("500px");

                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

                hBoxLayout.setId("hBoxLayout".concat(ticketToProcess.getOrder().getId().toString()));

                Button tableName = componentsFactory.createComponent(Button.class);

                tableName.setWidth("475px");

                tableName.setHeight("40px");

                tableName.setId("tableName".concat(ticketToProcess.getOrder().getId().toString()));

                tableName.setCaption("TAVOLO ".concat(ticketToProcess.getOrder().getTableItemCaption()).concat("   ").concat(ticketToProcess.getCreateTs().toString()));

                hBoxLayout.add(tableName);

                ticketScrollBox.add(hBoxLayout);

                for (OrderLine orderLine: ticketToProcess.getOrderLines()) {

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

                kitchenDisplayMainBox.add(ticketScrollBox);

            } else if (operation.equals("modify")) {

                ScrollBoxLayout ticketScrollBox = (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(ticketToProcess.getOrder().getId().toString()));

                HBoxLayout hBoxLayout = (HBoxLayout) ticketScrollBox.getComponent("hBoxLayout".concat(ticketToProcess.getOrder().getId().toString()));

                for (OrderLine orderLine: ticketToProcess.getOrderLines()) {

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

                //String audioFilePath = "E:/Test/Audio.wav";
                //AudioPlayer player = new AudioPlayer();
                //player.play(audioFilePath);

            } else if (operation.equals("remove")) {

                ScrollBoxLayout ticketScrollBox = (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(ticketToProcess.getOrder().getId().toString()));

                kitchenDisplayMainBox.remove(ticketScrollBox);

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
                ticketHorizontalSplitPanel.setSplitPosition(15, Component.UNITS_PERCENTAGE);
                ticketHorizontalSplitPanel.setMaxSplitPosition(15, Component.UNITS_PERCENTAGE);
                ticketHorizontalSplitPanel.setMinSplitPosition(15, Component.UNITS_PERCENTAGE);
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
                tableName.setId("infoBoxLayout".concat(localTicketList.get(i).getId().toString()));

                headerBoxLayout.add(infoBoxLayout);

                ButtonsPanel buttonsPanel = componentsFactory.createComponent(ButtonsPanel.class);

                buttonsPanel.setWidthFull();
                buttonsPanel.setHeight("40px");
                buttonsPanel.setId("buttonsPanel".concat(localTicketList.get(i).getId().toString()));
                buttonsPanel.setAlignment(Alignment.TOP_RIGHT);

                infoBoxLayout.add(buttonsPanel);

                Button barTiketStatus = componentsFactory.createComponent(Button.class);
                barTiketStatus.setWidth("190px");
                barTiketStatus.setHeight("40px");
                barTiketStatus.setId("barTiketStatus".concat(localTicketList.get(i).getId().toString()));
                barTiketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                barTiketStatus.setCaptionAsHtml(true);
                barTiketStatus.setCaption("BAR");

                Button fryerTiketStatus = componentsFactory.createComponent(Button.class);
                fryerTiketStatus.setWidth("190px");
                fryerTiketStatus.setHeight("40px");
                fryerTiketStatus.setId("fryerTiketStatus".concat(localTicketList.get(i).getId().toString()));
                fryerTiketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                fryerTiketStatus.setCaptionAsHtml(true);
                fryerTiketStatus.setCaption("FRYER");

                Button grillTiketStatus = componentsFactory.createComponent(Button.class);
                grillTiketStatus.setWidth("190px");
                grillTiketStatus.setHeight("40px");
                grillTiketStatus.setId("grillTiketStatus".concat(localTicketList.get(i).getId().toString()));
                grillTiketStatus.setStyleName("kitchenDisplayGridItem-checkBtn");
                grillTiketStatus.setCaptionAsHtml(true);
                grillTiketStatus.setCaption("GRILL");

                buttonsPanel.add(barTiketStatus); buttonsPanel.add(fryerTiketStatus); buttonsPanel.add(grillTiketStatus);

                ticketHorizontalSplitPanel.add(headerBoxLayout);

                ScrollBoxLayout ticketScrollBox = componentsFactory.createComponent(ScrollBoxLayout.class);
                ticketScrollBox.setId("ticketScrollBox".concat(localTicketList.get(i).getId().toString()));
                ticketScrollBox.setHeightFull();
                ticketScrollBox.setWidth("100%");

                ticketHorizontalSplitPanel.add(ticketScrollBox);

                localTicketList.get(i).getOrderLines().sort(Comparator.comparing(OrderLine::getPrinterGroup).thenComparing(OrderLine::getPosition));

                Boolean noBar = true;
                Boolean noFryer = true;
                Boolean noGrill = true;

                for (OrderLine orderLine: localTicketList.get(i).getOrderLines()) {

                    if (orderLine.getPrinterGroup().equals("Bar")) {

                        if (showBarTickets) {

                            ticketScrollBox.add(createOrderLineHBox(orderLine));
                            setOrderLineStyle(orderLine, ticketScrollBox);

                        }

                        if (noBar) noBar = false;

                    }

                    if (orderLine.getPrinterGroup().equals("Fryer")) {

                        if (showFryerTickets) {

                            ticketScrollBox.add(createOrderLineHBox(orderLine));
                            setOrderLineStyle(orderLine, ticketScrollBox);

                        }

                        if (noFryer) noFryer = false;

                    }

                    if (orderLine.getPrinterGroup().equals("Grill")) {

                        if (showGrillTickets) {

                            ticketScrollBox.add(createOrderLineHBox(orderLine));
                            setOrderLineStyle(orderLine, ticketScrollBox);

                        }

                        if (noGrill) noGrill = false;

                    }

                }

                if (noBar) { barTiketStatus.setCaption("NO BAR"); barTiketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                if (noFryer) { fryerTiketStatus.setCaption("NO FRYER"); fryerTiketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }
                if (noGrill) { grillTiketStatus.setCaption("NO GRILL"); grillTiketStatus.setStyleName("kitchenDisplayGridItem-checkBtn-pushed"); }

                if (ticketScrollBox.getOwnComponents().size() > 1) kitchenDisplayMainBox.add(ticketGroupBox);

            }

        }

    }

    private HBoxLayout createOrderLineHBox(OrderLine orderLine) {

        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

        hBoxLayout.setId("hBoxLayout".concat(orderLine.getId().toString()));

        Label quantity = componentsFactory.createComponent(Label.class);
        Button itemName = componentsFactory.createComponent(Button.class);

        quantity.setWidth("20px");
        itemName.setWidth("465px");

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

            if (orderLine.getIsdone()) {

                check.setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

            } else {

                check.setStyleName("kitchenDisplayGridItem-checkBtn");

            }

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

            if (orderLine.getTicket().getTicketStatus().equals(TicketStatus.sended)) {

                if (orderLine.getIsModifier()) {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getPrinterGroup().equals("Bar")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-isReversed-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-isReversed-bar");

                        } else if (orderLine.getPrinterGroup().equals("Fryer")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-isReversed-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-isReversed-fryer");

                        } else if (orderLine.getPrinterGroup().equals("Grill")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-isReversed-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-isReversed-grill");

                        }

                    } else {

                        if (orderLine.getPrinterGroup().equals("Bar")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-bar");

                        } else if (orderLine.getPrinterGroup().equals("Fryer")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-fryer");

                        } else if (orderLine.getPrinterGroup().equals("Grill")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-isSended-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-isSended-grill");

                        }

                    }

                } else {

                    if (orderLine.getIsReversed()) {

                        if (orderLine.getPrinterGroup().equals("Bar")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-isReversed-bar");

                        } else if (orderLine.getPrinterGroup().equals("Fryer")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-isReversed-fryer");

                        } else if (orderLine.getPrinterGroup().equals("Grill")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-isReversed-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-isReversed-grill");

                        }

                    } else {

                        if (orderLine.getPrinterGroup().equals("Bar")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-bar");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-bar");

                        } else if (orderLine.getPrinterGroup().equals("Fryer")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-fryer");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-fryer");

                        } else if (orderLine.getPrinterGroup().equals("Grill")) {

                            quantity.setStyleName("kitchenDisplayGridItem-label-isSended-grill");
                            itemName.setStyleName("kitchenDisplayGridItem-button-isSended-grill");

                        }

                    }

                }

            } else {

                if (orderLine.getIsModifier()) {

                    if (orderLine.getPrinterGroup().equals("Bar")) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-bar");
                        itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-bar");

                    } else if (orderLine.getPrinterGroup().equals("Fryer")) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-fryer");
                        itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-fryer");

                    } else if (orderLine.getPrinterGroup().equals("Grill")) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-isModifier-grill");
                        itemName.setStyleName("kitchenDisplayGridItem-button-isModifier-grill");

                    }

                } else {

                    if (orderLine.getPrinterGroup().equals("Bar")) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-bar");
                        itemName.setStyleName("kitchenDisplayGridItem-button-bar");

                    } else if (orderLine.getPrinterGroup().equals("Fryer")) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-fryer");
                        itemName.setStyleName("kitchenDisplayGridItem-button-fryer");

                    } else if (orderLine.getPrinterGroup().equals("Grill")) {

                        quantity.setStyleName("kitchenDisplayGridItem-label-grill");
                        itemName.setStyleName("kitchenDisplayGridItem-button-grill");

                    }

                }

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

            for (OrderLine orderLine: orderLineToCheckTicket.getOrderLines()) if (orderLine.getId().equals(orderLineToCheck)) {

                if (orderLine.getHasModifier()) for (OrderLine line: orderLineToCheckTicket.getOrderLines()) if (line.getIsModifier() && line.getItemToModifyId().equals(orderLine.getId())) {

                    if (orderLine.getIsdone()) line.setIsdone(false);
                    else line.setIsdone(true);
                    dataManager.commit(line);

                }

                if (orderLine.getIsdone()) {

                    orderLine.setIsdone(false);
                    checkBtn.setStyleName("kitchenDisplayGridItem-checkBtn");

                } else {

                    orderLine.setIsdone(true);
                    checkBtn.setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

                }

                dataManager.commit(orderLine);

                ticketsDs.refresh();

                localTicketList.clear();

                for (Ticket ticket:ticketsDs.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) {

                    localTicketList.add(ticket);

                }

                return;

            }

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

            if (!checkAll) return;

            Ticket orderLineToBumpTicket = ticketsDs.getItem(UUID.fromString(tableName.getParent().getParent().getId().substring(26)));

            ScrollBoxLayout scrollBoxLayout = (ScrollBoxLayout) kitchenDisplayMainBox.getComponent("ticketScrollBox".concat(orderLineToBumpTicket.getId().toString()));

            for (OrderLine orderLine: orderLineToBumpTicket.getOrderLines()) {

                if (!orderLine.getIsdone() && !orderLine.getIsModifier()) {

                    for (Component hBoxLayout: scrollBoxLayout.getOwnComponents()) {

                        if (hBoxLayout.getId().equals("hBoxLayout".concat(orderLine.getId().toString()))) {

                            orderLine.setIsdone(true);
                            dataManager.commit(orderLine);
                            scrollBoxLayout.getComponent("check".concat(orderLine.getId().toString())).setStyleName("kitchenDisplayGridItem-checkBtn-pushed");

                        }

                    }

                }

            }

            ticketsDs.refresh();

            localTicketList.clear();

            for (Ticket ticket:ticketsDs.getItems()) if (ticket.getTicketStatus().equals(TicketStatus.sended)) {

                localTicketList.add(ticket);

            }

            checkAllBtn.setStyleName("kitchenDisplayBtn");
            checkAll = false;

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