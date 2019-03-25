package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.haulmont.cuba.web.gui.components.WebGroupBox;
import com.joker.jokerapp.entity.*;
import com.joker.jokerapp.web.dialogs.ActualSeatsDialog;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.util.*;

public class TableSelect extends AbstractWindow {

    @Inject
    private CollectionDatasource<TableItem, UUID> tableItemsDs;

    @Inject
    private DataManager dataManager;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("upperHBox")
    private HBoxLayout upperHBox;

    @Named("bottomHBox")
    private HBoxLayout bottomHBox;

    @Named("tableFirstGrid")
    private GridLayout tableBtnFirstGrid;

    @Named("tableSecondGrid")
    private GridLayout tableBtnSecondGrid;

    @Named("tableThirdGrid")
    private GridLayout tableBtnThirdGrid;

    @Named("currentTimeField")
    private TimeField currentTimeField;

    @Named("splitTableBtn")
    private Button splitTableBtn;

    @Named("mergeTableBtn")
    private Button mergeTableBtn;

    @Named("moveTableBtn")
    private Button moveTableBtn;

    @Named("billBtn")
    private Button billBtn;

    @Named("cancelBtn")
    private Button cancelBtn;

    @Named("closeBtn")
    private Button closeBtn;

    private Boolean isSplitTableBtnPressed = false;
    private Boolean isMergeTableBtnPressed = false;
    private Boolean isMoveTableBtnPressed = false;
    private Boolean isBillBtnPressed = false;
    private Boolean isCancelBtnPressed = false;
    private Boolean isCloseBtnPressed = false;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        tableItemsDs.refresh();

        upperHBox.setStyleName("upperHBox");
        bottomHBox.setStyleName("bottomHBox");

        currentTimeField.setStyleName("currentTimeField");

        currentTimeField.setValue(Date.from(Instant.now()));

        drawTableElements();

        Timer clockTimer = componentsFactory.createTimer();
        addTimer(clockTimer);
        clockTimer.setDelay(1000);
        clockTimer.setRepeating(true);
        clockTimer.addActionListener(timer -> refreshData());

