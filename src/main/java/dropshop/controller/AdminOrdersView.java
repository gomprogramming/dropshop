package dropshop.controller;

import dropshop.model.entities.Order;
import java.util.List;

public interface AdminOrdersView {
    void showOrders(List<Order> orders);
    void showError(String message);
    void goBack();
}