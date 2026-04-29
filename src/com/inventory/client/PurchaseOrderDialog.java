package com.client;

import com.inventory.model.Command;
import com.inventory.model.Product;
import com.inventory.model.PurchaseOrder;
import com.inventory.model.Response;
import com.inventory.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("serial")
public class PurchaseOrderDialog extends JDialog {

    private NetworkManager networkManager;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    private static final String[] COLUMN_NAMES = {
            "Order ID", "Product", "Supplier", "Qty Ordered", "Total Cost", "Date", "Status"
    };

    @SuppressWarnings("this-escape")
    public PurchaseOrderDialog(JFrame parent, NetworkManager networkManager) {
        super(parent, "Purchase Orders", true);
        this.networkManager = networkManager;
        initializeUI();
        loadOrders();
    }

    private void initializeUI() {
        setSize(900, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton createOrderButton = new JButton("Create Order");
        JButton autoRestockButton = new JButton("\u26A0 Auto-Restock Low Stock");
        autoRestockButton.setForeground(new Color(0xE6, 0x5C, 0x00));
        autoRestockButton.setFont(autoRestockButton.getFont().deriveFont(Font.BOLD));
        JButton markCompletedButton = new JButton("Mark Completed");
        JButton markCancelledButton = new JButton("Mark Cancelled");
        JButton refreshButton = new JButton("Refresh");

        topPanel.add(createOrderButton);
        topPanel.add(autoRestockButton);
        topPanel.add(Box.createHorizontalStrut(15));
        topPanel.add(markCompletedButton);
        topPanel.add(markCancelledButton);
        topPanel.add(Box.createHorizontalStrut(15));
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(28);
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 6);
                    if ("PENDING".equals(status)) {
                        c.setBackground(new Color(0xFF, 0xF8, 0xE1));
                    } else if ("COMPLETED".equals(status)) {
                        c.setBackground(new Color(0xE8, 0xF5, 0xE9));
                    } else if ("CANCELLED".equals(status)) {
                        c.setBackground(new Color(0xFC, 0xE4, 0xEC));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                if (column == 6) {
                    String status = (String) value;
                    if ("PENDING".equals(status)) {
                        setForeground(new Color(0xF5, 0x7F, 0x17));
                    } else if ("COMPLETED".equals(status)) {
                        setForeground(new Color(0x2E, 0x7D, 0x32));
                    } else if ("CANCELLED".equals(status)) {
                        setForeground(new Color(0xC6, 0x28, 0x28));
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(Color.BLACK);
                }

                return c;
            }
        });

        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        createOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateOrderDialog();
            }
        });

        autoRestockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoRestockLowStock();
            }
        });

        markCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedOrderStatus("COMPLETED");
            }
        });

        markCancelledButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedOrderStatus("CANCELLED");
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadOrders();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void loadOrders() {
        try {
            Command command = new Command(Command.CommandType.GET_PURCHASE_ORDERS, null);
            Response response = networkManager.sendCommand(command);

            if (response.isSuccess()) {
                List<PurchaseOrder> orders = (List<PurchaseOrder>) response.getData();
                tableModel.setRowCount(0);
                for (PurchaseOrder order : orders) {
                    tableModel.addRow(new Object[]{
                            order.getId(),
                            order.getProductName(),
                            order.getSupplierName(),
                            order.getQuantityOrdered(),
                            String.format("%.2f", order.getTotalCost()),
                            order.getOrderDate(),
                            order.getStatus().name()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load orders: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void showCreateOrderDialog() {
        try {
            Response prodResp = networkManager.sendCommand(
                    new Command(Command.CommandType.GET_INVENTORY, null));
            Response suppResp = networkManager.sendCommand(
                    new Command(Command.CommandType.GET_SUPPLIERS, null));

            if (!prodResp.isSuccess() || !suppResp.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Failed to load products or suppliers.");
                return;
            }

            List<Product> products = (List<Product>) prodResp.getData();
            List<Supplier> suppliers = (List<Supplier>) suppResp.getData();

            if (products.isEmpty() || suppliers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You need at least one product and one supplier to create an order.",
                        "Cannot Create Order", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JComboBox<Product> productCombo = new JComboBox<>(products.toArray(new Product[0]));
            productCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Product) {
                        Product p = (Product) value;
                        setText(p.getName() + " (Stock: " + p.getQuantity() + ")");
                    }
                    return this;
                }
            });

            JComboBox<Supplier> supplierCombo = new JComboBox<>(suppliers.toArray(new Supplier[0]));
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));

            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Product:"), gbc);
            gbc.gridx = 1;
            panel.add(productCombo, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Supplier:"), gbc);
            gbc.gridx = 1;
            panel.add(supplierCombo, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Quantity:"), gbc);
            gbc.gridx = 1;
            panel.add(quantitySpinner, gbc);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Create Purchase Order", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                Product selectedProduct = (Product) productCombo.getSelectedItem();
                Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
                int quantity = (int) quantitySpinner.getValue();
                double totalCost = selectedProduct.getPrice() * quantity;

                PurchaseOrder order = new PurchaseOrder(
                        0,
                        selectedProduct.getId(),
                        selectedProduct.getName(),
                        selectedSupplier.getId(),
                        selectedSupplier.getName(),
                        quantity,
                        LocalDate.now().toString(),
                        PurchaseOrder.OrderStatus.PENDING,
                        totalCost);

                Command cmd = new Command(Command.CommandType.CREATE_PURCHASE_ORDER, order);
                Response resp = networkManager.sendCommand(cmd);

                if (resp.isSuccess()) {
                    JOptionPane.showMessageDialog(this,
                            "Purchase order created!\nTotal cost: $" + String.format("%.2f", totalCost));
                    loadOrders();
                } else {
                    JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating order: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void autoRestockLowStock() {
        try {
            Response lowStockResp = networkManager.sendCommand(
                    new Command(Command.CommandType.GET_LOW_STOCK, null));
            Response suppResp = networkManager.sendCommand(
                    new Command(Command.CommandType.GET_SUPPLIERS, null));

            if (!lowStockResp.isSuccess() || !suppResp.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Failed to load data.");
                return;
            }

            List<Product> lowStockProducts = (List<Product>) lowStockResp.getData();
            List<Supplier> suppliers = (List<Supplier>) suppResp.getData();

            if (lowStockProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No low stock products found! All products are well stocked.",
                        "No Restock Needed", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (suppliers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No suppliers available. Please add a supplier first.",
                        "No Suppliers", JOptionPane.WARNING_MESSAGE);
                return;
            }

            StringBuilder summary = new StringBuilder();
            summary.append("The following restock orders will be created:\n\n");
            for (Product p : lowStockProducts) {
                int orderQty = p.getReorderLevel() * 2;
                summary.append("\u2022 ").append(p.getName())
                        .append(" — Order ").append(orderQty)
                        .append(" units ($").append(String.format("%.2f", p.getPrice() * orderQty))
                        .append(")\n");
            }
            summary.append("\nSupplier: ").append(suppliers.get(0).getName());
            summary.append("\n\nProceed?");

            int confirm = JOptionPane.showConfirmDialog(this, summary.toString(),
                    "Auto-Restock " + lowStockProducts.size() + " Products",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                Supplier defaultSupplier = suppliers.get(0);
                int created = 0;

                for (Product p : lowStockProducts) {
                    int orderQty = p.getReorderLevel() * 2;
                    double totalCost = p.getPrice() * orderQty;

                    PurchaseOrder order = new PurchaseOrder(
                            0,
                            p.getId(),
                            p.getName(),
                            defaultSupplier.getId(),
                            defaultSupplier.getName(),
                            orderQty,
                            LocalDate.now().toString(),
                            PurchaseOrder.OrderStatus.PENDING,
                            totalCost);

                    Command cmd = new Command(Command.CommandType.CREATE_PURCHASE_ORDER, order);
                    Response resp = networkManager.sendCommand(cmd);
                    if (resp.isSuccess()) {
                        created++;
                    }
                }

                JOptionPane.showMessageDialog(this,
                        created + " restock orders created successfully!",
                        "Auto-Restock Complete", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during auto-restock: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedOrderStatus(String newStatus) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);
        if (!"PENDING".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "Only PENDING orders can be updated.",
                    "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            String payload = orderId + ":" + newStatus;
            Command cmd = new Command(Command.CommandType.UPDATE_ORDER_STATUS, payload);
            Response resp = networkManager.sendCommand(cmd);

            if (resp.isSuccess()) {
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this, resp.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update order: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
