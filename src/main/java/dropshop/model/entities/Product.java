package dropshop.model.entities;

public class Product{
    private int id;
    private String name;
    private String description;
    private String category;
    private double costproduction;
    private double costsale;
    private double valoration;
    private double totalbenefit;
    private int numvaloration;
    private int numsales;
    private int stock;

    public Product(int id, String name, String description, String category, double costproduction, double costsale, double valoration,double totalbenefit, int numvaloration, int numsales, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.costproduction = costproduction;
        this.costsale = costsale;
        this.valoration = valoration;
        this.numvaloration = numvaloration;
        this.numsales = numsales;
        this.stock = stock;
        this.totalbenefit = totalbenefit;
    }
    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getCategory(){
        return category;
    }
    public double getCostproduction(){
        return costproduction;
    }
    public double getCostsale(){
        return costsale;
    }
    public double getValoration(){
        return valoration;
    }
    public double getBenefit(){
        return costsale - costproduction;
    }
    public double getTotalbenefit(){
        return totalbenefit;
    }
    public int getNumvaloration(){
        return numvaloration;
    }
    public int getNumsales(){
        return numsales;
    }
    public int getStock(){
        return stock;
    }
    public boolean isAvailable(){
        return stock > 0;
    }

    public void sell(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > stock) {
            throw new IllegalStateException("Not enough stock of " + name);
        }
        stock -= quantity;
        numsales += quantity;
        totalbenefit += getBenefit() * quantity;
    }
    public void setprice(double price){
        if (price < 0 || price < costproduction) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.costsale = price;
    }
    public void setstock(int stock){
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.stock = stock;
    }
    public void addRating(double nueva) {
        if (nueva < 1 || nueva > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        valoration = (valoration * numvaloration + nueva) / (numvaloration + 1);
        numvaloration++;
    }
}