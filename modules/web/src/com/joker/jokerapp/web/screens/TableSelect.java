package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.OrderStatus;
import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.TableItemStatus;
import com.joker.jokerapp.entity.Order;
import com.joker.jokerapp.web.dialogs.ActualSeatsDialog;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class TableSelect extends AbstractWindow {

    @Inject
    private GroupDatasource<TableItem, UUID> tableItemsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("grid")
    private GridLayout grid;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        tableItemsDs.refresh();

        if (tableItemsDs.size()>3) grid.setColumns(Math.floorDiv(tableItemsDs.size(),3)+1);

        for (TableItem tableItem: tableItemsDs.getItems()) {


            WebButton btn = componentsFactory.createComponent(WebButton.class);
            btn.setWidth("200px");
            btn.setHeight("200px");
            btn.setId(tableItem.getTableNumber().toString());
            btn.setCaption(tableItem.getTableNumber().toString());
            btn.setAction(new BaseAction("openOrderScreen".concat(tableItem.getTableNumber().toString()))
                  .withHandler(e -> openOrderScreen(tableItem)));
            grid.add(btn);

        }

    }

    private void openOrderScreen(TableItem selectedTable) {

        TableItemStatus tableItemStatus = selectedTable.getTableStatus();

        final Order currentOrder;

        if (tableItemStatus == TableItemStatus.free) {

            selectedTable.setTableStatus(TableItemStatus.open);

            currentOrder =  metadata.create(Order.class);
            currentOrder.setStatus(OrderStatus.open);
            currentOrder.setTableItemNumber(selectedTable.getTableNumber());
            currentOrder.setActualSeats(0);

            selectedTable.setCurrentOrder(currentOrder);
            dataManager.commit(selectedTable);

            Map<String, Object> params = new HashMap<>();
            params.put("table", selectedTable);

            ActualSeatsDialog.CloseHandler handler = new ActualSeatsDialog.CloseHandler() {
                @Override
                public void onClose(int seats) {
                    currentOrder.setActualSeats(seats);
                }
            };

            params.put("handler", handler);

            openWindow("jokerapp$ActualSeats.dialog", WindowManager.OpenType.DIALOG, params)
                    .addCloseListener(closeString -> {
                        if (closeString.equals("ok")) {
                            openEditor("orderscreen", currentOrder, WindowManager.OpenType.THIS_TAB);
                        }
                    });

        } else if (tableItemStatus == TableItemStatus.open) {

            currentOrder = selectedTable.getCurrentOrder();
            openEditor("orderscreen", currentOrder, WindowManager.OpenType.THIS_TAB);
        } else if (tableItemStatus == TableItemStatus.closed) {
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

    private void freeTable(TableItem table) {

        table.setTableStatus(TableItemStatus.free);
        dataManager.commit(table);
        tableItemsDs.refresh();

    }

}