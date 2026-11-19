package dropshop.model.entities;

public class Supplier{
    private int id;
    private String name;
    private String email;
    private String address;
    private int daysToDeliver;

    public Supplier(int id, String name, String email, String address, int daysToDeliver) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.daysToDeliver = daysToDeliver;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
    public int getDaystoDeliver() {
        return daysToDeliver;
    }
    public void setDaysToDeliver(int daysToDeliver) {
        this.daysToDeliver = daysToDeliver;
    }
}