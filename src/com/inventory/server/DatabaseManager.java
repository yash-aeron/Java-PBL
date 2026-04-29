package com.inventory.server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager{
    private static final String DB_URL = "jdbc:sqlite:stockflow.db";

    //Well Making sure there's only one Database Manager running at a time - Yash
    private static DatabaseManager instance;
    private Connection connection;
    private DatabaseManager(){
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database");
        } 
        catch(ClassNotFoundException e){
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } 
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public static synchronized DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }
    public Connection getConnection(){
        return connection;
    }
    public void createTablesIfNotExist(){
        String createProductsTable = "CREATE TABLE IF NOT EXISTS products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "category TEXT, "
                + "quantity INTEGER, "
                + "price REAL, "
                + "reorder_level INTEGER DEFAULT 10)";

        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE, "
                + "password TEXT)";

        String createSuppliersTable = "CREATE TABLE IF NOT EXISTS suppliers ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "contact_email TEXT, "
                + "phone TEXT, "
                + "address TEXT)";

        String createPurchaseOrdersTable = "CREATE TABLE IF NOT EXISTS purchase_orders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "product_id INTEGER, "
                + "supplier_id INTEGER, "
                + "quantity_ordered INTEGER, "
                + "order_date TEXT, "
                + "status TEXT DEFAULT 'PENDING', "
                + "total_cost REAL, "
                + "FOREIGN KEY (product_id) REFERENCES products(id), "
                + "FOREIGN KEY (supplier_id) REFERENCES suppliers(id))";

        try (Statement stmt = connection.createStatement()){
            stmt.execute(createProductsTable);
            stmt.execute(createUsersTable);
            stmt.execute(createSuppliersTable);
            stmt.execute(createPurchaseOrdersTable);
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0){
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO users (username, password) VALUES (?, ?)");
                ps.setString(1, "admin");
                ps.setString(2, "admin123");
                ps.executeUpdate();
                ps.close();
                System.out.println("Default admin user created.");
            }
            rs.close();
            ResultSet suppRs = stmt.executeQuery("SELECT COUNT(*) FROM suppliers");
            if (suppRs.next() && suppRs.getInt(1) == 0) {
                String[] suppliers = {
                    "INSERT INTO suppliers (name, contact_email, phone, address) VALUES ('TechWorld Distributors', 'sales@techworld.com', '+91-9876543210', 'Mumbai, Maharashtra')",
                    "INSERT INTO suppliers (name, contact_email, phone, address) VALUES ('FreshGoods Supply Co.', 'orders@freshgoods.in', '+91-8765432109', 'Delhi, NCR')",
                    "INSERT INTO suppliers (name, contact_email, phone, address) VALUES ('Office Essentials Ltd.', 'bulk@officeessentials.com', '+91-7654321098', 'Bangalore, Karnataka')",
                    "INSERT INTO suppliers (name, contact_email, phone, address) VALUES ('Global Tech Solutions', 'contact@globaltech.com', '+91-1122334455', 'Hyderabad, Telangana')",
                    "INSERT INTO suppliers (name, contact_email, phone, address) VALUES ('Prime Electronics', 'support@primeelec.com', '+91-9988776655', 'Chennai, Tamil Nadu')"
                };
                for (String sql : suppliers) stmt.execute(sql);
                System.out.println("Default suppliers created.");
            }
            suppRs.close();

            ResultSet prodRs = stmt.executeQuery("SELECT COUNT(*) FROM products");
            if (prodRs.next() && prodRs.getInt(1) == 0) {
                String[] products = {
                    "INSERT INTO products (name, category, quantity, price, reorder_level) VALUES ('MacBook Pro 16', 'Electronics', 15, 2500.00, 5)",
                    "INSERT INTO products (name, category, quantity, price, reorder_level) VALUES ('Ergonomic Chair', 'Furniture', 42, 199.99, 10)",
                    "INSERT INTO products (name, category, quantity, price, reorder_level) VALUES ('Mechanical Keyboard', 'Accessories', 8, 89.50, 15)",
                    "INSERT INTO products (name, category, quantity, price, reorder_level) VALUES ('Wireless Mouse', 'Accessories', 55, 45.00, 20)",
                    "INSERT INTO products (name, category, quantity, price, reorder_level) VALUES ('4K Monitor', 'Electronics', 3, 350.00, 8)"
                };
                for (String sql : products) stmt.execute(sql);
                System.out.println("Default products created.");
            }
            prodRs.close();

            ResultSet orderRs = stmt.executeQuery("SELECT COUNT(*) FROM purchase_orders");
            if (orderRs.next() && orderRs.getInt(1) == 0) {
                String[] orders = {
                    "INSERT INTO purchase_orders (product_id, supplier_id, quantity_ordered, order_date, status, total_cost) VALUES (3, 1, 50, '2026-04-20', 'COMPLETED', 4475.00)",
                    "INSERT INTO purchase_orders (product_id, supplier_id, quantity_ordered, order_date, status, total_cost) VALUES (5, 4, 10, '2026-04-22', 'PENDING', 3500.00)",
                    "INSERT INTO purchase_orders (product_id, supplier_id, quantity_ordered, order_date, status, total_cost) VALUES (1, 5, 5, '2026-04-25', 'PENDING', 12500.00)",
                    "INSERT INTO purchase_orders (product_id, supplier_id, quantity_ordered, order_date, status, total_cost) VALUES (2, 3, 20, '2026-04-21', 'COMPLETED', 3999.80)",
                    "INSERT INTO purchase_orders (product_id, supplier_id, quantity_ordered, order_date, status, total_cost) VALUES (4, 1, 30, '2026-04-26', 'PENDING', 1350.00)"
                };
                for (String sql : orders) stmt.execute(sql);
                System.out.println("Default purchase orders created.");
            }
            orderRs.close();

            System.out.println("Database tables verified.");
        } 
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
