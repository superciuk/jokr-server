package com.joker.jokerapp.web.screens.tableitemarea;

import com.haulmont.cuba.gui.screen.*;
import com.joker.jokerapp.entity.TableItemArea;

@UiController("jokerapp_TableItemArea.browse")
@UiDescriptor("table-item-area-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class TableItemAreaBrowse extends MasterDetailScreen<TableItemArea> {
}