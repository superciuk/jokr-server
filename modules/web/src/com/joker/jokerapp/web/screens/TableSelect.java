package com.joker.jokerapp.web.screens;

import ch.qos.logback.classic.db.names.TableName;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.TableItem;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class TableSelect extends AbstractWindow {


    @Inject
    private GroupDatasource<TableItem, UUID> tableItemsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("order")
    private Window order;

    @Named("grid")
    private GridLayout grid;

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
          btn.setAction(new BaseAction("openOrderScreen".concat(tableItem.getTableNumber().toString())).withHandler(e -> openOrderScreen(tableItem)));
          grid.add(btn);

        }

    }

    private void openOrderScreen(TableItem table) {

        Map<String, Object> params = new HashMap<>();
        params.put("table", table);
        if (table.getStatus() == 0) {
            table.setOrderId(UUID.randomUUID());
            table.setStatus(1);
        }
        openWindow("order", WindowManager.OpenType.THIS_TAB, params);
    }

}