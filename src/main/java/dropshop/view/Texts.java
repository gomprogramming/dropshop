package dropshop.view;

import dropshop.model.entities.OrderStatus;
import java.time.format.DateTimeFormatter;

public final class Texts {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Texts() { }

    public static String status(OrderStatus status) {
        return switch (status) {
            case PENDING          -> "Pending";
            case SENT_TO_SUPPLIER -> "Sent to supplier";
            case SHIPPED          -> "Shipped";
            case DELIVERED        -> "Delivered";
            case CANCELLED        -> "Cancelled";
        };
    }
}