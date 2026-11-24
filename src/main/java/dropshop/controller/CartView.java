package dropshop.controller;

import java.util.List;

public interface CartView {
    void showItems(List<CartLine> lines, double total);
    void showError(String message);
    void showOrderPlaced(int orderId, double total);
    void goBack();
}