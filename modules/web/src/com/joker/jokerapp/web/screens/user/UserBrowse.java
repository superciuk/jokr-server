package com.joker.jokerapp.web.screens.user;

import com.haulmont.cuba.gui.screen.*;
import com.joker.jokerapp.entity.User;
import com.joker.jokerapp.entity.UserStatus;

@UiController("jokerapp_User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class UserBrowse extends MasterDetailScreen<User> {

    @Subscribe
    protected void onInitEntity(InitEntityEvent<User> event) {
        event.getEntity().setUserStatus(UserStatus.available);
    }

}