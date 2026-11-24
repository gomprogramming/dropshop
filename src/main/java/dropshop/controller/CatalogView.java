package dropshop.controller;

import dropshop.model.entities.Product;
import java.util.List;

public interface CatalogView {
    void showCategories(List<String> categories);
    void showProducts(List<Product> products);
    void showError(String message);
    void goBackToLogin();
}