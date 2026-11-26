package dropshop.model;

public interface ModelListener {
    void cartChanged(int itemCount, double total);
    void ordersChanged();
    void productsChanged();
    void suppliersChanged();
}
