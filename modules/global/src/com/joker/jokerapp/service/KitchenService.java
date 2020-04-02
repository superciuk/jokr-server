package com.joker.jokerapp.service;

public interface KitchenService {

    String NAME = "jokerapp_KitchenService";

    boolean bumpLine(String ticketId, String orderLineId);

}