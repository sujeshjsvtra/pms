package com.sparksupport.pms.controller;

import com.sparksupport.pms.model.Sale;
import com.sparksupport.pms.dto.SaleDTO;
import com.sparksupport.pms.service.SaleService;

import io.swagger.v3.oas.annotations.Operation;

import com.sparksupport.pms.mapper.SaleMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    @Operation(summary = "Get all sales", description = "Retrieve all sales. Accessible by USER and ADMIN roles.")
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSaleDTOs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID", description = "Retrieve a specific sale by its ID. Accessible by USER and ADMIN roles.")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleDTO(id));
    }

    @PostMapping
    @Operation(summary = "Add new sale", description = "Create a new sale. Accessible by USER and ADMIN roles.")
    public ResponseEntity<SaleDTO> addSale(@RequestBody SaleDTO saleDTO) {
        Sale created = saleService.addSale(saleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(SaleMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Sale (ADMIN only)", description = "Accessible only by ADMIN users")
    public ResponseEntity<SaleDTO> updateSale(@PathVariable Long id, @RequestBody SaleDTO saleDTO) {
        Sale updated = saleService.updateSale(id, saleDTO);
        return ResponseEntity.ok(SaleMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete sale (ADMIN only)", description = "Accessible only by ADMIN users")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update sale (ADMIN only)", description = "Accessible only by ADMIN users")
    public ResponseEntity<Void> restoreSale(@PathVariable Long id) {
        saleService.restoreSale(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SaleDTO>> getSalesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(saleService.getSaleDTOsByProductId(productId));
    }
}