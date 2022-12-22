package com.joker.jokerapp.web.screens.workplace;

import com.haulmont.cuba.gui.screen.*;
import com.joker.jokerapp.entity.Workplace;

@UiController("jokerapp_Workplace.browse")
@UiDescriptor("workplace-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class WorkplaceBrowse extends MasterDetailScreen<Workplace> {
}