package com.joker.jokerapp.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum UserStatus implements EnumClass<String> {

    takingOrder("takingOrder"),
    available("available");

    private String id;

    UserStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static UserStatus fromId(String id) {
        for (UserStatus at : UserStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}