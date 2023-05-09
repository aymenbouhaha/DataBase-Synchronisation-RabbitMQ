package branchOffice;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import Encommun.Product;

import javax.swing.*;

import static Encommun.Serialize.serialize;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;


public class BOdb {
    public final static String QUEUE_NAME="product_sale_queue";
    public static void main(String[] args) throws IOException, SQLException {

        int DBNumber = Integer.parseInt(args[0]);
        DAOService service = new DAOService(DBNumber);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        JFrame insertionFrame = new JFrame();
        insertionFrame.setVisible(true);
        insertionFrame.setTitle("Branch Office " + args[0]);

        DataTable dataTable = new DataTable(DBNumber);

        InsertPanel insertPanel = new InsertPanel(DBNumber,dataTable,connectionFactory,service);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                insertPanel, dataTable.getScrollPane());
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(300);

        insertionFrame.add(splitPane);
        insertionFrame.setSize(700,450);
        insertionFrame.setLocation(500,250);

    }
}
