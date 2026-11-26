package dropshop.controller;

import dropshop.model.entities.Product;
import java.util.List;

public interface ProductsView {
    void showProducts(List<Product> products);
    void showError(String message);
    void goBack();
}