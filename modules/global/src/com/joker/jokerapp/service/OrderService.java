package com.joker.jokerapp.service;

public interface OrderService {

    String NAME = "jokerapp_OrderService";

    String createNewOrder(String tableId, String actualSeats);

    String addToOrder(String orderId, String productItemId, String isModifier, String selectedOrderLineId, String plusOrMinus, String manualModifierText, String manualModifierPrice, String withModifiers);

    boolean plusButtonPressed(String orderId, String orderLineId);

    boolean minusButtonPressed(String orderId, String orderLineId);

    boolean quantityButtonPressed(String orderId, String orderLineId);

    boolean priceButtonPressed(String orderId, String orderLineId, String price);

    boolean sendOrder(String tableItemId, String printTicket);

    boolean removeEmptyTickets(String tableItemId);

    boolean printBill(String tableItemId);

    boolean freeTable(String tableItemId);

    boolean reopenTable(String tableItemId);

}