package dropshop.controller;

import dropshop.model.entities.Order;
import java.util.List;

public interface OrdersView {
    void showOrders(List<Order> orders);
    void showMessage(String message);
    void showError(String message);
    void goBack();
}