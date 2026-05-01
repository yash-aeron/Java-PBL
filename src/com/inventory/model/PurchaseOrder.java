package com.inventory.model;
import java.io.Serializable;

public class PurchaseOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum OrderStatus {
        PENDING,
        COMPLETED,
        CANCELLED
    }
    private int id;
    private int productId;
    private String productName;
    private int supplierId;
    private String supplierName;
    private int quantityOrdered;
    private String orderDate;
    private OrderStatus status;
    private double totalCost;
    public PurchaseOrder() {
    }
    public PurchaseOrder(int id, int productId, String productName, int supplierId,
                         String supplierName, int quantityOrdered, String orderDate,
                         OrderStatus status, double totalCost) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.quantityOrdered = quantityOrdered;
        this.orderDate = orderDate;
        this.status = status;
        this.totalCost = totalCost;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public int getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    public int getQuantityOrdered() {
        return quantityOrdered;
    }
    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }
    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public double getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", product='" + productName + '\'' +
                ", supplier='" + supplierName + '\'' +
                ", qty=" + quantityOrdered +
                ", status=" + status +
                '}';
    }
}
