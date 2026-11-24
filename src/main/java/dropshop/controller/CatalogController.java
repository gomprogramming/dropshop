package dropshop.controller;

import dropshop.model.ShopModel;
import java.util.ArrayList;
import java.util.List;

public class CatalogController {
    private final ShopModel model;
    private CatalogView view;
    private String currentText = "";
    private String currentCategory = "All";

    public CatalogController(ShopModel model) {
        this.model = model;
    }

    public void setView(CatalogView view) {
        this.view = view;
    }

    public void loadInitial() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.addAll(model.getCategories());
        view.showCategories(categories);
        refreshProducts();
    }

    public void search(String text, String category) {
        currentText = text;
        currentCategory = category;
        refreshProducts();
    }

    public void refreshProducts() {
        view.showProducts(model.searchProducts(currentText, currentCategory));
    }

    public void addToCart(int productId) {
        try {
            model.addToCart(productId, 1);
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    public void logout() {
        model.logout();
        view.goBackToLogin();
    }

    public String getUsername()     { return model.getCurrentUser().getUsername(); }
    public int getCartItemCount()   { return model.getCartItemCount(); }
    public double getCartTotal()    { return model.getCartTotal(); }
}