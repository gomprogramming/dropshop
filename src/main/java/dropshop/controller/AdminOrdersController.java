package dropshop.controller;

import dropshop.model.ShopModel;
import dropshop.model.entities.Order;

public class AdminOrdersController {
    private final ShopModel model;
    private AdminOrdersView view;

    public AdminOrdersController(ShopModel model) {
        this.model = model;
    }

    public void setView(AdminOrdersView view) {
        this.view = view;
    }

    public void loadOrders() {
        view.showOrders(model.getAllOrders());
    }

    public void advance(int orderId) {
        try {
            model.advanceOrder(orderId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    public String getCustomerName(Order order) {
        return model.getUsernameOf(order.getUserId());
    }

    public void back() {
        view.goBack();
    }
}