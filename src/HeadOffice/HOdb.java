package HeadOffice;

import static Encommun.Serialize.deserialize;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import Encommun.Product;

import javax.swing.*;


public class HOdb {
    public final static String QUEUE_NAME="product_sale_queue";
    public static void main(String[] args) throws IOException , TimeoutException{


        JFrame tableFrame = new JFrame();
        tableFrame.setVisible(true);
        tableFrame.setTitle("Head Office");

        DataTable dataTable = new DataTable();
        DaoService service = new DaoService(dataTable);

        tableFrame.add(dataTable.getScrollPane());
        tableFrame.setSize(700,450);
        tableFrame.setLocation(500,250);

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();
        channel1.queueDeclare(QUEUE_NAME + "1",false,false,false,null);
        channel2.queueDeclare(QUEUE_NAME + "2",false,false,false,null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
            List<Product> productList = deserialize(receivedMessage);
            System.out.println(productList);
            List<Product> toInsert = new ArrayList<Product>();
            List<Product> toUpdate = new ArrayList<Product>();
            for(Product p :productList){
                if(p.getUpdated()==1)
                    toUpdate.add(p);
                else
                    toInsert.add(p);
            }
            try {
                service.insert(toInsert);
                service.update(toUpdate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        try {
            channel1.basicConsume(QUEUE_NAME + "1",true,deliverCallback,consumerTag -> {
                System.out.println("ERROR");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            channel2.basicConsume(QUEUE_NAME + "2",true,deliverCallback,consumerTag -> {
                System.out.println("ERROR");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

//        TimerTask task=new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("HO");
//                try {
//                    channel1.basicConsume(QUEUE_NAME + "1",true,deliverCallback,consumerTag -> {
//                        System.out.println("ERROR");
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    channel2.basicConsume(QUEUE_NAME + "2",true,deliverCallback,consumerTag -> {
//                        System.out.println("ERROR");
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        Timer timer = new Timer("Ss");
//        timer.schedule(task,0, 30*1000);
    }

}
