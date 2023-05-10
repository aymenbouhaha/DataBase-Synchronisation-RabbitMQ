package branchOffice;

import Encommun.Product;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import static Encommun.Serialize.serialize;

public class InsertPanel extends JPanel{


    public JTextField regionTextFld;
    public JTextField productTextFld;
    public JTextField quantityTextFld;
    public JTextField costTextFld;
    public JTextField amtTextFld;
    public JTextField taxTextFld;
    public JTextField totalTextFld;
    public JTextArea textArea;
    public JButton submitBtn;
    public JButton sendButton;
    public JButton updateButton;
    public ConnectionFactory rabitMqConnectionFactory;

    private Connection connection;

    DAOService service;

    DataTable dataTable;
    private int selectedRowIndex;
    private int selectedId;
    private String selectedRegion;
    private String selectedProduct;
    private int selectedQty;
    private float selectedCost;
    private double selectedAmt;
    private float selectedTax;
    private double selectedTotal;

    public InsertPanel( int dbNumber,DataTable dataTable ,ConnectionFactory rabitMqConnectionFactory, DAOService daoService) {

        this.service=daoService;
        this.rabitMqConnectionFactory=rabitMqConnectionFactory;
        this.dataTable=dataTable;
        this.prepraInterface(dbNumber);

        ListSelectionModel selectionModel= dataTable.dataTable.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && dataTable.dataTable.getSelectedRow() != -1) {
                    selectedRowIndex=dataTable.dataTable.getSelectedRow();
                    selectedId=(int) dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 0);
                    selectedRegion=(String)  dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 2);
                    selectedProduct=(String)  dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 3);
                    selectedQty=  Integer.parseInt((String) dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 4));
                    selectedCost=Float.parseFloat((String)dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 5));
                    selectedAmt=Double.parseDouble((String)dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 6));
                    selectedTax=  Float.parseFloat( (String)dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 7));
                    selectedTotal=  Double.parseDouble( (String) dataTable.dataTable.getValueAt(dataTable.dataTable.getSelectedRow(), 8));
                    regionTextFld.setText(selectedRegion);
                    productTextFld.setText(selectedProduct);
                    quantityTextFld.setText(Integer.toString(selectedQty));
                    costTextFld.setText(Float.toString(selectedCost));
                    amtTextFld.setText(Double.toString(selectedAmt));
                    taxTextFld.setText(Float.toString(selectedTax));
                    totalTextFld.setText(Double.toString(selectedTotal));
                    updateButton.setEnabled(true);


                }
            }
        });

    }

    public void prepraInterface(int dbNumber){
        setPreferredSize(new Dimension(1000, 1000));

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1000, 1000));

        JLabel regionLabel = new JLabel("Region: ");
        regionTextFld = new JTextField(15);

        JLabel productLabel = new JLabel("Product: ");
        productTextFld = new JTextField(20);

        JLabel quantityLabel = new JLabel("Quantity: ");
        quantityTextFld = new JTextField(10);

        JLabel costLabel = new JLabel("Cost: ");
        costTextFld = new JTextField(10);

        JLabel amtLabel = new JLabel("AMT: ");
        amtTextFld = new JTextField(10);

        JLabel taxLabel = new JLabel("Tax: ");
        taxTextFld = new JTextField(10);

        JLabel totalLabel = new JLabel("Total: ");
        totalTextFld = new JTextField(10);

        submitBtn = new JButton("Submit");
        SubmitButtonListner submitButtonListner = new SubmitButtonListner(dbNumber);
        submitBtn.addActionListener(submitButtonListner);

        sendButton = new JButton("Send");
        SendButtonListener sendButtonListener=new SendButtonListener(dbNumber);
        sendButton.addActionListener(sendButtonListener);

        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        UpdateButtonListner updateButtonListener=new UpdateButtonListner(dbNumber);
        updateButton.addActionListener(updateButtonListener);

        textArea = new JTextArea(10, 30);

        //add current date
        p.add(regionLabel);
        p.add(regionTextFld);
        p.add(productLabel);
        p.add(productTextFld);
        p.add(quantityLabel);
        p.add(quantityTextFld);
        p.add(costLabel);
        p.add(costTextFld);
        p.add(amtLabel);
        p.add(amtTextFld);
        p.add(taxLabel);
        p.add(taxTextFld);
        p.add(totalLabel);
        p.add(totalTextFld);
        p.add(submitBtn);
        p.add(textArea);
        p.add(sendButton);
        p.add(updateButton);
        add(p, BorderLayout.NORTH);
        add(textArea, BorderLayout.CENTER);
    }

    public void rabitMqInteract(int dbNumber){
        try {
            List<Product> productList = service.getNonSyncedProducts();
            String message = serialize(productList);
            if(productList.size()>0) {
                try (com.rabbitmq.client.Connection connection = rabitMqConnectionFactory.newConnection()) {
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(BOdb.QUEUE_NAME + Integer.toString(dbNumber), false, false, false, null);
                    channel.basicPublish("", BOdb.QUEUE_NAME + Integer.toString(dbNumber), null, message.getBytes());
                    System.out.println(" [x] sent '" + message + "' at " + LocalDateTime.now().toString());
                    service.updateSyncedProducts(productList);
                } catch (TimeoutException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){ e.printStackTrace(); }
    }

    public void submit(int dbNumber){
        try {
            Calendar calendar = Calendar.getInstance();
            java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

            String url = "jdbc:mysql://localhost:3306/bo"+Integer.toString(dbNumber);
            String user="root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);

            String region = regionTextFld.getText();
            String product = productTextFld.getText();
            int qty = Integer.parseInt(quantityTextFld.getText());
            float cost = Float.parseFloat(costTextFld.getText());
            double amt = Double.parseDouble(amtTextFld.getText());
            float tax = Float.parseFloat(taxTextFld.getText());
            double total = Double.parseDouble(totalTextFld.getText());


            // the mysql insert statement
            String query = " INSERT INTO product_sale (date, region, product, qty, cost, amt, tax, total)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setDate(1, startDate);
            preparedStmt.setString(2, region);
            preparedStmt.setString(3, product);
            preparedStmt.setInt(4, qty);
            preparedStmt.setFloat(5, cost);
            preparedStmt.setDouble(6, amt);
            preparedStmt.setFloat(7, tax);
            preparedStmt.setDouble(8, total);
            int id =0;

            preparedStmt.executeUpdate();


            try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = (int) generatedKeys.getLong(1);
                    System.out.println("Last inserted ID: " + id);
                }
                else {
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }

            Product createdProduct = new Product(id,startDate,region,product,qty,cost,amt,tax,total,dbNumber);
            System.out.println(preparedStmt);

            connection.close();

            regionTextFld.setText("");
            productTextFld.setText("");
            quantityTextFld.setText("");
            costTextFld.setText("");
            amtTextFld.setText("");
            taxTextFld.setText("");
            totalTextFld.setText("");
            System.out.println("ajout table succes");
            dataTable.addLine(createdProduct);

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public class SubmitButtonListner implements ActionListener {
        int dbNumber;
        public SubmitButtonListner(int dbNumber){
            this.dbNumber=dbNumber;
        }
        public void actionPerformed(ActionEvent e){
            submit(dbNumber);
        }
    }

    public class SendButtonListener implements ActionListener {
        int dbNumber;
        public SendButtonListener(int dbNumber){
            this.dbNumber=dbNumber;
        }
        public void actionPerformed(ActionEvent e){
            rabitMqInteract(dbNumber);
        }
    }

    public class UpdateButtonListner implements ActionListener {
        int dbNumber;
        public UpdateButtonListner(int dbNumber){
            this.dbNumber=dbNumber;
        }
        public void actionPerformed(ActionEvent e){
            updateButton(dbNumber);
        }
    }

    public void updateButton(int dbNumber) {
        try {

            String url = "jdbc:mysql://localhost:3306/bo"+Integer.toString(dbNumber);
            String user="root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);

            String region = regionTextFld.getText();
            String product = productTextFld.getText();
            int qty = Integer.parseInt(quantityTextFld.getText());
            float cost = Float.parseFloat(costTextFld.getText());
            double amt = Double.parseDouble(amtTextFld.getText());
            float tax = Float.parseFloat(taxTextFld.getText());
            double total = Double.parseDouble(totalTextFld.getText());


            // the mysql insert statement
            String updateQuery ="UPDATE product_sale set region = ?,product =?,qty=?,cost=?,amt=?,tax=?,total=?, updated=? , synchd=? where id = ?";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = connection.prepareStatement(updateQuery);
            preparedStmt.setString(1, region);
            preparedStmt.setString(2, product);
            preparedStmt.setInt(3, qty);
            preparedStmt.setFloat(4, cost);
            preparedStmt.setDouble(5, amt);
            preparedStmt.setFloat(6, tax);
            preparedStmt.setDouble(7, total);
            preparedStmt.setBoolean(8,true);
            preparedStmt.setBoolean(9,false);
            preparedStmt.setInt(10,selectedId );

            preparedStmt.executeUpdate();


            connection.close();

            regionTextFld.setText("");
            productTextFld.setText("");
            quantityTextFld.setText("");
            costTextFld.setText("");
            amtTextFld.setText("");
            taxTextFld.setText("");
            totalTextFld.setText("");
            System.out.println("update table success");

            dataTable.dataTable.setValueAt(region,selectedRowIndex,2);
            dataTable.dataTable.setValueAt(product,selectedRowIndex,3);
            dataTable.dataTable.setValueAt(qty,selectedRowIndex,4);
            dataTable.dataTable.setValueAt(cost,selectedRowIndex,5);
            dataTable.dataTable.setValueAt(amt,selectedRowIndex,6);
            dataTable.dataTable.setValueAt(tax,selectedRowIndex,7);
            dataTable.dataTable.setValueAt(total,selectedRowIndex,8);
            updateButton.setEnabled(false);



        } catch (Exception exception){
            exception.printStackTrace();
        }


    }
}
