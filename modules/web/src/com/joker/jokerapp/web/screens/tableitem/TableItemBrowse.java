package com.joker.jokerapp.web.screens.tableitem;

import com.haulmont.cuba.gui.screen.*;
import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.TableItemStatus;

@UiController("jokerapp$TableItem.browse")
@UiDescriptor("table-item-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class TableItemBrowse extends MasterDetailScreen<TableItem> {

    @Subscribe
    protected void onInitEntity(InitEntityEvent<TableItem> event) {
        event.getEntity().setTableStatus(TableItemStatus.free);
    }

}