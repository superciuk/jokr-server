package com.joker.jokerapp.web.preferences;

import com.haulmont.cuba.gui.screen.*;
import com.joker.jokerapp.entity.Preferences;

@UiController("jokerapp_Preferences.browse")
@UiDescriptor("preferences-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class PreferencesBrowse extends MasterDetailScreen<Preferences> {
}