package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.TableItemStatus;
import com.joker.jokerapp.entity.Order;

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

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        tableItemsDs.refresh();

        for (TableItem tableItem: tableItemsDs.getItems()) {

          grid.setColumns(Math.floorDiv(tableItemsDs.size(),3));
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

    private void openOrderScreen(TableItem table) {

        TableItemStatus tableItemStatus = table.getTableStatus();

        Order currentOrder = null;

        if (tableItemStatus == TableItemStatus.free) {
            currentOrder =  metadata.create(Order.class);
            currentOrder.setTableItem(table);
            currentOrder.setActualSeats(getActualSeats(table));

        } else if (tableItemStatus == TableItemStatus.open) {
            currentOrder = table.getCurrentOrder();
            openEditor("orderscreen", currentOrder, WindowManager.OpenType.THIS_TAB);
        } else if (tableItemStatus == TableItemStatus.closed) {
            showOptionDialog(
                    getMessage("freeTableDialog.title"),
                    getMessage("freeTableDialog.msg"),
                    MessageType.CONFIRMATION,
                    new Action[] {
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> freeTable(table)),
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

    private Integer getActualSeats(TableItem table) {
        Map<String, Object> params = new HashMap<>();
        params.put("table", table);

        openWindow("jokerapp$ActualSeats.dialog", WindowManager.OpenType.DIALOG, params);
        return 3;
    }

}