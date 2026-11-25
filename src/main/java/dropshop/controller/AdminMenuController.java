package dropshop.controller;

import dropshop.model.ShopModel;

public class AdminMenuController {
    private final ShopModel model;
    private AdminMenuView view;

    public AdminMenuController(ShopModel model) {
        this.model = model;
    }

    public void setView(AdminMenuView view) {
        this.view = view;
    }

    public String getUsername() {
        return model.getCurrentUser().getUsername();
    }

    public void logout() {
        model.logout();
        view.goBackToLogin();
    }
}