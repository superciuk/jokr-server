package com.joker.jokerapp.web.tableitem;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.TableItemStatus;

public class TableItemBrowse extends EntityCombinedScreen {

    @Override
    protected void initNewItem(Entity item) {
        ((TableItem) item).setTableStatus(TableItemStatus.free);
        super.initNewItem(item);

    }
}