package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.gui.components.*;
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
          btn.setId(tableItem.getNumber().toString());
          btn.setCaption(tableItem.getNumber().toString());

//          btn.setAction(Action );


          grid.add(btn);

        }

    }

}