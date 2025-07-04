package com.sparksupport.pms.mapper;

import com.sparksupport.pms.model.Product;
import com.sparksupport.pms.dto.ProductDTO;
import com.sparksupport.pms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private static ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        ProductMapper.productService = productService;
    }

    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        
        double revenue = productService != null ? productService.getRevenueByProduct(product.getId()) : 0.0;
        
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity(),
            revenue
        );
    }

    public static Product toEntity(ProductDTO dto) {
        if (dto == null) return null;
        
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        return product;
    }

    public static List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) return null;
        return products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<Product> toEntityList(List<ProductDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }
} 