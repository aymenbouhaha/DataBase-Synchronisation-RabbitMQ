package HeadOffice;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Encommun.Product;

public class DaoService {
    public String url = "jdbc:mysql://localhost:3306/ho";
    public String user = "root";
    public String password = "";
    DataTable dataTable;

    public DaoService(DataTable dataTable){
        this.dataTable=dataTable;
    }

    public void insert(List<Product> productList) throws SQLException {
        String query = "INSERT INTO ho.product_sale( date , region, product, qty, cost, amt, tax, total, dbNumber) values(?,?,?,?,?,?,?,?,?)";
        List<Product> products=this.getAllData();
        System.out.println("existiing products Count: "+products.size());
        System.out.println(products);
        List<Product> productsToAdd=new ArrayList<Product>();
        boolean b;
        if (products.size()!=0){
            for (Product product : productList){
                b=false;
                for (Product product1 : products){
                    System.out.println(product.compareProduct(product1));
                    if (product.compareProduct(product1)){
                        b=true;
                    }
                }
                if (!b){
                    productsToAdd.add(product);
                }
            }
        }else {
            productsToAdd=productList;
        }
        System.out.println("products to add:"+productsToAdd);
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)
        ) {
            for (Product p : productsToAdd) {
                pst.setDate(1, new Date(p.getDate().getTime()));
                pst.setString(2, p.getRegion());
                pst.setString(3, p.getProduct());
                pst.setInt(4, p.getQty());
                pst.setFloat(5, p.getCost());
                pst.setDouble(6, p.getAmt());
                pst.setFloat(7, p.getTax());
                pst.setDouble(8, p.getTotal());
                pst.setInt(9, p.getDbNumber());
                pst.executeUpdate();
                int id=-1;
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        id = (int) generatedKeys.getLong(1);
                        System.out.println(id);
                    }
                    else {
                        throw new SQLException("Insert failed, no ID obtained.");
                    }
                }
                p.setId(id!=-1?id:p.getId());
            }
            dataTable.addLines(productsToAdd);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<Product> getAllData() throws SQLException{
        List<Product> products = new ArrayList<>();
        String getAllProducts="SELECT * FROM product_sale";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = connection.prepareStatement(getAllProducts)
        ){
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setDate(rs.getDate("date"));
                product.setRegion(rs.getString("region"));
                product.setProduct(rs.getString("product"));
                product.setQty(rs.getInt("qty"));
                product.setCost(rs.getFloat("cost"));
                product.setAmt(rs.getDouble("amt"));
                product.setTax(rs.getFloat("tax"));
                product.setTotal(rs.getDouble("total"));
                product.setDbNumber(rs.getInt("dbNumber"));

                products.add(product);
            }
        }


        return products;
    }


    public void update(List<Product> toUpdate) throws SQLException {
        String updateQuery ="UPDATE product_sale set date = ?, region = ?,product =?,qty=?,cost=?,amt=?,tax=?,total=? where id = ?";
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement pst = connection.prepareStatement(updateQuery);
        ){
            for (Product p : toUpdate) {
                pst.setDate(1, new Date(p.getDate().getTime()));
                pst.setString(2, p.getRegion());
                pst.setString(3, p.getProduct());
                pst.setInt(4, p.getQty());
                pst.setFloat(5, p.getCost());
                pst.setDouble(6, p.getAmt());
                pst.setFloat(7, p.getTax());
                pst.setDouble(8, p.getTotal());
                pst.setInt(9, p.getId());
                pst.executeUpdate();
                dataTable.updateTable(p);
            }
        }
    }
}