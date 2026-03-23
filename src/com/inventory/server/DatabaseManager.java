package com.inventory.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:stockflow.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTablesIfNotExist() {
        try (Statement stmt = connection.createStatement()) {
            String createProducts = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "category TEXT, " +
                    "quantity INTEGER, " +
                    "price REAL)";
            stmt.execute(createProducts);

            String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)";
            stmt.execute(createUsers);

            String checkUsers = "SELECT COUNT(*) AS count FROM users";
            ResultSet rs = stmt.executeQuery(checkUsers);
            if (rs.next() && rs.getInt("count") == 0) {
                String insertAdmin = "INSERT INTO users (username, password) VALUES ('admin', 'admin123')";
                stmt.execute(insertAdmin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
