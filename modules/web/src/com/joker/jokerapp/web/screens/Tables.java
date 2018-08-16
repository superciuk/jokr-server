package com.joker.jokerapp.web.screens;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.joker.jokerapp.entity.TableItem;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class Tables extends AbstractWindow {

    @Inject
    private GroupDatasource<TableItem, UUID> tableItemsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("grid")
    private GridLayout grid;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        WebButton btn1 = componentsFactory.createComponent(WebButton.class);
        btn1.setId("button1");
        btn1.setHeight("300px");
        btn1.setWidth("300px");
        btn1.setCaption("PROVA");
        grid.add(btn1);

        for (TableItem item: tableItemsDs.getItems()) {

            WebButton btn = componentsFactory.createComponent(WebButton.class);
            btn.setId(item.getValue("number"));
            btn.setDescription(item.getValue("number"));
            grid.add(btn);
        }

    }

}