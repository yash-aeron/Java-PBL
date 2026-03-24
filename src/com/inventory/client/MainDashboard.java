
package com.inventory.client;

import com.inventory.model.Command;
import com.inventory.model.Product;
import com.inventory.model.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainDashboard extends JFrame {

    private NetworkManager network;
    private JTable table;
    private DefaultTableModel model;

    public MainDashboard() {
        network = new NetworkManager();

        setTitle("Inventory Dashboard");
        setSize(600, 400);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Qty", "Price"}, 0);
        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Item");

        JPanel panel = new JPanel();
        panel.add(refreshBtn);
        panel.add(addBtn);

        add(panel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> new AddItemDialog(this, network));

        loadData();

        setVisible(true);
    }

    public void loadData() {
        model.setRowCount(0);

        Command cmd = new Command(Command.CommandType.GET_INVENTORY, null);
        Response res = network.sendCommand(cmd);

        if (res.isSuccess()) {
            List<Product> list = (List<Product>) res.getData();
            for (Product p : list) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getQuantity(),
                        p.getPrice()
                });
            }
        }
    }
}
