package com.joker.jokerapp.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamePattern("%s|name")
@Table(name = "JOKERAPP_WORKPLACE")
@Entity(name = "jokerapp_Workplace")
public class Workplace extends StandardEntity {
    private static final long serialVersionUID = 3565253833938196929L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}