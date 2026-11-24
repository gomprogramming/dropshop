package dropshop.model;

import dropshop.model.entities.*;
import java.time.LocalDateTime;
import java.util.*;

public class ShopModel {

    private final List<Product> products = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<ModelListener> listeners = new ArrayList<>();

    private User currentUser = null;
    private final Cart cart = new Cart();

    private int nextUserId = 1;
    private int nextProductId = 1;
    private int nextSupplierId = 1;
    private int nextOrderId = 1;

    public ShopModel() {
        loadSampleData();
    }

    private void loadSampleData() {
        users.add(new User(nextUserId++, "admin", PasswordHasher.hash("admin123"),
                "admin@dropshop.com", "Head office", User.Role.ADMIN));
        users.add(new User(nextUserId++, "customer", PasswordHasher.hash("customer123"),
                "customer@mail.com", "1 Main Street, Madrid", User.Role.CUSTOMER));

        suppliers.add(new Supplier(nextSupplierId++, "ShenTech Ltd.",
                "sales@shentech.example", "Shenzhen, China", 12));
        suppliers.add(new Supplier(nextSupplierId++, "EuroHome GmbH",
                "orders@eurohome.example", "Berlin, Germany", 4));

        addSampleProduct("X2 Headphones", "Bluetooth 5.3, 30 h battery life", "Electronics", 9.50, 24.99, 40);
        addSampleProduct("65W USB-C Charger", "GaN fast charging", "Electronics", 7.00, 19.90, 25);
        addSampleProduct("Sports Watch", "Heart rate monitor and GPS", "Electronics", 18.00, 49.90, 10);
        addSampleProduct("LED Lamp", "Dimmable, 3 light tones", "Home", 6.50, 17.99, 15);
        addSampleProduct("Cable Organizer", "Pack of 10", "Home", 1.20, 6.99, 60);
        addSampleProduct("Thermal Bottle", "Stainless steel, 750 ml", "Sports", 4.00, 14.50, 30);
        addSampleProduct("Yoga Mat", "Non-slip, 6 mm", "Sports", 5.50, 21.00, 8);
    }

    private void addSampleProduct(String name, String description, String category,
                                  double cost, double price, int stock) {
        products.add(new Product(nextProductId++, name, description, category,cost, price, 0, 0, 0, 0, stock));
    }

    public void addListener(ModelListener l)    { listeners.add(l); }
    public void removeListener(ModelListener l) { listeners.remove(l); }

    private void notifyCartChanged() {
        for (ModelListener l : List.copyOf(listeners)) l.cartChanged(getCartItemCount(), getCartTotal());
    }
    private void notifyOrdersChanged() {
        for (ModelListener l : List.copyOf(listeners)) l.ordersChanged();
    }
    private void notifyProductsChanged() {
        for (ModelListener l : List.copyOf(listeners)) l.productsChanged();
    }

    public User login(String username, String password) {
        String hash = PasswordHasher.hash(password);
        for (User u : users) {
            if (u.getUsername().equals(username) && u.checkPassword(hash)) {
                currentUser = u;
                cart.clearCart();
                notifyCartChanged();
                return u;
            }
        }
        throw new IllegalArgumentException("Incorrect username or password");
    }

    public void logout() {
        currentUser = null;
        cart.clearCart();
        notifyCartChanged();
    }

