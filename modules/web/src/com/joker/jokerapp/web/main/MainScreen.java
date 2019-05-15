package com.joker.jokerapp.web.main;

import com.haulmont.cuba.core.global.DataManager;

import com.haulmont.cuba.gui.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.haulmont.cuba.web.gui.components.WebGroupBox;
import com.joker.jokerapp.entity.OrderStatus;
import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.TableItemStatus;
import com.joker.jokerapp.web.popups.ActualSeats;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.util.Date;

@UiController("jokerapp_MainScreen")
@UiDescriptor("main-screen.xml")
@LoadDataBeforeShow

public class MainScreen extends Screen {

    @Inject
    private CollectionContainer<TableItem> tableItemsDc;

    @Inject
    private DataManager dataManager;

    @Inject
    private UiComponents uiComponents;

    @Inject
    private ScreenBuilders screenBuilders;

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

    @Subscribe
    protected void onInit(InitEvent event) {

        currentTimeField.setValue(Date.from(Instant.now()));

        getScreenData().loadAll();
        drawTableElements();

    }

    private void freeTable(TableItem tableToFree) {

        tableToFree.setTableStatus(TableItemStatus.free);
        tableToFree.setCurrentOrder(null);
        dataManager.commit(tableToFree);

    }

    private void refreshData() {

        currentTimeField.setValue(Date.from(Instant.now()));

        getScreenData().loadAll();

        tableBtnFirstGrid.removeAll();
        tableBtnSecondGrid.removeAll();
        tableBtnThirdGrid.removeAll();

        drawTableElements();

    }

    private void drawTableElements() {

        int tableCounter = 0;

        for (TableItem tableItem: tableItemsDc.getItems()) {

            tableCounter++;
            VBoxLayout vBox = uiComponents.create(VBoxLayout.class);
            vBox.setStyleName("tableSelectVBox");
            WebButton btn = uiComponents.create(WebButton.class);
            WebGroupBox groupBox = uiComponents.create(WebGroupBox.class);
            groupBox.setWidth("190px");
            groupBox.setHeight("140px");
            groupBox.setAlignment(Component.Alignment.TOP_CENTER);
            groupBox.setId("groupBoxLayout".concat(tableItem.getTableNumber().toString()));

            if (tableItem.getCurrentOrder() != null) {

                TimeField tableBtnTimeField = uiComponents.create(TimeField.class);

                tableBtnTimeField.setAlignment(Component.Alignment.TOP_CENTER);
                tableBtnTimeField.setWidth("160px");
                tableBtnTimeField.setHeight("35px");
                tableBtnTimeField.setId("tableBtnTimeField".concat(tableItem.getTableNumber().toString()));

                Long tableTime = Instant.now().getEpochSecond() - tableItem.getCurrentOrder().getCreateTs().toInstant().getEpochSecond() - 3600;

                if (tableTime <= 0) tableBtnTimeField.setStyleName("tableTimeField-normal");
                else tableBtnTimeField.setStyleName("tableTimeField-hot");

                tableBtnTimeField.setValue(Date.from(Instant.ofEpochSecond(tableTime)));

                TextField paxTextField = uiComponents.create(TextField.class);
                getWindow();
                paxTextField.setAlignment(Component.Alignment.MIDDLE_CENTER);
                paxTextField.setWidth("160px");
                paxTextField.setHeight("30px");
                paxTextField.setStyleName("tableInfoPaxTextField");
                paxTextField.setValue("PAX: ".concat(tableItem.getCurrentOrder().getActualSeats().toString()));

                groupBox.add(tableBtnTimeField);
                groupBox.add(paxTextField);

                TextField totalAmountTextField = uiComponents.create(TextField.class);
                totalAmountTextField.setAlignment(Component.Alignment.BOTTOM_CENTER);
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
            btn.setAlignment(Component.Alignment.BOTTOM_CENTER);

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

    @Subscribe("billBtn")
    protected void onBillClick(Button.ClickEvent event) {

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

            if (selectedTable.getCurrentOrder() != null) {

                selectedTable.getCurrentOrder().setStatus(OrderStatus.cancelled);
                dataManager.commit(selectedTable.getCurrentOrder());
                selectedTable.setCurrentOrder(null);
                selectedTable.setTableStatus(TableItemStatus.free);

                dataManager.commit(selectedTable);

                deselectFunctionButtons();

                closeWithDefaultAction();
                screenBuilders.screen(this)
                        .withScreenClass(MainScreen.class)
                        .build()
                        .show();

            } else deselectFunctionButtons();

        } else if (isCloseBtnPressed) {

            if (selectedTable.getCurrentOrder() != null && selectedTable.getCurrentOrder().getStatus().equals(OrderStatus.bill)) {

                selectedTable.getCurrentOrder().setStatus(OrderStatus.closed);

                dataManager.commit(selectedTable.getCurrentOrder());

                selectedTable.setCurrentOrder(null);
                selectedTable.setTableStatus(TableItemStatus.free);

                dataManager.commit(selectedTable);

                deselectFunctionButtons();

                closeWithDefaultAction();
                screenBuilders.screen(this)
                        .withScreenClass(MainScreen.class)
                        .build()
                        .show();

            } else deselectFunctionButtons();

        } else {

            TableItemStatus tableItemStatus = selectedTable.getTableStatus();

            if (tableItemStatus.equals(TableItemStatus.free)) {

                ActualSeats actualSeats = screenBuilders.screen(this)
                        .withScreenClass(ActualSeats.class)
                        .withAfterCloseListener(afterCloseEvent -> {
                            ActualSeats screen = afterCloseEvent.getScreen();
                            if (afterCloseEvent.getCloseAction().equals(WINDOW_COMMIT_AND_CLOSE_ACTION)) {

                        String seats = screen.getSeats();

                        closeWithDefaultAction();

                    }

                        })
                        .build();

                actualSeats.setSeatsCapacity(selectedTable.getSeatsCapacity().toString());
                actualSeats.show();

            } else if (tableItemStatus.equals(TableItemStatus.open)) {

                closeWithDefaultAction();
                //openWindow("orderScreen", WindowManager.OpenType.THIS_TAB, orderParams);

            } else if (tableItemStatus.equals(TableItemStatus.closed)) {

                /*showOptionDialog(
                        getMessage("freeTableDialog.title"),
                        getMessage("freeTableDialog.msg"),
                        Frame.MessageType.CONFIRMATION,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> freeTable(selectedTable)),
                                new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)

                        }

                );*/

            }

        }

    }

    public void onTimerClick(Timer source) {

        refreshData();

    }
}