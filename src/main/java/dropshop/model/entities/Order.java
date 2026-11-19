package dropshop.model.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final int id;
    private final int userId;
    private final LocalDateTime date;
    private final String shippingAddress;
    private final List<OrderLine> orderLines;
    private OrderStatus status;             

    public Order(int id, int userId, LocalDateTime date, OrderStatus status,
                 List<OrderLine> orderLines, String shippingAddress) {
        if (orderLines == null || orderLines.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one product");
        }
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.status = status;
        this.orderLines = List.copyOf(orderLines);   
        this.shippingAddress = shippingAddress;
    }

    public int getId()                     { return id; }
    public int getUserId()                 { return userId; }
    public LocalDateTime getDate()         { return date; }
    public OrderStatus getStatus()         { return status; }
    public String getShippingAddress()     { return shippingAddress; }
    public List<OrderLine> getOrderLines() { return orderLines; }

    public double getTotalBillingAmount() {
        double total = 0;
        for (OrderLine line : orderLines) {
            total += line.billingAmount();
        }
        return total;
    }

    public double getTotalBenefitAmount() {
        double total = 0;
        for (OrderLine line : orderLines) {
            total += line.benefitAmount();
        }
        return total;
    }

    public void nextState() {
        switch (status) {
            case PENDING:
                status = OrderStatus.SENT_TO_SUPPLIER;
                break;
            case SENT_TO_SUPPLIER:
                status = OrderStatus.SHIPPED;
                break;
            case SHIPPED:
                status = OrderStatus.DELIVERED;
                break;
            case DELIVERED:
                throw new IllegalStateException("The order has already been delivered.");
            case CANCELLED:
                throw new IllegalStateException("The order has been cancelled.");
        }
    }

    public void cancelOrder() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled.");
        }
        status = OrderStatus.CANCELLED;
    }
}