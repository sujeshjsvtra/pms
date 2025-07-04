package com.sparksupport.pms.mapper;

import com.sparksupport.pms.model.Sale;
import com.sparksupport.pms.dto.SaleDTO;
import java.util.List;
import java.util.stream.Collectors;

public class SaleMapper {

    public static SaleDTO toDTO(Sale sale) {
        if (sale == null) return null;
        
        return new SaleDTO(
            sale.getId(),
            sale.getProduct().getId(),
            sale.getQuantity(),
            sale.getSaleDate()
        );
    }

    public static Sale toEntity(SaleDTO dto) {
        if (dto == null) return null;
        
        Sale sale = new Sale();
        sale.setId(dto.getId());
        sale.setQuantity(dto.getQuantity());
        sale.setSaleDate(dto.getSaleDate());
        return sale;
    }

    public static List<SaleDTO> toDTOList(List<Sale> sales) {
        if (sales == null) return null;
        return sales.stream()
                .map(SaleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<Sale> toEntityList(List<SaleDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(SaleMapper::toEntity)
                .collect(Collectors.toList());
    }
} 