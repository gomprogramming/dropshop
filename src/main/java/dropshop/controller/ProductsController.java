package dropshop.controller;

import dropshop.model.ShopModel;

public class ProductsController {
    private final ShopModel model;
    private ProductsView view;

    public ProductsController(ShopModel model) {
        this.model = model;
    }

    public void setView(ProductsView view) {
        this.view = view;
    }

    public void loadProducts() {
        view.showProducts(model.getProducts());
    }

    public boolean changePrice(int productId, String text) {
        try {
            model.updatePrice(productId, parseMoney(text));
            return true;
        } catch (NumberFormatException e) {
            view.showError("The price must be a number, e.g. 19.90");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
        return false;
    }

    public boolean changeStock(int productId, String text) {
        try {
            model.updateStock(productId, Integer.parseInt(text.trim()));
            return true;
        } catch (NumberFormatException e) {
            view.showError("The stock must be a whole number, e.g. 25");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
        return false;
    }

    public boolean addProduct(String name, String description, String category,
                              String costText, String priceText, String stockText) {
        try {
            model.addProduct(name, description, category,
                    parseMoney(costText), parseMoney(priceText), Integer.parseInt(stockText.trim()));
            return true;
        } catch (NumberFormatException e) {
            view.showError("Cost and price must be numbers (e.g. 9.50) and stock a whole number");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
        return false;
    }

    public void back() {
        view.goBack();
    }

    private double parseMoney(String text) {
        return Double.parseDouble(text.trim().replace(',', '.'));
    }
}