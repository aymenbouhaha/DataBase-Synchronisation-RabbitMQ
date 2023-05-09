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
        String query = "INSERT INTO ho.product_sale( id,date , region, product, qty, cost, amt, tax, total, dbNumber) values(?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = connection.prepareStatement(query)
        ) {
            for (Product p : productList) {
                pst.setInt(1,p.getId());
                pst.setDate(2, new Date(p.getDate().getTime()));
                pst.setString(3, p.getRegion());
                pst.setString(4, p.getProduct());
                pst.setInt(5, p.getQty());
                pst.setFloat(6, p.getCost());
                pst.setDouble(7, p.getAmt());
                pst.setFloat(8, p.getTax());
                pst.setDouble(9, p.getTotal());
                pst.setInt(10, p.getDbNumber());
                pst.executeUpdate();
            }
            dataTable.addLines(productList);
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
            }
        }
    }
}