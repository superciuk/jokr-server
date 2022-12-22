package com.joker.jokerapp.service;

public interface OrderService {

    String NAME = "jokerapp_OrderService";

    boolean checkConnection();

    String userLogin(String username, String userPassword);

    boolean setNotificationToken(String userId, String token);

    String createNewOrder(String tableId, String actualSeats, String userId, String orderInProgress);

    String addToOrder(String orderId, String userId, String productItemId, String isModifier, String selectedOrderLineId, String plusOrMinus, String manualModifierText, String manualModifierPrice, String withModifiers);

    boolean plusButtonPressed(String orderId, String userId, String orderLineId);

    boolean minusButtonPressed(String orderId, String orderLineId);

    boolean quantityButtonPressed(String orderId, String orderLineId, String operation);

    boolean splitLineButtonPressed(String orderId, String orderLineId);

    boolean priceButtonPressed(String orderId, String orderLineId, String price);

    boolean sendOrder(String tableItemId, String ticketId, String SendAndClose, String printTicket);

    boolean removeEmptyTickets(String tableItemId);

    boolean printBill(String tableItemId);

    boolean freeTable(String tableItemId);

    boolean reopenTable(String tableItemId, String whichOrder);

    boolean tableReservation(String tableItemToReserveId, String operation, String tableReservationName, String tableReservationSeats, String tableReservationTime, String tableReservationPhoneNumber);

    boolean renameTable(String orderId, String newTableName);

    boolean setActualSeats(String orderId, String newActualSeats);

    String setWithService(String orderId, String trueOrFalse);

    boolean moveTable(String tableItemToMoveId, String newTableItemId);

    boolean setOrderInProgress(String orderId, String trueOrFalse);

    boolean setWaiterCall(String orderId, String userId);

}