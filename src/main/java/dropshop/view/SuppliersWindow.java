package dropshop.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.SuppliersController;
import dropshop.controller.SuppliersView;
import dropshop.model.ModelListener;
import dropshop.model.entities.Supplier;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SuppliersWindow extends BasicWindow implements SuppliersView, ModelListener {

    private final SuppliersController controller;
    private final WindowBasedTextGUI gui;

    private final Table<String> table =
            new Table<>("ID", "Name", "Email", "Address", "Shipping days");

    public SuppliersWindow(SuppliersController controller, WindowBasedTextGUI gui) {
        super("DropShop - Suppliers");
        this.controller = controller;
        this.gui = gui;
        setHints(List.of(Hint.FULL_SCREEN));

        Panel root = new Panel(new BorderLayout());
        root.addComponent(table, BorderLayout.Location.CENTER);
        root.addComponent(new Label("N: new supplier · Esc: back"), BorderLayout.Location.BOTTOM);
        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    controller.back();
                    handled.set(true);
                } else if (key.getKeyType() == KeyType.Character
                        && Character.toLowerCase(key.getCharacter()) == 'n') {
                    newSupplier();
                    handled.set(true);
                }
            }
        });
    }

    private void newSupplier() {
        String name = TextInputDialog.showDialog(gui, "New supplier", "Name", "");
        if (name == null) return;
        String email = TextInputDialog.showDialog(gui, "New supplier", "Email", "");
        if (email == null) return;
        String address = TextInputDialog.showDialog(gui, "New supplier", "Address", "");
        if (address == null) return;
        String days = TextInputDialog.showDialog(gui, "New supplier", "Shipping days", "");
        if (days == null) return;
        controller.addSupplier(name, email, address, days);
    }

    @Override
    public void showSuppliers(List<Supplier> suppliers) {
        TableModel<String> tm = table.getTableModel();
        tm.clear();
        for (Supplier s : suppliers) {
            tm.addRow(String.valueOf(s.getId()), s.getName(), s.getEmail(), s.getAddress(),
                    String.valueOf(s.getDaystoDeliver()));
        }
    }

    @Override
    public void showError(String message) {
        MessageDialog.showMessageDialog(gui, "Error", message);
    }

    @Override
    public void goBack() {
        close();
    }

    @Override
    public void suppliersChanged() {
        controller.loadSuppliers();
    }

    @Override public void productsChanged() { }
    @Override public void cartChanged(int itemCount, double total) { }
    @Override public void ordersChanged() { }
}
