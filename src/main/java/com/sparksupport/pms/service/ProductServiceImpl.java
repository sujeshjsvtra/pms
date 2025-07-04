package com.sparksupport.pms.service;

import com.sparksupport.pms.model.Product;
import com.sparksupport.pms.repository.ProductRepository;
import com.sparksupport.pms.util.Constants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparksupport.pms.dto.ProductDTO;
import com.sparksupport.pms.exception.ResourceNotFoundException;
import com.sparksupport.pms.mapper.ProductMapper;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductServiceImpl(ProductRepository productRepository, AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).getContent();
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Override
    public List<Product> getAllActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByActiveTrue(pageable).getContent();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product addProduct(ProductDTO dto) {
        Product product = ProductMapper.toEntity(dto);
        product.setActive(true);
        product.setActive(true);
        Product saved = productRepository.save(product);
        logAudit(Constants.PRODUCT, saved.getId(), Constants.ADD, saved);
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product updateProduct(Long id, ProductDTO updatedProductDto) {

        Product existingProduct = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with id: " + id));
        Product updatedProduct = ProductMapper.toEntity(updatedProductDto);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());

        Product saved = productRepository.save(existingProduct);
        logAudit(Constants.PRODUCT, saved.getId(), Constants.UPDATE, saved);

        return saved;

    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with id: " + id));

        product.setActive(false);
        productRepository.save(product);
        logAudit(Constants.PRODUCT, id, Constants.SOFT_DELETE, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void hardDeleteProduct(Long id) {
        Product optionalProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(optionalProduct);
        logAudit(Constants.PRODUCT, id, Constants.HARD_DELETE, null);

    }

    @Override
    @Transactional
    public void restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (!product.isActive()) {
            product.setActive(true);
            productRepository.save(product);
            logAudit(Constants.PRODUCT, id, Constants.RESTORE, null);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public double getTotalRevenue() {
        List<Product> activeProducts = productRepository.findByActiveTrue();
        double totalRevenue = 0;
        for (Product product : activeProducts) {
            double price = product.getPrice() != null ? product.getPrice() : 0;
            double productRevenue = product.getSales().stream()
                    .mapToDouble(sale -> sale.getQuantity() * price)
                    .sum();
            totalRevenue += productRevenue;
        }
        return totalRevenue;
    }

    @Override
    @Transactional(readOnly = true)
    public double getRevenueByProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findByIdWithSales(productId);
        double totalRevenue = 0;
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            double price = product.getPrice() != null ? product.getPrice() : 0;
            double productRevenue = product.getSales().stream()
                    .mapToDouble(sale -> sale.getQuantity() * price)
                    .sum();
            totalRevenue += productRevenue;
        }
        return totalRevenue;
    }

    @Override
    public ProductDTO getProductDTO(Long id) {
        Product product = getProductById(id);
        return ProductMapper.toDTO(product);
    }

    @Override
    public List<ProductDTO> getAllProductDTOs() {
        return ProductMapper.toDTOList(getAllActiveProducts());
    }

    @Override
    public List<ProductDTO> getAllProductDTOs(int page, int size) {
        return ProductMapper.toDTOList(getAllActiveProducts(page, size));
    }

    private void logAudit(String entityType, Long entityId, String action, Object details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        String detailsStr = null;
        try {
            detailsStr = details != null ? objectMapper.writeValueAsString(details) : null;
        } catch (Exception e) {
            detailsStr = details != null ? details.toString() : null;
        }
        auditLogService.logEvent(entityType, entityId, action, username, detailsStr);
    }

    @Override
    @Transactional
    public void addProduct(Product product) {
        productRepository.save(product);
    }
}
