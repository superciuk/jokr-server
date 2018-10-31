package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TableItemStatus implements EnumClass<String> {

    closed("0"),
    open("10"),
    reserved("20");

    private String id;

    TableItemStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TableItemStatus fromId(String id) {
        for (TableItemStatus at : TableItemStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}