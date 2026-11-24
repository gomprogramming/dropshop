package dropshop.controller;

import dropshop.model.ShopModel;

public class OrdersController {
    private final ShopModel model;
    private OrdersView view;

    public OrdersController(ShopModel model) {
        this.model = model;
    }

    public void setView(OrdersView view) {
        this.view = view;
    }

    public void loadOrders() {
        view.showOrders(model.getMyOrders());
    }

    public void cancel(int orderId) {
        try {
            model.cancelMyOrder(orderId);
            view.showMessage("Order #" + orderId + " has been cancelled.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    public void back() {
        view.goBack();
    }
}