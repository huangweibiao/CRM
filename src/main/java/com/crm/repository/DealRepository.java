package com.crm.repository;

import com.crm.entity.Deal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Deal Repository
 *
 * @author CRM Team
 */
@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByCustomerId(Long customerId);

    List<Deal> findByStatus(String status);

    Page<Deal> findByStatus(String status, Pageable pageable);

    Page<Deal> findByStage(String stage, Pageable pageable);

    @Query("SELECT d FROM Deal d WHERE d.deleted = false AND " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Deal> searchDeals(String keyword, Pageable pageable);

    @Query("SELECT d FROM Deal d WHERE d.status = 'OPEN' AND " +
           "d.expectedCloseDate BETWEEN :startDate AND :endDate")
    List<Deal> findDealsClosingBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT d FROM Deal d WHERE d.assignedUserId = :userId AND d.deleted = false")
    Page<Deal> findByAssignedUserId(Long userId, Pageable pageable);
}
