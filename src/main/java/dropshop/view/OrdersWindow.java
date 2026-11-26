package dropshop.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.OrdersController;
import dropshop.controller.OrdersView;
import dropshop.model.ModelListener;
import dropshop.model.entities.Order;
import dropshop.model.entities.OrderLine;
import dropshop.model.entities.OrderStatus;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrdersWindow extends BasicWindow implements OrdersView, ModelListener {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final OrdersController controller;
    private final WindowBasedTextGUI gui;

    private final Table<String> table = new Table<>("Order", "Date", "Items", "Total", "Status");
    private final Label lblInfo = new Label("");

    private List<Order> shownOrders = new ArrayList<>();

    public OrdersWindow(OrdersController controller, WindowBasedTextGUI gui) {
        super("DropShop - My orders");
        this.controller = controller;
        this.gui = gui;
        setHints(List.of(Hint.FULL_SCREEN));

        Panel root = new Panel(new BorderLayout());

        table.setSelectAction(this::showDetailsOfSelected);
        root.addComponent(table, BorderLayout.Location.CENTER);

        Panel bottom = new Panel(new LinearLayout(Direction.VERTICAL));
        bottom.addComponent(lblInfo);
        bottom.addComponent(new Label("Enter: details · X: cancel order · Esc: back"));
        root.addComponent(bottom, BorderLayout.Location.BOTTOM);

        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    controller.back();
                    handled.set(true);
                } else if (key.getKeyType() == KeyType.Character
                        && Character.toLowerCase(key.getCharacter()) == 'x') {
                    confirmCancelSelected();
                    handled.set(true);
                }
            }
        });
    }

    private Order selectedOrder() {
        int row = table.getSelectedRow();
        return (row >= 0 && row < shownOrders.size()) ? shownOrders.get(row) : null;
    }

    private void showDetailsOfSelected() {
        Order o = selectedOrder();
        if (o == null) return;

        StringBuilder text = new StringBuilder();
        for (OrderLine line : o.getOrderLines()) {
            text.append(String.format("%d x %-22s %8.2f €%n",
                    line.getQuantity(), line.getProductName(), line.billingAmount()));
        }
        text.append(String.format("%nTotal: %.2f €%n", o.getTotalBillingAmount()));
        text.append("Status: ").append(statusText(o.getStatus())).append("\n");
        text.append("Ship to: ").append(o.getShippingAddress());

        MessageDialog.showMessageDialog(gui, "Order #" + o.getId(), text.toString());
    }

    private void confirmCancelSelected() {
        Order o = selectedOrder();
        if (o == null) return;
        MessageDialogButton answer = MessageDialog.showMessageDialog(gui, "Cancel order",
                "Cancel order #" + o.getId() + "?",
                MessageDialogButton.Yes, MessageDialogButton.No);
        if (answer == MessageDialogButton.Yes) {
            controller.cancel(o.getId());
        }
    }

    private String statusText(OrderStatus status) {
        return switch (status) {
            case PENDING          -> "Pending";
            case SENT_TO_SUPPLIER -> "Sent to supplier";
            case SHIPPED          -> "Shipped";
            case DELIVERED        -> "Delivered";
            case CANCELLED        -> "Cancelled";
        };
    }

    private String[] toRow(Order o) {
        int items = 0;
        for (OrderLine line : o.getOrderLines()) items += line.getQuantity();
        return new String[] {
                "#" + o.getId(),
                o.getDate().format(DATE_FORMAT),
                String.valueOf(items),
                String.format("%.2f €", o.getTotalBillingAmount()),
                statusText(o.getStatus())
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

        lblInfo.setText(orders.isEmpty()
                ? "You have no orders yet."
                : orders.size() + " order(s)");
    }

    @Override
    public void showMessage(String message) {
        MessageDialog.showMessageDialog(gui, "Info", message);
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