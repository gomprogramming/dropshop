package dropshop.controller;

import dropshop.model.ShopModel;
import dropshop.model.entities.OrderStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsController {
    private final ShopModel model;
    private StatisticsView view;

    public StatisticsController(ShopModel model) {
        this.model = model;
    }

    public void setView(StatisticsView view) {
        this.view = view;
    }

    public void load() {
        Map<OrderStatus, Integer> byStatus = new LinkedHashMap<>();
        int totalOrders = 0;
        for (OrderStatus status : OrderStatus.values()) {
            int count = model.countOrders(status);
            byStatus.put(status, count);
            totalOrders += count;
        }
        view.showSummary(model.getTotalRevenue(), model.getTotalProfit(), totalOrders, byStatus);
        view.showBestSellers(model.getUnitsSoldByProduct());
    }

    public void back() {
        view.goBack();
    }
}