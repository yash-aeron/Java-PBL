package com.inventory.server;

import com.inventory.model.Command;
import com.inventory.model.Product;
import com.inventory.model.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final InventoryDAO dao;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.dao = new InventoryDAO();
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                Command command = (Command) in.readObject();
                Response response = processCommand(command);
                out.writeObject(response);
                out.flush();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected or error: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Response processCommand(Command command) {
        if (command.getType() == Command.CommandType.GET_INVENTORY) {
            List<Product> products = dao.getAllProducts();
            return new Response(true, "OK", products);
        } else if (command.getType() == Command.CommandType.ADD_ITEM) {
            Product p = (Product) command.getPayload();
            boolean success = dao.addProduct(p);
            if (success) {
                return new Response(true, "Product added successfully", null);
            } else {
                return new Response(false, "Failed to add product", null);
            }
        }
        return new Response(false, "Unknown command", null);
    }
}
