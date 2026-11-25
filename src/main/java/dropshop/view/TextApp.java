package dropshop.view;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import dropshop.controller.AdminMenuController;
import dropshop.controller.AdminOrdersController;
import dropshop.controller.CartController;
import dropshop.controller.CatalogController;
import dropshop.controller.LoginController;
import dropshop.controller.OrdersController;
import dropshop.model.ShopModel;

public class TextApp {
    private final ShopModel model;
    private final WindowBasedTextGUI gui;

    public TextApp(ShopModel model, WindowBasedTextGUI gui) {
        this.model = model;
        this.gui = gui;
    }

    public void start() {
        LoginController controller = new LoginController(model);
        LoginWindow window = new LoginWindow(controller, gui, this);
        controller.setView(window);
        gui.addWindowAndWait(window);
    }

    public void showCatalog() {
        CatalogController controller = new CatalogController(model);
        CatalogWindow window = new CatalogWindow(controller, gui, this);
        controller.setView(window);
        model.addListener(window);
        controller.loadInitial();
        gui.addWindowAndWait(window);
        model.removeListener(window);
    }
    public void showCart() {
        CartController controller = new CartController(model);
        CartWindow window = new CartWindow(controller, gui);
        controller.setView(window);
        model.addListener(window);
        controller.loadItems();
        gui.addWindowAndWait(window);
        model.removeListener(window);
    }
    public void showMyOrders() {
        OrdersController controller = new OrdersController(model);
        OrdersWindow window = new OrdersWindow(controller, gui);
        controller.setView(window);
        model.addListener(window);       controller.loadOrders();
        gui.addWindowAndWait(window);
        model.removeListener(window);
    }
    public void showAdminMenu() {
        AdminMenuController controller = new AdminMenuController(model);
        AdminMenuWindow window = new AdminMenuWindow(controller, gui, this);
        controller.setView(window);
        gui.addWindowAndWait(window);
    }

    public void showAdminOrders() {
        AdminOrdersController controller = new AdminOrdersController(model);
        AdminOrdersWindow window = new AdminOrdersWindow(controller, gui);
        controller.setView(window);
        model.addListener(window);
        controller.loadOrders();
        gui.addWindowAndWait(window);
        model.removeListener(window);
    }
}