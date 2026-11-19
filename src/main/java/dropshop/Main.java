package dropshop;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dropshop.controller.LoginController;
import dropshop.model.ShopModel;
import dropshop.view.LoginWindow;

public class Main {
    public static void main(String[] args) throws Exception {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        try {
            ShopModel model = new ShopModel();

            LoginController loginController = new LoginController(model);
            LoginWindow loginWindow = new LoginWindow(loginController, gui);
            loginController.setView(loginWindow);

            gui.addWindowAndWait(loginWindow);
        } finally {
            screen.stopScreen();
        }
    }
}