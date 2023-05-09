package branchOffice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Encommun.Product;

public class DAOService {
    private int DBNumber;
    Connection connection;
    public String url ;
    public String user = "root";
    public String password = "";

    public String queryUpdate = "UPDATE product_sale set synchd = 1  where id = ?";
    public DAOService(int DBNumber) {
        this.DBNumber = DBNumber;
        this.url ="jdbc:mysql://localhost:3306/bo"+Integer.toString(DBNumber);
        try {
            connection = DriverManager.getConnection(url, user, password);
        }catch (SQLException sqlException){

        }
    }
    public List<Product> getNonSyncedProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String queryGet="SELECT * FROM product_sale where synchd = 0";
        PreparedStatement pst = connection.prepareStatement(queryGet);
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
            product.setDbNumber(DBNumber);
            product.setUpdated(rs.getInt("updated"));
            products.add(product);
        }
        return products;
    }

    public List<Product> getAllData() throws SQLException{
        List<Product> products = new ArrayList<>();
        String getAllProducts="SELECT * FROM product_sale";
        PreparedStatement pst = connection.prepareStatement(getAllProducts);
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
            product.setDbNumber(DBNumber);
            product.setUpdated(rs.getInt("updated"));
            products.add(product);
        }
        return products;
    }

    public void updateSyncedProducts(List<Product> productList) throws SQLException {
        PreparedStatement pst = connection.prepareStatement(queryUpdate);
        for (Product product : productList) {
            pst.setInt(1, product.getId());
            pst.executeUpdate();
        }
    }
}
