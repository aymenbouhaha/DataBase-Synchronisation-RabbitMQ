package branchOffice;

import Encommun.Product;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.swing.*;

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
    public ConnectionFactory rabitMqConnectionFactory;

    private Connection connection;

    DAOService service;

    DataTable dataTable;

    public InsertPanel( int dbNumber,DataTable dataTable ,ConnectionFactory rabitMqConnectionFactory, DAOService daoService) {

        this.service=daoService;
        this.rabitMqConnectionFactory=rabitMqConnectionFactory;
        this.dataTable=dataTable;
        this.prepraInterface(dbNumber);

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
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setDate(1, startDate);
            preparedStmt.setString(2, region);
            preparedStmt.setString(3, product);
            preparedStmt.setInt(4, qty);
            preparedStmt.setFloat(5, cost);
            preparedStmt.setDouble(6, amt);
            preparedStmt.setFloat(7, tax);
            preparedStmt.setDouble(8, total);

            // execute the preparedstatement
            preparedStmt.execute();
            Product createdProduct = new Product(startDate,region,product,qty,cost,amt,tax,total,dbNumber);

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
            System.out.println("ajout table succes");

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
}
