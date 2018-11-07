package com.joker.jokerapp.listener;

import com.haulmont.cuba.core.global.DataManager;
import com.joker.jokerapp.entity.TableItem;
import com.joker.jokerapp.entity.TableItemStatus;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import java.sql.Connection;
import com.joker.jokerapp.entity.Order;

import javax.inject.Inject;


@Component("jokerapp_NewOrderEntityListener")
public class NewOrderEntityListener implements AfterInsertEntityListener<Order> {

    @Inject
    private DataManager dataManager;

    @Override
    public void onAfterInsert(Order entity, Connection connection) {
        TableItem table = entity.getTableItem();

        TableItemStatus tableItemStatus = table.getTableStatus();

        if (tableItemStatus == TableItemStatus.free || tableItemStatus == TableItemStatus.closed) {
            table.setTableStatus(TableItemStatus.open);
            dataManager.commit(table);
        }

    }

}