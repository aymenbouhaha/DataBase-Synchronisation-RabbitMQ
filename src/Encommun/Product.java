package Encommun;


import java.util.Date;

public class Product {
    private int id;
    private Date date;
    private String region;
    private String product;
    private int qty;
    private float cost;
    private double amt;
    private float tax;
    private double total;
    private int dbNumber;

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    private int updated;

    public Product(int id, Date date, String region, String product, int qty, float cost, double amt, float tax, double total, int dbNumber) {
        this.id = id;
        this.date = date;
        this.region = region;
        this.product = product;
        this.qty = qty;
        this.cost = cost;
        this.amt = amt;
        this.tax = tax;
        this.total = total;
        this.dbNumber = dbNumber;
    }

    public Product(Date date, String region, String product, int qty, float cost, double amt, float tax, double total, int dbNumber) {
        this.date = date;
        this.region = region;
        this.product = product;
        this.qty = qty;
        this.cost = cost;
        this.amt = amt;
        this.tax = tax;
        this.total = total;
        this.dbNumber = dbNumber;
    }

    public Product() {
    }

    public boolean compareProduct(Product p) {
//        System.out.println("from compare:" + p.toString());
        boolean b1=p.getId() == this.id;
        boolean b2=this.region.equals(p.getRegion());
        boolean b3=p.getQty() == this.qty;
        boolean b4=p.getCost() == this.cost;
        boolean b5=p.getAmt() == this.amt;
        boolean b6=p.getTax() == this.tax;
        boolean b7=p.getTotal() == this.total;
        boolean b8=this.product.equals(p.getProduct());
        if (b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8) {
            return true;
        } else {
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getDbNumber() {
        return dbNumber;
    }

    public void setDbNumber(int dbNumber) {
        this.dbNumber = dbNumber;
    }

    public String toString() {
        return "Product{" +
                "id=" + id +
                ", date=" + date +
                ", region='" + region + '\'' +
                ", product='" + product + '\'' +
                ", qty=" + qty +
                ", cost=" + cost +
                ", amt=" + amt +
                ", tax=" + tax +
                ", total=" + total +
                ", DB_Number=" + dbNumber +
                '}';
    }
}
