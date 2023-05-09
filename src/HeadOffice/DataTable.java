package HeadOffice;


import Encommun.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;

public class DataTable{
    final Object[] column = {"Date","Region","Product","Quantity","Cost","AMT","Tax","Total","BO N°"};
    private JScrollPane scrollPane;
    private JTable dataTable;
    DefaultTableModel dtm;


    public DataTable(){
        Object[][] data = {};
        this.dtm = new DefaultTableModel(data, this.column);
        this.dataTable =new JTable(dtm);
        this.dataTable.setBounds(30,40,200,300);
        this.scrollPane = new JScrollPane(this.dataTable);
        try {
            this.initTable();
        } catch (SQLException sqlException){

        }

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
                    Integer.toString(p.getDbNumber())

            });
        }
    }


    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void initTable() throws SQLException {
        dtm.setRowCount(0);
        DaoService dbRetrieveService = new DaoService(this);
        List<Product> productList = dbRetrieveService.getAllData();
        for (Product p : productList){
            dtm.addRow(new Object[]{p.getDate().toString(),
                    p.getRegion(),
                    p.getProduct(),
                    Integer.toString(p.getQty()),
                    Float.toString(p.getCost()),
                    Double.toString(p.getAmt()),
                    Float.toString(p.getTax()),
                    Double.toString(p.getTotal()),
                    Integer.toString(p.getDbNumber()),
            });
        }

    }
}

