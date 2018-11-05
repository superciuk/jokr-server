package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TableItemStatus implements EnumClass<String> {

    free("0"),
    reserved("10"),
    open("20"),
    closed("30");

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