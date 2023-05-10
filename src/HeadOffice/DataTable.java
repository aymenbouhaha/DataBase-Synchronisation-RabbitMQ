package HeadOffice;


import Encommun.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataTable{
    final Object[] column = {"id","Date","Region","Product","Quantity","Cost","AMT","Tax","Total","BO N°"};
    private JScrollPane scrollPane;
    public JTable dataTable;
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
            sqlException.printStackTrace();
        }

        getIdsTable();


    }

    public void addLines(List<Product> products){
        for (Product p : products){
            dtm.addRow(new Object[]{
                    p.getId(),
                    p.getDate().toString(),
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

    public List<Integer> getIdsTable(){
        List<Integer> idTable= new ArrayList<Integer>();
        TableModel model = dataTable.getModel();
        int rowCount = model.getRowCount();
        for (int i=0;i<rowCount;i++){

            idTable.add((int) model.getValueAt(i,0));
        }
        return idTable;
    }


    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void initTable() throws SQLException {
        dtm.setRowCount(0);
        DaoService dbRetrieveService = new DaoService(this);
        List<Product> productList = dbRetrieveService.getAllData();
        System.out.println(productList);
        for (Product p : productList){
            dtm.addRow(new Object[]{
                    p.getId(),
                    p.getDate().toString(),
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

    public void updateTable(Product product){
        List<Integer> idsTable=this.getIdsTable();
        int index=-1;
        for (int i=0;i<idsTable.size();i++){
            if (idsTable.get(i)==product.getId()){
                index=i;
                break;
            }

        }
        if (index!=-1){
            dataTable.setValueAt(product.getRegion(),index,2);
            dataTable.setValueAt(product.getProduct(),index,3);
            dataTable.setValueAt(product.getQty(),index,4);
            dataTable.setValueAt(product.getCost(),index,5);
            dataTable.setValueAt(product.getAmt(),index,6);
            dataTable.setValueAt(product.getTax(),index,7);
            dataTable.setValueAt(product.getTotal(),index,8);
        }
    }
}

