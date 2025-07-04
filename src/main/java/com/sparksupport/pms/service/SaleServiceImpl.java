package com.sparksupport.pms.service;

import com.sparksupport.pms.model.Sale;
import com.sparksupport.pms.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

 

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparksupport.pms.mapper.SaleMapper;
import com.sparksupport.pms.dto.SaleDTO;
import com.sparksupport.pms.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SaleServiceImpl(SaleRepository saleRepository, AuditLogService auditLogService) {
        this.saleRepository = saleRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll().stream().filter(Sale::isActive).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> getSalesByProductId(Long productId) {
        return saleRepository.findByProduct_Id(productId).stream().filter(Sale::isActive).toList();
    }

    @Override
    public Sale getSaleById(Long id) {
        return saleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public Sale addSale(SaleDTO saleDTO) {
        Sale sale = SaleMapper.toEntity(saleDTO);
        Sale saved = saleRepository.save(sale);
        logAudit("Sale", saved.getId(), "ADD", saved);
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Sale updateSale(Long id, SaleDTO saleDTO) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        Sale entity = SaleMapper.toEntity(saleDTO);

        entity.setProduct(sale.getProduct());
        entity.setQuantity(sale.getQuantity());
        entity.setSaleDate(sale.getSaleDate());
        Sale updated = saleRepository.save(entity);
        logAudit("Sale", updated.getId(), "UPDATE", updated);
        return updated;
    }

    @Override
    @Transactional
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .filter(Sale::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active sale not found with id: " + id));

        sale.setActive(false);
        saleRepository.save(sale);
        logAudit("Sale", id, "SOFT_DELETE", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreSale(Long id) {
        Sale optionalProduct = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        saleRepository.delete(optionalProduct);
        logAudit("Product", id, "HARD_DELETE", null);
    }

    @Override
    public SaleDTO getSaleDTO(Long id) {
        Sale sale = saleRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return SaleMapper.toDTO(sale);
    }

    @Override
    public List<SaleDTO> getAllSaleDTOs() {
        return SaleMapper.toDTOList(getAllSales());
    }

    @Override
    public List<SaleDTO> getSaleDTOsByProductId(Long productId) {
        return SaleMapper.toDTOList(getSalesByProductId(productId));
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
    public void addSale(Sale sale) {
        saleRepository.save(sale);
    }
}