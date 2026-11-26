package dropshop.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.StatisticsController;
import dropshop.controller.StatisticsView;
import dropshop.model.ModelListener;
import dropshop.model.entities.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatisticsWindow extends BasicWindow implements StatisticsView, ModelListener {

    private static final int BAR_WIDTH = 30;
    private static final int MAX_PRODUCTS = 8;

    private final StatisticsController controller;

    private final Label lblSummary = new Label("");
    private final Label lblByStatus = new Label("");
    private final Label lblChart = new Label("");

    public StatisticsWindow(StatisticsController controller) {
        super("DropShop - Statistics");
        this.controller = controller;
        setHints(List.of(Hint.FULL_SCREEN));

        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        Panel topRow = new Panel(new LinearLayout(Direction.HORIZONTAL));
        topRow.addComponent(lblSummary.withBorder(Borders.singleLine("Summary")));
        topRow.addComponent(lblByStatus.withBorder(Borders.singleLine("Orders by status")));
        root.addComponent(topRow);

        root.addComponent(new EmptySpace());
        root.addComponent(lblChart.withBorder(Borders.singleLine("Best sellers (units sold)")));
        root.addComponent(new EmptySpace());
        root.addComponent(new Label("Esc: back"));

        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    controller.back();
                    handled.set(true);
                }
            }
        });
    }

    @Override
    public void showSummary(double revenue, double benefit, int totalOrders,
                            Map<OrderStatus, Integer> ordersByStatus) {
        double margin = revenue > 0 ? benefit / revenue * 100 : 0;
        lblSummary.setText(String.format(
                "Revenue:  %10.2f €%nBenefit:  %10.2f €%nMargin:   %10.1f %%%nOrders:   %10d",
                revenue, benefit, margin, totalOrders));

        StringBuilder text = new StringBuilder();
        for (Map.Entry<OrderStatus, Integer> e : ordersByStatus.entrySet()) {
            text.append(String.format("%-18s %3d%n", Texts.status(e.getKey()), e.getValue()));
        }
        lblByStatus.setText(text.toString().stripTrailing());
    }

    @Override
    public void showBestSellers(Map<String, Integer> unitsByProduct) {
        if (unitsByProduct.isEmpty()) {
            lblChart.setText("No sales yet.");
            return;
        }

        int max = 0;
        for (int units : unitsByProduct.values()) max = Math.max(max, units);

        StringBuilder chart = new StringBuilder();
        int shown = 0;
        for (Map.Entry<String, Integer> e : unitsByProduct.entrySet()) {
            if (shown == MAX_PRODUCTS) break;
            int length = Math.max(1, e.getValue() * BAR_WIDTH / max);
            chart.append(String.format("%-22s %s %d%n", e.getKey(), "█".repeat(length), e.getValue()));
            shown++;
        }
        lblChart.setText(chart.toString().stripTrailing());
    }

    @Override
    public void goBack() {
        close();
    }

    @Override
    public void ordersChanged() {
        controller.load();
    }

    @Override public void cartChanged(int itemCount, double total) { }
    @Override public void productsChanged() { }
    @Override public void suppliersChanged() { }
}