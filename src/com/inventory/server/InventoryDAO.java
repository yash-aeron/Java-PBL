package com.inventory.server;
import com.inventory.model.Product;
import com.inventory.model.PurchaseOrder;
import com.inventory.model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO{
    private Connection getConnection(){
        return DatabaseManager.getInstance().getConnection();
    }
    public synchronized List<Product> getAllProducts(){
        List<Product> products = new ArrayList<>();
        try(Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")){
            while(rs.next()){
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getInt("reorder_level"));
                products.add(product);
            }
        } 
        catch(SQLException e){
            e.printStackTrace();
        }
        return products;
    }
    public synchronized boolean addProduct(Product p) {
        String sql = "INSERT INTO products (name, category, quantity, price, reorder_level) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getReorderLevel());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public synchronized boolean updateProduct(Product p){
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ?, reorder_level = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getReorderLevel());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public synchronized boolean deleteProduct(int id){
        String sql = "DELETE FROM products WHERE id = ?";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } 
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public synchronized List<Product> searchProducts(String keyword){
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getInt("reorder_level"));
                products.add(product);
            }
            rs.close();
        } 
        catch(SQLException e){
            e.printStackTrace();
        }
        return products;
    }
    public synchronized List<Product> getLowStockProducts(){
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity < reorder_level";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getInt("reorder_level"));
                products.add(product);
            }
        } 
        catch (SQLException e){
            e.printStackTrace();
        }
        return products;
    }
    public synchronized boolean validateUser(String username, String password){
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            boolean valid = rs.next();
            rs.close();
            return valid;
        } 
        catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public synchronized boolean addUser(String username, String password){
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, password);
            return ps.executeUpdate() > 0;
        } 
        catch(SQLException e){
            System.err.println("Registration failed: " + e.getMessage());
        }
        return false;
    }
    public synchronized List<Supplier> getAllSuppliers(){
        List<Supplier> suppliers = new ArrayList<>();
        try(Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM suppliers")){
            while (rs.next()){
                Supplier supplier = new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_email"),
                        rs.getString("phone"),
                        rs.getString("address"));
                suppliers.add(supplier);
            }
        } 
        catch (SQLException e){
            e.printStackTrace();
        }
        return suppliers;
    }
    public synchronized boolean addSupplier(Supplier s){
        String sql = "INSERT INTO suppliers (name, contact_email, phone, address) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            ps.setString(1, s.getName());
            ps.setString(2, s.getContactEmail());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getAddress());
            return ps.executeUpdate() > 0;
        } 
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public synchronized boolean createPurchaseOrder(PurchaseOrder order){
        String sql = "INSERT INTO purchase_orders (product_id, supplier_id, quantity_ordered, order_date, status, total_cost) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, order.getProductId());
            ps.setInt(2, order.getSupplierId());
            ps.setInt(3, order.getQuantityOrdered());
            ps.setString(4, order.getOrderDate());
            ps.setString(5, order.getStatus().name());
            ps.setDouble(6, order.getTotalCost());
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public synchronized List<PurchaseOrder> getAllPurchaseOrders(){
        List<PurchaseOrder> orders = new ArrayList<>();
        String sql = "SELECT po.*, p.name AS product_name, s.name AS supplier_name "
                + "FROM purchase_orders po "
                + "JOIN products p ON po.product_id = p.id "
                + "JOIN suppliers s ON po.supplier_id = s.id "
                + "ORDER BY po.id DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                PurchaseOrder order = new PurchaseOrder(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getInt("quantity_ordered"),
                        rs.getString("order_date"),
                        PurchaseOrder.OrderStatus.valueOf(rs.getString("status")),
                        rs.getDouble("total_cost"));
                orders.add(order);
            }
        } 
        catch (SQLException e){
            e.printStackTrace();
        }
        return orders;
    }
    public synchronized boolean updateOrderStatus(int orderId, String status) {
        String checkSql = "SELECT status, product_id, quantity_ordered FROM purchase_orders WHERE id = ?";
        String currentStatus = "";
        int productId = -1;
        int qtyOrdered = 0;
        try (PreparedStatement checkPs = getConnection().prepareStatement(checkSql)) {
            checkPs.setInt(1, orderId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                currentStatus = rs.getString("status");
                productId = rs.getInt("product_id");
                qtyOrdered = rs.getInt("quantity_ordered");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE purchase_orders SET status = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0 && "COMPLETED".equals(status) && !"COMPLETED".equals(currentStatus)) {
                String updateStock = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
                try (PreparedStatement stockPs = getConnection().prepareStatement(updateStock)) {
                    stockPs.setInt(1, qtyOrdered);
                    stockPs.setInt(2, productId);
                    stockPs.executeUpdate();
                }
            }
            return rowsUpdated > 0;
        } 
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
