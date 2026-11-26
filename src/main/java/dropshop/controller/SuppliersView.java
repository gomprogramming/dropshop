package dropshop.controller;

import dropshop.model.entities.Supplier;
import java.util.List;

public interface SuppliersView {
    void showSuppliers(List<Supplier> suppliers);
    void showError(String message);
    void goBack();
}