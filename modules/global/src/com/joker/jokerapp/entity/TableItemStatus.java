package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TableItemStatus implements EnumClass<String> {

    free("free"),
    open("open"),
    closed("closed");

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