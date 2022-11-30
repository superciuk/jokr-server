package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum OrderStatus implements EnumClass<String> {

    empty("empty"),
    onlyBeverage("onlyBeverage"),
    onlyFood("onlyFood"),
    foodAndBeverage("foodAndBeverage"),
    bill("bill"),
    paid("paid"),
    cancelled("cancelled"),
    closed("closed");

    private String id;

    OrderStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static OrderStatus fromId(String id) {
        for (OrderStatus at : OrderStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}