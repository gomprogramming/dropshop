package dropshop.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.LoginController;
import dropshop.controller.LoginView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginWindow extends BasicWindow implements LoginView {

    private static final String LOGO =
            "╔══════════════════════════╗\n" +
            "║     D R O P S H O P      ║\n" +
            "║   your store, no stock   ║\n" +
            "╚══════════════════════════╝";

    private final WindowBasedTextGUI gui;
    private final TextBox txtUser = new TextBox(new TerminalSize(22, 1));
    private final TextBox txtPass = new TextBox(new TerminalSize(22, 1)).setMask('*');
    private final TextApp app;

    public LoginWindow(LoginController controller, WindowBasedTextGUI gui,TextApp app) {
        super("Log in");
        this.gui = gui;
        this.app = app;
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
        root.addComponent(new Label(LOGO));
        root.addComponent(new EmptySpace());

        Panel form = new Panel(new GridLayout(2));
        form.addComponent(new Label("Username:"));
        form.addComponent(txtUser);
        form.addComponent(new Label("Password:"));
        form.addComponent(txtPass);
        root.addComponent(form);
        root.addComponent(new EmptySpace());

        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Log in",
                () -> controller.login(txtUser.getText(), txtPass.getText())));
        buttons.addComponent(new Button("Exit", this::close));
        root.addComponent(buttons);

        root.addComponent(new EmptySpace());
        root.addComponent(new Label("Tab: next field · Enter: press · Esc: exit"));
        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    close();
                    handled.set(true);
                }
            }
        });
    }

    @Override
    public void showError(String message) {
        MessageDialog.showMessageDialog(gui, "Error", message);
        txtPass.setText("");
        setFocusedInteractable(txtPass);
    }

    @Override
    public void openCustomerArea(String username) {
        app.showCatalog();
        txtPass.setText("");
        setFocusedInteractable(txtUser);
        MessageDialog.showMessageDialog(gui, "Welcome",
                "Hello, " + username + ".\n(The catalog will be the next screen)");
    }

    @Override
    public void openAdminArea(String username) {
        MessageDialog.showMessageDialog(gui, "Welcome",
                "Hello, " + username + " (admin).\n(The admin panel will come later)");
    }
}