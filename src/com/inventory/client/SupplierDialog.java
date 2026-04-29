package com.client;

import com.inventory.model.Command;
import com.inventory.model.Response;
import com.inventory.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

@SuppressWarnings("serial")
public class SupplierDialog extends JDialog {

    private NetworkManager networkManager;
    private JTable supplierTable;
    private DefaultTableModel tableModel;

    private static final String[] COLUMN_NAMES = {
            "ID", "Name", "Email", "Phone", "Address"
    };

    @SuppressWarnings("this-escape")
    public SupplierDialog(JFrame parent, NetworkManager networkManager) {
        super(parent, "Manage Suppliers", true);
        this.networkManager = networkManager;
        initializeUI();
        loadSuppliers();
    }

    private void initializeUI() {
        setSize(700, 450);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton addButton = new JButton("Add Supplier");
        topPanel.add(addButton);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setRowHeight(26);
        add(new JScrollPane(supplierTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddSupplierDialog();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void loadSuppliers() {
        try {
            Command command = new Command(Command.CommandType.GET_SUPPLIERS, null);
            Response response = networkManager.sendCommand(command);

            if (response.isSuccess()) {
                List<Supplier> suppliers = (List<Supplier>) response.getData();
                tableModel.setRowCount(0);
                for (Supplier s : suppliers) {
                    tableModel.addRow(new Object[]{
                            s.getId(), s.getName(), s.getContactEmail(),
                            s.getPhone(), s.getAddress()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load suppliers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddSupplierDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Supplier", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Supplier name is required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Supplier supplier = new Supplier(0, name, emailField.getText().trim(),
                    phoneField.getText().trim(), addressField.getText().trim());

            try {
                Command cmd = new Command(Command.CommandType.ADD_SUPPLIER, supplier);
                Response resp = networkManager.sendCommand(cmd);

                if (resp.isSuccess()) {
                    JOptionPane.showMessageDialog(this, "Supplier added successfully!");
                    loadSuppliers();
                } else {
                    JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Failed to add supplier: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
