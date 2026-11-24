package dropshop.controller;

public record CartLine(int productId, String productName, int quantity, double unitPrice) {
    public double subtotal() {
        return unitPrice * quantity;
    }
}