package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum UserType implements EnumClass<String> {

    chief("chief"),
    waiter("waiter"),
    barman("barman"),
    chef("chef");

    private String id;

    UserType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static UserType fromId(String id) {
        for (UserType at : UserType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}