    public User register(String username, String password, String email, String address) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email");
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("Address cannot be empty");
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username))
                throw new IllegalArgumentException("That username already exists");
        }
        User newUser = new User(nextUserId++, username, PasswordHasher.hash(password),
                email, address, User.Role.CUSTOMER);
        users.add(newUser);
        return newUser;
    }

    public User getCurrentUser() { return currentUser; }
    public boolean isLoggedIn()  { return currentUser != null; }

    private void requireLogin() {
        if (currentUser == null) throw new IllegalStateException("You must log in");
    }

    private void requireAdmin() {
        requireLogin();
        if (!currentUser.isAdmin())
            throw new IllegalStateException("Only the administrator can do this");
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public Product findProductById(int id) {
        for (Product p : products) {
            if (p.getId() == id) return p;
        }
        throw new IllegalArgumentException("Product not found: " + id);
    }

    public List<Product> searchProducts(String text, String category) {
        String query = (text == null) ? "" : text.toLowerCase();
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            boolean nameMatches = p.getName().toLowerCase().contains(query);
            boolean categoryMatches = category == null || category.equals("All")
                    || p.getCategory().equals(category);
            if (nameMatches && categoryMatches) result.add(p);
        }
        return result;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (Product p : products) {
            if (!categories.contains(p.getCategory())) categories.add(p.getCategory());
        }
        return categories;
    }

    public void addToCart(int productId, int quantity) {
        requireLogin();
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        Product p = findProductById(productId);
        int alreadyInCart = cart.getItems().getOrDefault(productId, 0);
        if (alreadyInCart + quantity > p.getStock()) {
            throw new IllegalStateException("Only " + p.getStock() + " units left of " + p.getName());
        }
        cart.addItem(productId, quantity);
        notifyCartChanged();
    }

    public void removeOneFromCart(int productId) {
        cart.removeoneItem(productId);
        notifyCartChanged();
    }

    public void removeFromCart(int productId) {
        cart.removewholeItem(productId);
        notifyCartChanged();
    }

    public void clearCart() {
        cart.clearCart();
        notifyCartChanged();
    }

    public Map<Integer, Integer> getCartItems() {
        return cart.getItems();
    }

    public double getCartTotal() {
        double total = 0;
        for (Map.Entry<Integer, Integer> item : cart.getItems().entrySet()) {
            total += findProductById(item.getKey()).getCostsale() * item.getValue();
        }
        return total;
    }

    public int getCartItemCount() {
        int count = 0;
        for (int q : cart.getItems().values()) count += q;
        return count;
    }

    public Order placeOrder() {
        requireLogin();
        Map<Integer, Integer> items = cart.getItems();
        if (items.isEmpty()) throw new IllegalStateException("The cart is empty");

        for (Map.Entry<Integer, Integer> item : items.entrySet()) {
            Product p = findProductById(item.getKey());
            if (item.getValue() > p.getStock())
                throw new IllegalStateException("Not enough stock of " + p.getName());
        }

        List<OrderLine> lines = new ArrayList<>();
        for (Map.Entry<Integer, Integer> item : items.entrySet()) {
            Product p = findProductById(item.getKey());
            int qty = item.getValue();
            p.sell(qty);
            lines.add(new OrderLine(p.getId(), qty, p.getName(), p.getCostsale(), p.getCostproduction()));
        }

        Order order = new Order(nextOrderId++, currentUser.getId(), LocalDateTime.now(),
                OrderStatus.PENDING, lines, currentUser.getAddress());
        orders.add(order);
        cart.clearCart();

        notifyCartChanged();
        notifyOrdersChanged();
        notifyProductsChanged();
        return order;
    }

    public List<Order> getMyOrders() {
        requireLogin();
        List<Order> mine = new ArrayList<>();
        for (int i = orders.size() - 1; i >= 0; i--) {
            if (orders.get(i).getUserId() == currentUser.getId()) mine.add(orders.get(i));
        }
        return mine;
    }

    public void cancelMyOrder(int orderId) {
        requireLogin();
        Order o = findOrderById(orderId);
        if (o.getUserId() != currentUser.getId())
            throw new IllegalStateException("That order does not belong to you");
        o.cancelOrder();

        for (OrderLine l : o.getOrderLines()) {
            Product p = findProductById(l.getProductId());
            p.setstock(p.getStock() + l.getQuantity());
        }
        notifyOrdersChanged();
        notifyProductsChanged();
    }

    private Order findOrderById(int id) {
        for (Order o : orders) {
            if (o.getId() == id) return o;
        }
        throw new IllegalArgumentException("Order not found: " + id);
    }

    public List<Order> getAllOrders() {
        requireAdmin();
        List<Order> all = new ArrayList<>(orders);
        Collections.reverse(all);
        return all;
    }

    public void advanceOrder(int orderId) {
        requireAdmin();
        findOrderById(orderId).nextState();
        notifyOrdersChanged();
    }

    public List<Supplier> getSuppliers() {
        requireAdmin();
        return List.copyOf(suppliers);
    }

    public Supplier addSupplier(String name, String email, String address, int daysToDeliver) {
        requireAdmin();
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        if (daysToDeliver <= 0) throw new IllegalArgumentException("Delivery days must be greater than 0");
        Supplier s = new Supplier(nextSupplierId++, name, email, address, daysToDeliver);
        suppliers.add(s);
        return s;
    }

    public Product addProduct(String name, String description, String category,
                              double cost, double price, int stock) {
        requireAdmin();
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        if (cost <= 0) throw new IllegalArgumentException("Cost must be greater than 0");
        if (price <= cost) throw new IllegalArgumentException("Price must be greater than cost");
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        Product p = new Product(nextProductId++, name, description, category, cost, price, 0, 0, 0, 0, stock);
        products.add(p);
        notifyProductsChanged();
        return p;
    }

    public void updatePrice(int productId, double price) {
        requireAdmin();
        findProductById(productId).setprice(price);
        notifyProductsChanged();
    }

    public void updateStock(int productId, int stock) {
        requireAdmin();
        findProductById(productId).setstock(stock);
        notifyProductsChanged();
    }

    public double getTotalRevenue() {
        requireAdmin();
        double total = 0;
        for (Order o : orders) {
            if (o.getStatus() != OrderStatus.CANCELLED) total += o.getTotalBillingAmount();
        }
        return total;
    }

    public double getTotalProfit() {
        requireAdmin();
        double total = 0;
        for (Order o : orders) {
            if (o.getStatus() != OrderStatus.CANCELLED) total += o.getTotalProfitAmount();
        }
        return total;
    }

    public int countOrders(OrderStatus status) {
        requireAdmin();
        int count = 0;
        for (Order o : orders) {
            if (o.getStatus() == status) count++;
        }
        return count;
    }

    public Map<String, Integer> getUnitsSoldByProduct() {
        requireAdmin();
        Map<String, Integer> units = new HashMap<>();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.CANCELLED) continue;
            for (OrderLine l : o.getOrderLines()) {
                units.put(l.getProductName(), units.getOrDefault(l.getProductName(), 0) + l.getQuantity());
            }
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(units.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
        Map<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : entries) sorted.put(e.getKey(), e.getValue());
        return sorted;
    }
}
