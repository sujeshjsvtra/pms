package com.sparksupport.pms.service;

import com.sparksupport.pms.model.Product;
import com.sparksupport.pms.dto.ProductDTO;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> getAllProducts(int page, int size);

     List<Product> getAllActiveProducts();

    List<Product> getAllActiveProducts(int page, int size);

    Product getProductById(Long id);

    Product addProduct(ProductDTO dto);

    Product updateProduct(Long id, ProductDTO productDto);

    void deleteProduct(Long id);

    void hardDeleteProduct(Long id);

    void restoreProduct(Long id);

    double getTotalRevenue();

    double getRevenueByProduct(Long productId);

    ProductDTO getProductDTO(Long id);

    List<ProductDTO> getAllProductDTOs();

    List<ProductDTO> getAllProductDTOs(int page, int size);

    void addProduct(Product p1);
}
