package dropshop.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.AdminMenuController;
import dropshop.controller.AdminMenuView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminMenuWindow extends BasicWindow implements AdminMenuView {

    public AdminMenuWindow(AdminMenuController controller, WindowBasedTextGUI gui, TextApp app) {
        super("DropShop - Admin");
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
        root.addComponent(new Label("Logged in as " + controller.getUsername() + " (admin)"));
        root.addComponent(new EmptySpace());

        ActionListBox menu = new ActionListBox(new TerminalSize(32, 5));
        menu.addItem("Manage orders", app::showAdminOrders);
        menu.addItem("Products & stock", app::showProducts);
        menu.addItem("Suppliers", app::showSuppliers);
        menu.addItem("Statistics", app::showStatistics);
        menu.addItem("Log out", controller::logout);
        root.addComponent(menu);

        root.addComponent(new EmptySpace());
        root.addComponent(new Label("Arrows: move · Enter: open · Esc: log out"));
        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    controller.logout();
                    handled.set(true);
                }
            }
        });
    }

    @Override
    public void goBackToLogin() {
        close();
    }
}