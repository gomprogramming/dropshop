package dropshop.controller;

import dropshop.model.ShopModel;
import dropshop.model.entities.Order;
import dropshop.model.entities.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CartController {
    private final ShopModel model;
    private CartView view;

    public CartController(ShopModel model) {
        this.model = model;
    }

    public void setView(CartView view) {
        this.view = view;
    }

    public void loadItems() {
        List<CartLine> lines = new ArrayList<>();
        for (Map.Entry<Integer, Integer> item : model.getCartItems().entrySet()) {
            Product p = model.findProductById(item.getKey());
            lines.add(new CartLine(p.getId(), p.getName(), item.getValue(), p.getCostsale()));
        }
        lines.sort(Comparator.comparing(CartLine::productName));
        view.showItems(lines, model.getCartTotal());
    }

    public void increase(int productId) {
        try {
            model.addToCart(productId, 1);
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    public void decrease(int productId) {
        model.removeOneFromCart(productId);
    }

    public void remove(int productId) {
        model.removeFromCart(productId);
    }

    public void placeOrder() {
        try {
            Order order = model.placeOrder();
            view.showOrderPlaced(order.getId(), order.getTotalBillingAmount());
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    public void back() {
        view.goBack();
    }
}