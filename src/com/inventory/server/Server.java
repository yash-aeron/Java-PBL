package com.inventory.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int PORT = 5000;

    public static void main(String[] args) {
        DatabaseManager.getInstance().createTablesIfNotExist();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("StockFlow Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
