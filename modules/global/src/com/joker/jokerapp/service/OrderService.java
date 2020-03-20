package com.joker.jokerapp.service;

public interface OrderService {

    String NAME = "jokerapp_OrderService";

    String createNewOrder(String tableId, String actualSeats);

    String addToOrder(String orderId, String productItemId, String isModifier, String selectedOrderLineId, String plusOrMinus, String withModifiers);

    boolean plusButtonPressed(String orderId, String orderLineId);

    boolean minusButtonPressed(String orderId, String orderLineId);

    boolean sendOrder(String tableItemId);

    boolean printBill(String tableItemId);

}