package com.sparksupport.pms.controller;

import com.sparksupport.pms.model.Product;
import com.sparksupport.pms.service.DocumentService;
import com.sparksupport.pms.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;

import com.sparksupport.pms.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.sparksupport.pms.dto.ProductDTO;
import com.sparksupport.pms.mapper.ProductMapper;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final DocumentService documentService;

    public ProductController(ProductService productService, DocumentService documentService) {
        this.productService = productService;
        this.documentService = documentService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products with pagination. Accessible by USER and ADMIN roles.")
    public ResponseEntity<List<ProductDTO>> getAllProducts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProductDTOs(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID. Accessible by USER and ADMIN roles.")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductDTO(id);
        return ResponseEntity.ok(productDTO);
    }

    @PostMapping
    @Operation(summary = "Add new product", description = "Create a new product. Accessible by USER and ADMIN roles.")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO) {
        Product created = productService.addProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product (ADMIN only)", description = "Accessible only by ADMIN users")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(ProductMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product (ADMIN only)", description = "Accessible only by ADMIN users")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product (ADMIN only)", description = "Accessible only by ADMIN users")
    public ResponseEntity<Void> restoreProduct(@PathVariable Long id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(productService.getTotalRevenue());
    }

    @GetMapping("/revenue/{productId}")
    public ResponseEntity<Double> getRevenueByProduct(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            return ResponseEntity.ok(productService.getRevenueByProduct(productId));
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
    }

    @GetMapping("/pdf")
    public void downloadProductsPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=products.pdf");
        try {
            documentService.generateProductsPdf(response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not generate PDF");
        }
    }

    @GetMapping("/{id}/qrcode")
    public void getProductQrCode(@PathVariable Long id, HttpServletResponse response)
            throws IOException {
        try {
            documentService.generateProductQrCode(id, response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issue found while genearting QR code " + e.getMessage());
        }
    }
}