        clockTimer.start();

    }

    private void freeTable(TableItem tableToFree) {

        tableToFree.setTableStatus(TableItemStatus.free);
        tableToFree.setCurrentOrder(null);
        dataManager.commit(tableToFree);
        tableItemsDs.refresh();

    }

    private void refreshData() {

        currentTimeField.setValue(Date.from(Instant.now()));

        tableItemsDs.refresh();

        tableBtnFirstGrid.removeAll();
        tableBtnSecondGrid.removeAll();
        tableBtnThirdGrid.removeAll();

        drawTableElements();

    }

    private void drawTableElements() {

        int tableCounter = 0;

        for (TableItem tableItem : tableItemsDs.getItems()) {

            tableCounter++;
            VBoxLayout vBox = componentsFactory.createComponent(VBoxLayout.class);
            vBox.setStyleName("tableSelectVBox");
            WebButton btn = componentsFactory.createComponent(WebButton.class);
            WebGroupBox groupBox = componentsFactory.createComponent(WebGroupBox.class);
            groupBox.setWidth("190px");
            groupBox.setHeight("140px");
            groupBox.setAlignment(Alignment.TOP_CENTER);
            groupBox.setId("groupBoxLayout".concat(tableItem.getTableNumber().toString()));

            if (tableItem.getCurrentOrder() != null) {

                TimeField tableBtnTimeField = componentsFactory.createComponent(TimeField.class);

                tableBtnTimeField.setAlignment(Alignment.TOP_CENTER);
                tableBtnTimeField.setWidth("160px");
                tableBtnTimeField.setHeight("35px");
                tableBtnTimeField.setId("tableBtnTimeField".concat(tableItem.getTableNumber().toString()));

                Long tableTime = Instant.now().getEpochSecond() - tableItem.getCurrentOrder().getCreateTs().toInstant().getEpochSecond() - 3600;

                if (tableTime <= 0) tableBtnTimeField.setStyleName("tableTimeField-normal");
                else tableBtnTimeField.setStyleName("tableTimeField-hot");

                tableBtnTimeField.setValue(Date.from(Instant.ofEpochSecond(tableTime)));

                TextField paxTextField = componentsFactory.createComponent(TextField.class);
                paxTextField.setAlignment(Alignment.MIDDLE_CENTER);
                paxTextField.setWidth("160px");
                paxTextField.setHeight("30px");
                paxTextField.setStyleName("tableInfoPaxTextField");
                paxTextField.setValue("PAX: ".concat(tableItem.getCurrentOrder().getActualSeats().toString()));

                groupBox.add(tableBtnTimeField);
                groupBox.add(paxTextField);

                TextField totalAmountTextField = componentsFactory.createComponent(TextField.class);
                totalAmountTextField.setAlignment(Alignment.BOTTOM_CENTER);
                totalAmountTextField.setWidth("160px");
                totalAmountTextField.setHeight("30px");

                if (tableItem.getCurrentOrder().getWithService()) {

                    totalAmountTextField.setStyleName("tableInfoTotalAmountTextFieldService");
                    totalAmountTextField.setValue("€  ".concat(tableItem.getCurrentOrder().getCharge()
                            .add(tableItem.getCurrentOrder().getTaxes()).toString()));

                } else {

                    totalAmountTextField.setStyleName("tableInfoTotalAmountTextFieldNoService");
                    totalAmountTextField.setValue("€  ".concat(tableItem.getCurrentOrder().getCharge().toString()));

                }

                groupBox.add(totalAmountTextField);

            }

            btn.setWidth("190px");
            btn.setHeight("110px");
            btn.setAlignment(Alignment.BOTTOM_CENTER);

            if (tableItem.getTableCaption().length() <= 3) {

                if (tableItem.getTableStatus().equals(TableItemStatus.free)) btn.setStyleName("v-button-backgroundColorGreenFont60");
                else if (tableItem.getTableStatus().equals(TableItemStatus.open)) if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.open)) btn.setStyleName("v-button-backgroundColorRedFont60");
                else if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.bill)) btn.setStyleName("v-button-backgroundColorYellowFont60");
                else if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.paid)) btn.setStyleName("v-button-backgroundColorAzureFont60");

            } else if (tableItem.getTableCaption().length() <= 8) {

                if (tableItem.getTableStatus().equals(TableItemStatus.free)) btn.setStyleName("v-button-backgroundColorGreenFont40");
                else if (tableItem.getTableStatus().equals(TableItemStatus.open)) if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.open)) btn.setStyleName("v-button-backgroundColorRedFont40");
                else if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.bill)) btn.setStyleName("v-button-backgroundColorYellowFont40");
                else if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.paid)) btn.setStyleName("v-button-backgroundColorAzureFont40");

            } else if (tableItem.getTableStatus().equals(TableItemStatus.free)) btn.setStyleName("v-button-backgroundColorGreenFont30");

            else if (tableItem.getTableStatus().equals(TableItemStatus.open)) if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.open)) btn.setStyleName("v-button-backgroundColorRedFont30");
            else if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.bill)) btn.setStyleName("v-button-backgroundColorYellowFont30");
            else if (tableItem.getCurrentOrder().getStatus().equals(OrderStatus.paid)) btn.setStyleName("v-button-backgroundColorAzureFont30");

            btn.setCaption(tableItem.getTableCaption());
            btn.setAction(new BaseAction("buttonAction".concat(tableItem.getTableNumber().toString())).withHandler(e -> buttonAction(tableItem)));

            vBox.add(btn);
            vBox.add(groupBox);
            if (tableCounter <= 9) {
                tableBtnFirstGrid.add(vBox);
            }
            if (tableCounter > 9 && tableCounter <= 18) {
                tableBtnSecondGrid.add(vBox);
            }
            if (tableCounter > 18 && tableCounter <= 27) {
                tableBtnThirdGrid.add(vBox);
            }

            if (tableItem.getChecked()) btn.setEnabled(false);
            else btn.setEnabled(true);

        }

    }

    public void onSplitTableBtnClick() {

        if (!isSplitTableBtnPressed) {

            deselectFunctionButtons();
            isSplitTableBtnPressed = true;
            splitTableBtn.setStyleName("tableScreenFunctionBtnPressed");

        } else {

            deselectFunctionButtons();
            isSplitTableBtnPressed = false;
            splitTableBtn.setStyleName("tableScreenFunctionBtn");

        }

    }

    public void onMergeTableBtnClick() {

        if (!isMergeTableBtnPressed) {

            deselectFunctionButtons();
            isMergeTableBtnPressed = true;
            mergeTableBtn.setStyleName("tableScreenFunctionBtnPressed");

        } else {

            deselectFunctionButtons();
            isMergeTableBtnPressed = false;
            mergeTableBtn.setStyleName("tableScreenFunctionBtn");

        }

    }

    public void onMoveTableBtnClick() {

        if (!isMoveTableBtnPressed) {

            deselectFunctionButtons();
            isMoveTableBtnPressed = true;
            moveTableBtn.setStyleName("tableScreenFunctionBtnPressed");

        } else {

            deselectFunctionButtons();
            isMoveTableBtnPressed = false;
            moveTableBtn.setStyleName("tableScreenFunctionBtn");

        }

    }

    public void onBillClick() {

        if (!isBillBtnPressed) {

            deselectFunctionButtons();
            isBillBtnPressed = true;
            billBtn.setStyleName("tableScreenFunctionBtnPressed");

        } else {

            deselectFunctionButtons();
            isBillBtnPressed = false;
            billBtn.setStyleName("tableScreenFunctionBtn");

        }

    }

    public void onCancelBtnClick() {

        if (!isCancelBtnPressed) {

            deselectFunctionButtons();
            isCancelBtnPressed = true;
            cancelBtn.setStyleName("tableScreenFunctionBtnPressed");

        } else {

            deselectFunctionButtons();
            isCancelBtnPressed = false;
            cancelBtn.setStyleName("tableScreenFunctionBtn");

        }

    }

    public void onCloseBtnClick() {

        if (!isCloseBtnPressed) {

            deselectFunctionButtons();
            isCloseBtnPressed = true;
            closeBtn.setStyleName("tableScreenFunctionBtnPressed");

        } else {

            deselectFunctionButtons();
            isCloseBtnPressed = false;
            closeBtn.setStyleName("tableScreenFunctionBtn");

        }

    }

    private void deselectFunctionButtons() {

        isSplitTableBtnPressed = false;
        splitTableBtn.setStyleName("tableScreenFunctionBtn");
        isMergeTableBtnPressed = false;
        mergeTableBtn.setStyleName("tableScreenFunctionBtn");
        isMoveTableBtnPressed = false;
        moveTableBtn.setStyleName("tableScreenFunctionBtn");
        isBillBtnPressed = false;
        billBtn.setStyleName("tableScreenFunctionBtn");
        isCancelBtnPressed = false;
        cancelBtn.setStyleName("tableScreenFunctionBtn");
        isCloseBtnPressed = false;
        closeBtn.setStyleName("tableScreenFunctionBtn");

    }

    private void buttonAction(TableItem selectedTable) {

        if (isSplitTableBtnPressed) {



        } else if (isMergeTableBtnPressed) {



        } else if (isMoveTableBtnPressed) {



        } else if (isBillBtnPressed) {



        } else if (isCancelBtnPressed) {

            if (selectedTable.getCurrentOrder()!=null) {

                selectedTable.getCurrentOrder().setStatus(OrderStatus.cancelled);
                selectedTable.setCurrentOrder(null);
                selectedTable.setTableStatus(TableItemStatus.free);

                dataManager.commit(selectedTable);

                deselectFunctionButtons();

                getWindowManager().close(this);
                openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

            } else deselectFunctionButtons();

        } else  if (isCloseBtnPressed) {

            if (selectedTable.getCurrentOrder() != null && selectedTable.getCurrentOrder().getStatus().equals(OrderStatus.bill)) {

                selectedTable.getCurrentOrder().setStatus(OrderStatus.closed);

                dataManager.commit(selectedTable.getCurrentOrder());

                selectedTable.setCurrentOrder(null);
                selectedTable.setTableStatus(TableItemStatus.free);

                dataManager.commit(selectedTable);

                deselectFunctionButtons();

                getWindowManager().close(this);
                openWindow("tableselect", WindowManager.OpenType.THIS_TAB);

            } else deselectFunctionButtons();

        } else {

            TableItemStatus tableItemStatus = selectedTable.getTableStatus();

            Map<String, Object> orderParams = new HashMap<>();

            orderParams.put("selectedTable", selectedTable);

            if (tableItemStatus.equals(TableItemStatus.free)) {

                Map<String, Object> params = new HashMap<>();

                params.put("table", selectedTable);

                ActualSeatsDialog.CloseHandler handler = new ActualSeatsDialog.CloseHandler() {

                    @Override
                    public void onClose(int seats) {

                        orderParams.put("actualSeats", seats);

                    }

                };

                params.put("handler", handler);

                openWindow("jokerapp$ActualSeats.dialog", WindowManager.OpenType.DIALOG, params).addCloseListener(closeString -> {

                    if (closeString.equals("ok")) {

                        getWindowManager().close(this);
                        openWindow("orderScreen", WindowManager.OpenType.THIS_TAB, orderParams);

                    }

                });


            } else if (tableItemStatus.equals(TableItemStatus.open)) {

                getWindowManager().close(this);
                openWindow("orderScreen", WindowManager.OpenType.THIS_TAB, orderParams);

            } else if (tableItemStatus.equals(TableItemStatus.closed)) {

                showOptionDialog(
                        getMessage("freeTableDialog.title"),
                        getMessage("freeTableDialog.msg"),
                        MessageType.CONFIRMATION,
                        new Action[] {
                                new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> freeTable(selectedTable)),
                                new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)

                        }

                );

            }

        }


    }

}