package com.joker.jokerapp.service;

public interface OrderService {
    String NAME = "jokerapp_OrderService";

    String createNewOrder(String tableId, String actualSeats);

    boolean addToOrder(String orderId, String productItemId);

    boolean plusButtonPressed(String orderId, String orderLineId);

    boolean minusButtonPressed(String orderId, String orderLineId);

    boolean sendOrder(String orderId);

}