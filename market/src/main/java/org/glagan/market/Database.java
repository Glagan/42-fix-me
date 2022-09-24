package org.glagan.market;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.glagan.core.Dictionary;
import org.glagan.core.Message;

public class Database implements Runnable {
    protected Connection connection;
    protected BlockingQueue<Transaction> queue;
    protected String host;
    protected int port;
    protected String user;
    protected String password;
    protected String name;

    public Database(String host, int port, String user, String password, String name) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.name = name;
        this.connection = null;
        this.queue = new ArrayBlockingQueue<>(1024);
    }

    public void connect(String host, int port, String user, String password, String name) {
        // Validate for null and empty values
        if (host == null || host.equals("")) {
            System.err.println("\u001B[31mInvalid database hostname\u001B[0m");
            return;
        }
        if (port < 0 || port > 65535) {
            System.err.println("\u001B[31mInvalid database port\u001B[0m");
            return;
        }
        if (user == null || user.equals("")) {
            System.err.println("\u001B[31mInvalid database user\u001B[0m");
            return;
        }
        if (password == null || password.equals("")) {
            System.err.println("\u001B[31mInvalid database password\u001B[0m");
            return;
        }
        if (name == null || name.equals("")) {
            System.err.println("\u001B[31mInvalid database name\u001B[0m");
            return;
        }

        // Connect and ignore errors
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + name;
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("\u001B[36mConnected to database \u001B[0m" + host + ":" + port + "@" + name);
        } catch (SQLException e) {
            System.err.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void pushTransaction(Message message, String status) {
        if (connection == null) {
            return;
        }
        queue.add(new Transaction(message, status));
    }

    /**
     * The given message is **not** checked for validity, and expect all values to
     * be correctly converted and valid for the database
     */
    public void saveTransaction(Message message, String status) {
        if (connection == null) {
            return;
        }

        String sql = "INSERT INTO transactions(market, broker, broker_order_id, instrument, quantity, price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, message.getBody().get(Dictionary.Market));
            statement.setString(2, message.getHeader().getBeginString());
            int orderId = Integer.parseInt(message.getBody().get(Dictionary.OrderId));
            statement.setInt(3, orderId);
            statement.setString(4, message.getBody().get(Dictionary.Instrument));
            int quantity = Integer.parseInt(message.getBody().get(Dictionary.Quantity));
            statement.setInt(5, quantity);
            double price = Double.parseDouble(message.getBody().get(Dictionary.Price));
            statement.setDouble(6, price);
            statement.setString(7, status);

            boolean executed = statement.execute();
            if (executed) {
                System.out.print("Saved transaction to database");
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mFailed to save transaction to the database \u001B[0m" + e.getMessage());
        }
    }

    public void run() {
        connect(host, port, user, password, name);
        if (connection != null) {
            try {
                while (!connection.isClosed()) {
                    Transaction transaction = queue.poll(1, TimeUnit.SECONDS);
                    if (transaction != null) {
                        saveTransaction(transaction.getMessage(), transaction.getStatus());
                    }
                }
            } catch (InterruptedException | SQLException e) {
                // Ignore expected interrupts
            }
            close();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("\u001B[31mFailed to close database\u001B[0m");
            }
            connection = null;
        }
    }
}