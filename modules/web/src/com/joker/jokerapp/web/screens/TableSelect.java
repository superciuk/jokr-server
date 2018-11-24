package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.TableItemStatus;
import com.joker.jokerapp.web.dialogs.ActualSeatsDialog;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class TableSelect extends AbstractWindow {

    @Inject
    private Datasource<TableItem> tableItemDs;

    @Inject
    private GroupDatasource<TableItem, UUID> tableItemsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("grid")
    private GridLayout grid;

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
            btn.setAction(new BaseAction("openOrderScreen".concat(tableItem.getTableNumber().toString())).withHandler(e -> openOrderScreen(tableItem)));
            grid.add(btn);

        }

    }

    private void openOrderScreen(TableItem selectedTable) {

        tableItemDs.setItem(selectedTable);
        tableItemDs.refresh();

        TableItemStatus tableItemStatus = tableItemDs.getItem().getTableStatus();

        Map<String, Object> orderParams = new HashMap<>();
        orderParams.put("tableNumber",selectedTable.getTableNumber());

        if (tableItemStatus == TableItemStatus.free) {

            Map<String, Object> params = new HashMap<>();
            params.put("table", tableItemDs.getItem());

            ActualSeatsDialog.CloseHandler handler = new ActualSeatsDialog.CloseHandler() {
                @Override
                public void onClose(int seats) {
                    orderParams.put("actualSeats", seats);
                }
            };

            params.put("handler", handler);

            openWindow("jokerapp$ActualSeats.dialog", WindowManager.OpenType.DIALOG, params).addCloseListener(closeString -> {

                if (closeString.equals("ok")) {
                    openWindow("orderScreen", WindowManager.OpenType.THIS_TAB, orderParams);
                }

            });


        } else if (tableItemStatus == TableItemStatus.open) {

            openWindow("orderScreen", WindowManager.OpenType.THIS_TAB, orderParams);

        } else if (tableItemStatus == TableItemStatus.closed) {

            showOptionDialog(
                    getMessage("freeTableDialog.title"),
                    getMessage("freeTableDialog.msg"),
                    MessageType.CONFIRMATION,
                    new Action[] {
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> freeTable(tableItemDs.getItem())),
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                    }
            );

        }

    }

    private void freeTable(TableItem tableToFree) {

        tableItemDs.setItem(tableToFree);
        tableItemDs.refresh();
        tableItemDs.getItem().setTableStatus(TableItemStatus.free);
        tableItemDs.getItem().setCurrentOrder(null);
        tableItemDs.commit();

    }


    public void onEmptyClick() {
        tableItemsDs.clear();
        tableItemsDs.commit();

    }
}