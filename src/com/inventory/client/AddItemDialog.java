
package com.inventory.client;

import com.inventory.model.Command;
import com.inventory.model.Product;
import com.inventory.model.Response;

import javax.swing.*;
import java.awt.*;

public class AddItemDialog extends JDialog {

    private JTextField nameField, categoryField, qtyField, priceField;
    private NetworkManager network;
    private MainDashboard parent;

    public AddItemDialog(MainDashboard parent, NetworkManager network) {
        this.parent = parent;
        this.network = network;

        setTitle("Add Product");
        setSize(300, 250);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Category:"));
        categoryField = new JTextField();
        add(categoryField);

        add(new JLabel("Quantity:"));
        qtyField = new JTextField();
        add(qtyField);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        JButton addBtn = new JButton("Add");
        add(addBtn);

        addBtn.addActionListener(e -> addProduct());

        setVisible(true);
    }

    private void addProduct() {
        String name = nameField.getText();
        String category = categoryField.getText();
        int qty = Integer.parseInt(qtyField.getText());
        double price = Double.parseDouble(priceField.getText());

        Product p = new Product(0, name, category, qty, price);

        Command cmd = new Command(Command.CommandType.ADD_ITEM, p);
        Response res = network.sendCommand(cmd);

        if (res.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Added successfully");
            parent.loadData();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + res.getMessage());
        }
    }
}
