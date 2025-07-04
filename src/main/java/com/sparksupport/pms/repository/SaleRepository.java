package com.sparksupport.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparksupport.pms.model.Sale;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByProduct_Id(Long productId);

    Optional<Sale> findByIdAndActiveTrue(Long id);
}
