package com.joker.jokerapp.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamePattern("%s|areaName")
@Table(name = "JOKERAPP_TABLE_ITEM_AREA")
@Entity(name = "jokerapp$TableItemArea")
public class TableItemArea extends StandardEntity {
    private static final long serialVersionUID = -4633258643506803805L;

    @NotNull
    @Column(name = "AREA_NUMBER", nullable = false, unique = true)
    protected Integer areaNumber;

    @NotNull
    @Column(name = "AREA_NAME", nullable = false, unique = true)
    protected String areaName;

    public Integer getAreaNumber() {
        return areaNumber;
    }

    public void setAreaNumber(Integer areaNumber) {
        this.areaNumber = areaNumber;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}