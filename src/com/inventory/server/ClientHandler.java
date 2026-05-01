package com.inventory.server;
import com.inventory.model.Command;
import com.inventory.model.Product;
import com.inventory.model.PurchaseOrder;
import com.inventory.model.Response;
import com.inventory.model.Supplier;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private InventoryDAO dao;
    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.dao = new InventoryDAO();
    }
    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Command command = (Command) in.readObject();
                Response response = processCommand(command);
                out.writeObject(response);
                out.flush();
                out.reset();
            }
        } 
        catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress().getHostAddress());
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Response processCommand(Command command) {
        switch (command.getType()) {
            case LOGIN: {
                String credentials = (String) command.getPayload();
                String[] parts = credentials.split(":");
                if (parts.length == 2 && dao.validateUser(parts[0], parts[1])) {
                    return new Response(true, "Login successful", null);
                }
                return new Response(false, "Invalid username or password", null);
            }
            case GET_INVENTORY: {
                List<Product> products = dao.getAllProducts();
                return new Response(true, "OK", products);
            }
            case ADD_ITEM: {
                Product product = (Product) command.getPayload();
                boolean added = dao.addProduct(product);
                if (added) {
                    return new Response(true, "Product added successfully", null);
                }
                return new Response(false, "Failed to add product", null);
            }
            case UPDATE_ITEM: {
                Product product = (Product) command.getPayload();
                boolean updated = dao.updateProduct(product);
                if (updated) {
                    return new Response(true, "Product updated successfully", null);
                }
                return new Response(false, "Failed to update product", null);
            }
            case DELETE_ITEM: {
                int productId = (Integer) command.getPayload();
                boolean deleted = dao.deleteProduct(productId);
                if (deleted) {
                    return new Response(true, "Product deleted successfully", null);
                }
                return new Response(false, "Failed to delete product", null);
            }
            case SEARCH: {
                String keyword = (String) command.getPayload();
                List<Product> results = dao.searchProducts(keyword);
                return new Response(true, "OK", results);
            }
            case GET_LOW_STOCK: {
                List<Product> lowStock = dao.getLowStockProducts();
                return new Response(true, "OK", lowStock);
            }
            case GET_SUPPLIERS: {
                List<Supplier> suppliers = dao.getAllSuppliers();
                return new Response(true, "OK", suppliers);
            }
            case ADD_SUPPLIER: {
                Supplier supplier = (Supplier) command.getPayload();
                boolean added = dao.addSupplier(supplier);
                if (added) {
                    return new Response(true, "Supplier added successfully", null);
                }
                return new Response(false, "Failed to add supplier", null);
            }
            case CREATE_PURCHASE_ORDER: {
                PurchaseOrder order = (PurchaseOrder) command.getPayload();
                boolean created = dao.createPurchaseOrder(order);
                if (created) {
                    return new Response(true, "Purchase order created successfully", null);
                }
                return new Response(false, "Failed to create purchase order", null);
            }
            case GET_PURCHASE_ORDERS: {
                List<PurchaseOrder> orders = dao.getAllPurchaseOrders();
                return new Response(true, "OK", orders);
            }
            case UPDATE_ORDER_STATUS: {
                String[] parts = ((String) command.getPayload()).split(":");
                int orderId = Integer.parseInt(parts[0]);
                String status = parts[1];
                boolean updated = dao.updateOrderStatus(orderId, status);
                if (updated) {
                    return new Response(true, "Order status updated", null);
                }
                return new Response(false, "Failed to update order status", null);
            }
            case REGISTER: {
                String credentials = (String) command.getPayload();
                String[] parts = credentials.split(":");
                if (parts.length == 2) {
                    boolean success = dao.addUser(parts[0], parts[1]);
                    if (success) {
                        return new Response(true, "Account created successfully", null);
                    } 
                    else {
                        return new Response(false, "Username already exists", null);
                    }
                }
                return new Response(false, "Invalid registration data", null);
            }
            default:
                return new Response(false, "Unknown command", null);
        }
    }
}

