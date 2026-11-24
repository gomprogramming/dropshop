package dropshop.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.CartController;
import dropshop.controller.CartLine;
import dropshop.controller.CartView;
import dropshop.model.ModelListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CartWindow extends BasicWindow implements CartView, ModelListener {

    private final CartController controller;
    private final WindowBasedTextGUI gui;

    private final Table<String> table = new Table<>("Product", "Qty", "Unit price", "Subtotal");
    private final Label lblTotal = new Label("");

    private List<CartLine> shownLines = new ArrayList<>();
    private double currentTotal = 0;

    public CartWindow(CartController controller, WindowBasedTextGUI gui) {
        super("DropShop - Cart");
        this.controller = controller;
        this.gui = gui;
        setHints(List.of(Hint.FULL_SCREEN));

        Panel root = new Panel(new BorderLayout());
        root.addComponent(table, BorderLayout.Location.CENTER);

        Panel bottom = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel actions = new Panel(new LinearLayout(Direction.HORIZONTAL));
        actions.addComponent(lblTotal);
        actions.addComponent(new EmptySpace());
        actions.addComponent(new Button("Place order", this::confirmOrder));
        actions.addComponent(new Button("Back", controller::back));
        bottom.addComponent(actions);
        bottom.addComponent(new Label("+ / -: change quantity · Del: remove · Tab: buttons · Esc: back"));
        root.addComponent(bottom, BorderLayout.Location.BOTTOM);

        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                CartLine selected = selectedLine();
                if (key.getKeyType() == KeyType.Escape) {
                    controller.back();
                    handled.set(true);
                } else if (selected != null && key.getKeyType() == KeyType.Delete) {
                    controller.remove(selected.productId());
                    handled.set(true);
                } else if (selected != null && key.getKeyType() == KeyType.Character) {
                    if (key.getCharacter() == '+') {
                        controller.increase(selected.productId());
                        handled.set(true);
                    } else if (key.getCharacter() == '-') {
                        controller.decrease(selected.productId());
                        handled.set(true);
                    }
                }
            }
        });
    }

    private CartLine selectedLine() {
        int row = table.getSelectedRow();
        return (row >= 0 && row < shownLines.size()) ? shownLines.get(row) : null;
    }

    private void confirmOrder() {
        MessageDialogButton answer = MessageDialog.showMessageDialog(gui, "Confirm order",
                String.format("Place order for %.2f €?", currentTotal),
                MessageDialogButton.Yes, MessageDialogButton.No);
        if (answer == MessageDialogButton.Yes) {
            controller.placeOrder();
        }
    }

    private String[] toRow(CartLine line) {
        return new String[] {
                line.productName(),
                String.valueOf(line.quantity()),
                String.format("%.2f €", line.unitPrice()),
                String.format("%.2f €", line.subtotal())
        };
    }

    @Override
    public void showItems(List<CartLine> lines, double total) {
        TableModel<String> tm = table.getTableModel();

        boolean sameRows = lines.size() == shownLines.size();
        for (int i = 0; sameRows && i < lines.size(); i++) {
            if (lines.get(i).productId() != shownLines.get(i).productId()) sameRows = false;
        }

        if (sameRows) {
            for (int row = 0; row < lines.size(); row++) {
                String[] values = toRow(lines.get(row));
                for (int col = 0; col < values.length; col++) {
                    if (!values[col].equals(tm.getCell(col, row))) {
                        tm.setCell(col, row, values[col]);
                    }
                }
            }
        } else {
            tm.clear();
            for (CartLine line : lines) tm.addRow(toRow(line));
        }
        shownLines = lines;
        currentTotal = total;

        lblTotal.setText(lines.isEmpty()
                ? "Your cart is empty"
                : String.format("Total: %.2f €", total));
    }

    @Override
    public void showError(String message) {
        MessageDialog.showMessageDialog(gui, "Error", message);
    }

    @Override
    public void showOrderPlaced(int orderId, double total) {
        MessageDialog.showMessageDialog(gui, "Order placed",
                String.format("Order #%d placed for %.2f €.%nThe supplier will ship it soon.", orderId, total));
        close();
    }

    @Override
    public void goBack() {
        close();
    }



    @Override
    public void cartChanged(int itemCount, double total) {
        controller.loadItems();
    }

    @Override public void productsChanged() { }
    @Override public void ordersChanged()   { }
}