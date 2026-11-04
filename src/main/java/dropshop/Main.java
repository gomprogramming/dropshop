package dropshop;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class Main {
    public static void main(String[] args) throws Exception {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        BasicWindow ventana = new BasicWindow("DropShop");
        Panel panel = new Panel();
        panel.addComponent(new Label("¡Bienvenido a DropShop!"));
        panel.addComponent(new Button("Salir", ventana::close));
        ventana.setComponent(panel);

        gui.addWindowAndWait(ventana);
        screen.stopScreen();
    }
}