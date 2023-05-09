package branchOffice;


import Encommun.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;

public class DataTable{
    final Object[] column = {"Date","Region","Product","Quantity","Cost","AMT","Tax","Total"};
    private JScrollPane scrollPane;
    private JTable dataTable;
    DefaultTableModel dtm;

    int dbNumber;
    public DataTable(int dbNumber){
        Object[][] data = {};
        this.dbNumber = dbNumber;
        this.dtm = new DefaultTableModel(data, this.column);
        this.dataTable =new JTable(dtm);
        this.dataTable.setBounds(30,40,200,300);
        this.scrollPane = new JScrollPane(this.dataTable);
        try {
            this.initTable();
        }catch (SQLException sqlException){

        }
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void addLines(List<Product> products){
        for (Product p : products){
            dtm.addRow(new Object[]{p.getDate().toString(),
                    p.getRegion(),
                    p.getProduct(),
                    Integer.toString(p.getQty()),
                    Float.toString(p.getCost()),
                    Double.toString(p.getAmt()),
                    Float.toString(p.getTax()),
                    Double.toString(p.getTotal()),
            });
        }
    }



    public void addLine(Product p){
        dtm.addRow(new Object[]{p.getDate().toString(),
                p.getRegion(),
                p.getProduct(),
                Integer.toString(p.getQty()),
                Float.toString(p.getCost()),
                Double.toString(p.getAmt()),
                Float.toString(p.getTax()),
                Double.toString(p.getTotal()),
        });
    }


    public void initTable() throws SQLException {
        DAOService daoService = new DAOService(this.dbNumber);
        List<Product> productList = daoService.getAllData();
        for (Product p : productList){
            dtm.addRow(new Object[]{p.getDate().toString(),
                    p.getRegion(),
                    p.getProduct(),
                    Integer.toString(p.getQty()),
                    Float.toString(p.getCost()),
                    Double.toString(p.getAmt()),
                    Float.toString(p.getTax()),
                    Double.toString(p.getTotal()),
            });
        }

    }

}
