package dropshop;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dropshop.model.ShopModel;
import dropshop.view.TextApp;

public class Main {
    public static void main(String[] args) throws Exception {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        try {
            ShopModel model = new ShopModel();
            new TextApp(model, gui).start();
        } finally {
            screen.stopScreen();
        }
    }
}
