package com.sparksupport.pms.dto;

import java.time.LocalDateTime;

public class SaleDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private LocalDateTime saleDate;

    public SaleDTO() {}

    public SaleDTO(Long id, Long productId, Integer quantity, LocalDateTime saleDate) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.saleDate = saleDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
} 