package dropshop.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.ProductsController;
import dropshop.controller.ProductsView;
import dropshop.model.ModelListener;
import dropshop.model.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductsWindow extends BasicWindow implements ProductsView, ModelListener {

    private final ProductsController controller;
    private final WindowBasedTextGUI gui;

    private final Table<String> table =
            new Table<>("ID", "Name", "Category", "Cost", "Price", "Stock", "Sold");

    private List<Product> shownProducts = new ArrayList<>();

    public ProductsWindow(ProductsController controller, WindowBasedTextGUI gui) {
        super("DropShop - Products & stock");
        this.controller = controller;
        this.gui = gui;
        setHints(List.of(Hint.FULL_SCREEN));

        Panel root = new Panel(new BorderLayout());
        root.addComponent(table, BorderLayout.Location.CENTER);
        root.addComponent(new Label("P: change price · S: change stock · N: new product · Esc: back"),
                BorderLayout.Location.BOTTOM);
        setComponent(root);

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    controller.back();
                    handled.set(true);
                } else if (key.getKeyType() == KeyType.Character) {
                    switch (Character.toLowerCase(key.getCharacter())) {
                        case 'p' -> { changePriceOfSelected(); handled.set(true); }
                        case 's' -> { changeStockOfSelected(); handled.set(true); }
                        case 'n' -> { newProduct(); handled.set(true); }
                        default -> { }
                    }
                }
            }
        });
    }

    private Product selectedProduct() {
        int row = table.getSelectedRow();
        return (row >= 0 && row < shownProducts.size()) ? shownProducts.get(row) : null;
    }

    private void changePriceOfSelected() {
        Product p = selectedProduct();
        if (p == null) return;
        String text = TextInputDialog.showDialog(gui, "Change price",
                p.getName() + " (cost " + String.format("%.2f €", p.getCostproduction()) + ")",
                String.format("%.2f", p.getCostsale()));
        if (text != null) controller.changePrice(p.getId(), text);
    }

    private void changeStockOfSelected() {
        Product p = selectedProduct();
        if (p == null) return;
        String text = TextInputDialog.showDialog(gui, "Change stock", p.getName(),
                String.valueOf(p.getStock()));
        if (text != null) controller.changeStock(p.getId(), text);
    }

    private void newProduct() {
        String name = TextInputDialog.showDialog(gui, "New product", "Name", "");
        if (name == null) return;
        String description = TextInputDialog.showDialog(gui, "New product", "Description", "");
        if (description == null) return;
        String category = TextInputDialog.showDialog(gui, "New product", "Category", "");
        if (category == null) return;
        String cost = TextInputDialog.showDialog(gui, "New product", "Cost", "");
        if (cost == null) return;
        String price = TextInputDialog.showDialog(gui, "New product", "Price", "");
        if (price == null) return;
        String stock = TextInputDialog.showDialog(gui, "New product", "Stock", "");
        if (stock == null) return;
        controller.addProduct(name, description, category, cost, price, stock);
    }

    private String[] toRow(Product p) {
        return new String[] {
                String.valueOf(p.getId()),
                p.getName(),
                p.getCategory(),
                String.format("%.2f €", p.getCostproduction()),
                String.format("%.2f €", p.getCostsale()),
                String.valueOf(p.getStock()),
                String.valueOf(p.getNumsales())
        };
    }

    @Override
    public void showProducts(List<Product> products) {
        TableModel<String> tm = table.getTableModel();
        int selected = table.getSelectedRow();
        tm.clear();
        for (Product p : products) tm.addRow(toRow(p));
        shownProducts = new ArrayList<>(products);
        if (selected >= 0 && selected < products.size()) table.setSelectedRow(selected);
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
    public void productsChanged() {
        controller.loadProducts();
    }

    @Override public void cartChanged(int itemCount, double total) { }
    @Override public void ordersChanged() { }
    @Override public void suppliersChanged() { }
}
