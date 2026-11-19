package dropshop.controller;

public interface LoginView {
    void showError(String message);
    void openCustomerArea(String username);
    void openAdminArea(String username);
}