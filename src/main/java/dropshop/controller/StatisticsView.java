package dropshop.controller;

import dropshop.model.entities.OrderStatus;
import java.util.Map;

public interface StatisticsView {
    void showSummary(double revenue, double benefit, int totalOrders, Map<OrderStatus, Integer> ordersByStatus);
    void showBestSellers(Map<String, Integer> unitsByProduct);
    void goBack();
}