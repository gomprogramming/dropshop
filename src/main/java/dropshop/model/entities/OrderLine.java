package dropshop.model.entities;

public class OrderLine {
    private int productId;
    private int quantity;
    private String productName;
    private double productPrice;
    private double productCost;

    public OrderLine(int productId, int quantity, String productName, double productPrice, double productCost) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productCost = productCost;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
    public String getProductName() {
        return productName;
    }   

    public double getProductPrice() {
        return productPrice;
    }
    public double getProductCost() {
        return productCost;
    }   

    public double billingAmount() {
        return productPrice * quantity;
    }
    public double benefitAmount() {
        return (productPrice - productCost) * quantity;
    }

    
}
