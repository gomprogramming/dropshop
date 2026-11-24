package dropshop.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dropshop.controller.CatalogController;
import dropshop.controller.CatalogView;
import dropshop.model.ModelListener;
import dropshop.model.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CatalogWindow extends BasicWindow implements CatalogView, ModelListener {

    private final CatalogController controller;
    private final WindowBasedTextGUI gui;

    private final TextBox txtSearch = new TextBox(new TerminalSize(25, 1));
    private final ComboBox<String> cmbCategory = new ComboBox<>();
    private final Table<String> table = new Table<>("Product", "Category", "Price", "Stock", "Rating");
    private final Label lblCart = new Label("");

    private List<Product> shownProducts = new ArrayList<>();

    public CatalogWindow(CatalogController controller, WindowBasedTextGUI gui, TextApp app) {
        super("DropShop - Catalog");
        this.controller = controller;
        this.gui = gui;
        setHints(List.of(Hint.FULL_SCREEN));

        Panel root = new Panel(new BorderLayout());

        Panel top = new Panel(new LinearLayout(Direction.HORIZONTAL));
        top.addComponent(new Label("Hi, " + controller.getUsername() + "  |  Search:"));
        top.addComponent(txtSearch);
        top.addComponent(new Label("  Category:"));
        top.addComponent(cmbCategory);
        root.addComponent(top, BorderLayout.Location.TOP);

        table.setSelectAction(this::addSelectedToCart);
        root.addComponent(table, BorderLayout.Location.CENTER);

        Panel bottom = new Panel(new LinearLayout(Direction.VERTICAL));
        bottom.addComponent(lblCart);
        bottom.addComponent(new Label("Arrows: move · Enter: add to cart · C: view cart · O: view orders · Tab: switch area · Esc: log out"));
        root.addComponent(bottom, BorderLayout.Location.BOTTOM);

        setComponent(root);
        updateCartLabel(controller.getCartItemCount(), controller.getCartTotal());

        txtSearch.setTextChangeListener((newText, changedByUser) -> doSearch());
        cmbCategory.addListener((selected, previous, changedByUser) -> {
            if (changedByUser) doSearch();
        });

        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window w, KeyStroke key, AtomicBoolean handled) {
                if (key.getKeyType() == KeyType.Escape) {
                    controller.logout();
                    handled.set(true);
                } else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'c') {
                app.showCart();
                    handled.set(true);
                } else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'o') {
                    app.showMyOrders();
                    handled.set(true);
                }
            }
        });
    }

    private void doSearch() {
        String category = cmbCategory.getSelectedItem();
        controller.search(txtSearch.getText(), category == null ? "All" : category);
    }

    private void addSelectedToCart() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < shownProducts.size()) {
            controller.addToCart(shownProducts.get(row).getId());
        }
    }

    private void updateCartLabel(int itemCount, double total) {
        lblCart.setText(String.format("Cart: %d items · %.2f €", itemCount, total));
    }

    private String[] toRow(Product p) {
        String rating = p.getRatingCount() == 0 ? "-" : String.format("%.1f/5", p.getRating());
        return new String[] {
                p.getName(),
                p.getCategory(),
                String.format("%.2f €", p.getCostsale()),
                String.valueOf(p.getStock()),
                rating
        };
    }

    @Override
    public void showCategories(List<String> categories) {
        cmbCategory.clearItems();
        for (String c : categories) cmbCategory.addItem(c);
    }

    @Override
    public void showProducts(List<Product> products) {
        TableModel<String> tm = table.getTableModel();

        boolean sameRows = products.size() == shownProducts.size();
        for (int i = 0; sameRows && i < products.size(); i++) {
            if (products.get(i).getId() != shownProducts.get(i).getId()) sameRows = false;
        }

        if (sameRows) {
            for (int row = 0; row < products.size(); row++) {
                String[] values = toRow(products.get(row));
                for (int col = 0; col < values.length; col++) {
                    if (!values[col].equals(tm.getCell(col, row))) {
                        tm.setCell(col, row, values[col]);
                    }
                }
            }
        } else {
            tm.clear();
            for (Product p : products) tm.addRow(toRow(p));
        }
        shownProducts = products;
    }

    @Override
    public void showError(String message) {
        MessageDialog.showMessageDialog(gui, "Error", message);
    }

    @Override
    public void goBackToLogin() {
        close();
    }

    @Override
    public void cartChanged(int itemCount, double total) {
        updateCartLabel(itemCount, total);
    }

    @Override
    public void productsChanged() {
        controller.refreshProducts();
    }

    @Override
    public void ordersChanged() { }
}