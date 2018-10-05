package com.joker.jokerapp.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "JOKERAPP_TICKET")
@Entity(name = "jokerapp$Ticket")
public class Ticket extends StandardEntity {
    private static final long serialVersionUID = 8400685436814283626L;

}