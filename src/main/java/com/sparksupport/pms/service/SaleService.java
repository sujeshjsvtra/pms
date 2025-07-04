package com.sparksupport.pms.service;

import com.sparksupport.pms.model.Sale;
import com.sparksupport.pms.dto.SaleDTO;
import java.util.List;

public interface SaleService {
    List<Sale> getAllSales();

    List<Sale> getSalesByProductId(Long productId);

    Sale getSaleById(Long id);

    Sale addSale(SaleDTO saleDTO);

    Sale updateSale(Long id, SaleDTO saleDTO);

    void deleteSale(Long id);

    void restoreSale(Long id);
 
    SaleDTO getSaleDTO(Long id);

    List<SaleDTO> getAllSaleDTOs();

    List<SaleDTO> getSaleDTOsByProductId(Long productId);

    void addSale(Sale s1);
}