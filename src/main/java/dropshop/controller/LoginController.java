package dropshop.controller;

import dropshop.model.ShopModel;
import dropshop.model.entities.User;

public class LoginController {
    private final ShopModel model;
    private LoginView view;

    public LoginController(ShopModel model) {
        this.model = model;
    }

    public void setView(LoginView view) {
        this.view = view;
    }

    public void login(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            view.showError("Enter your username and password");
            return;
        }
        try {
            User user = model.login(username, password);
            if (user.isAdmin()) {
                view.openAdminArea(user.getUsername());
            } else {
                view.openCustomerArea(user.getUsername());
            }
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }
}