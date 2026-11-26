package dropshop.controller;

import dropshop.model.ShopModel;

public class SuppliersController {
    private final ShopModel model;
    private SuppliersView view;

    public SuppliersController(ShopModel model) {
        this.model = model;
    }

    public void setView(SuppliersView view) {
        this.view = view;
    }

    public void loadSuppliers() {
        view.showSuppliers(model.getSuppliers());
    }

    public boolean addSupplier(String name, String email, String address, String daysText) {
        try {
            model.addSupplier(name, email, address, Integer.parseInt(daysText.trim()));
            return true;
        } catch (NumberFormatException e) {
            view.showError("Shipping days must be a whole number, e.g. 5");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
        return false;
    }

    public void back() {
        view.goBack();
    }
}