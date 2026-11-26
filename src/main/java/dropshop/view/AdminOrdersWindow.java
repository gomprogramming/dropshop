package dropshop.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.AdminOrdersController;
import dropshop.controller.AdminOrdersView;
import dropshop.model.ModelListener;
import dropshop.model.entities.Order;
import dropshop.model.entities.OrderLine;
import dropshop.model.entities.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminOrdersWindow extends BasicWindow implements AdminOrdersView, ModelListener {

    private final AdminOrdersController controller;
    private final WindowBasedTextGUI gui;

    private final Table<String> table =
            new Table<>("Order", "Date", "Customer", "Items", "Total", "Profit", "Status");
    private final Label lblInfo = new Label("");

    private List<Order> shownOrders = new ArrayList<>();

    public AdminOrdersWindow(AdminOrdersController controller, WindowBasedTextGUI gui) {
        super("DropShop - Manage orders");
        this.controller = controller;
        this.gui = gui;
        setHints(List.of(Hint.FULL_SCREEN));

        Panel root = new Panel(new BorderLayout());

        table.setSelectAction(this::showDetailsOfSelected);
        root.addComponent(table, BorderLayout.Location.CENTER);

        Panel bottom = new Panel(new LinearLayout(Direction.VERTICAL));
        bottom.addComponent(lblInfo);
        bottom.addComponent(new Label("Enter: details · A: advance to next stage · Esc: back"));
        root.addComponent(bottom, BorderLayout.Location.BOTTOM);

        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    controller.back();
                    handled.set(true);
                } else if (key.getKeyType() == KeyType.Character
                        && Character.toLowerCase(key.getCharacter()) == 'a') {
                    confirmAdvanceSelected();
                    handled.set(true);
                }
            }
        });
    }

    private Order selectedOrder() {
        int row = table.getSelectedRow();
        return (row >= 0 && row < shownOrders.size()) ? shownOrders.get(row) : null;
    }

    private void confirmAdvanceSelected() {
        Order o = selectedOrder();
        if (o == null) return;
        MessageDialogButton answer = MessageDialog.showMessageDialog(gui, "Advance order",
                "Order #" + o.getId() + " is now: " + Texts.status(o.getStatus())
                        + "\nMove it to the next stage?",
                MessageDialogButton.Yes, MessageDialogButton.No);
        if (answer == MessageDialogButton.Yes) {
            controller.advance(o.getId());
        }
    }

    private void showDetailsOfSelected() {
        Order o = selectedOrder();
        if (o == null) return;

        StringBuilder text = new StringBuilder();
        text.append("Customer: ").append(controller.getCustomerName(o)).append("\n");
        text.append("Ship to:  ").append(o.getShippingAddress()).append("\n\n");
        for (OrderLine line : o.getOrderLines()) {
            text.append(String.format("%d x %-22s %8.2f €%n",
                    line.getQuantity(), line.getProductName(), line.billingAmount()));
        }
        text.append(String.format("%nTotal:   %.2f €%n", o.getTotalBillingAmount()));
        text.append(String.format("Profit:  %.2f €%n", o.getTotalProfitAmount()));
        text.append("Status:  ").append(Texts.status(o.getStatus()));

        MessageDialog.showMessageDialog(gui, "Order #" + o.getId(), text.toString());
    }

    private String[] toRow(Order o) {
        int items = 0;
        for (OrderLine line : o.getOrderLines()) items += line.getQuantity();
        return new String[] {
                "#" + o.getId(),
                o.getDate().format(Texts.DATE),
                controller.getCustomerName(o),
                String.valueOf(items),
                String.format("%.2f €", o.getTotalBillingAmount()),
                String.format("%.2f €", o.getTotalProfitAmount()),
                Texts.status(o.getStatus())
        };
    }

    @Override
    public void showOrders(List<Order> orders) {
        TableModel<String> tm = table.getTableModel();

        boolean sameRows = orders.size() == shownOrders.size();
        for (int i = 0; sameRows && i < orders.size(); i++) {
            if (orders.get(i).getId() != shownOrders.get(i).getId()) sameRows = false;
        }

        if (sameRows) {
            for (int row = 0; row < orders.size(); row++) {
                String[] values = toRow(orders.get(row));
                for (int col = 0; col < values.length; col++) {
                    if (!values[col].equals(tm.getCell(col, row))) {
                        tm.setCell(col, row, values[col]);
                    }
                }
            }
        } else {
            tm.clear();
            for (Order o : orders) tm.addRow(toRow(o));
        }
        shownOrders = orders;

        int pending = 0;
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.PENDING) pending++;
        }
        lblInfo.setText(orders.isEmpty()
                ? "No orders yet."
                : orders.size() + " order(s) · " + pending + " pending to send to the supplier");
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
    public void ordersChanged() {
        controller.loadOrders();
    }

    @Override public void cartChanged(int itemCount, double total) { }
    @Override public void productsChanged() { }
    @Override public void suppliersChanged() { }
}