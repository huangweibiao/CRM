package com.crm.repository;

import com.crm.entity.SalesPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * SalesPerformance Repository - 销售业绩Repository
 *
 * @author CRM Team
 */
@Repository
public interface SalesPerformanceRepository extends JpaRepository<SalesPerformance, Long> {

    /**
     * 根据用户ID查询业绩列表
     */
    Page<SalesPerformance> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据部门ID查询业绩列表
     */
    Page<SalesPerformance> findByDeptId(Long deptId, Pageable pageable);

    /**
     * 根据合同ID查询业绩
     */
    List<SalesPerformance> findByContractId(Long contractId);

    /**
     * 根据日期范围查询业绩
     */
    @Query("SELECT s FROM SalesPerformance s WHERE s.performanceDate BETWEEN :startDate AND :endDate")
    List<SalesPerformance> findByPerformanceDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 统计用户业绩总额
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SalesPerformance s WHERE s.userId = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);

    /**
     * 统计部门业绩总额
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SalesPerformance s WHERE s.deptId = :deptId")
    BigDecimal sumAmountByDeptId(@Param("deptId") Long deptId);

    /**
     * 统计日期范围内业绩
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SalesPerformance s " +
           "WHERE s.performanceDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 统计用户日期范围内业绩
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SalesPerformance s " +
           "WHERE s.userId = :userId AND s.performanceDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 统计成单数量
     */
    long countByUserId(Long userId);
}
