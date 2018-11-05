package com.joker.jokerapp.web.screens;

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

        if (tableItemStatus == TableItemStatus.closed || tableItemStatus == TableItemStatus.reserved) {
            currentOrder =  metadata.create(Order.class);

        } else if (tableItemStatus == TableItemStatus.open) {
            currentOrder = table.getCurrentOrder();
        }


        Map<String, Object> params = new HashMap<>();
        params.put("order", currentOrder);

        openWindow("orderscreen", WindowManager.OpenType.THIS_TAB, params);

    }

}