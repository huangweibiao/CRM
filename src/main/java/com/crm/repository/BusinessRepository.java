package com.crm.repository;

import com.crm.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Business Repository - 商机Repository
 *
 * @author CRM Team
 */
@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    /**
     * 根据客户ID查询商机列表
     */
    List<Business> findByCustomerId(Long customerId);

    /**
     * 根据客户ID查询商机（分页）
     */
    Page<Business> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * 根据归属人ID查询商机列表
     */
    Page<Business> findByOwnerUserId(Long ownerUserId, Pageable pageable);

    /**
     * 根据归属人和状态查询商机
     */
    Page<Business> findByOwnerUserIdAndStatus(Long ownerUserId, String status, Pageable pageable);

    /**
     * 根据商机阶段查询商机
     */
    List<Business> findByStage(String stage);

    /**
     * 根据状态查询商机
     */
    List<Business> findByStatus(String status);

    Page<Business> findByStatus(String status, Pageable pageable);

    /**
     * 搜索商机
     */
    @Query("SELECT b FROM Business b WHERE b.deleted = false AND " +
           "(LOWER(b.businessName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Business> searchBusinesses(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询即将到期的商机
     */
    @Query("SELECT b FROM Business b WHERE b.status = 'IN_PROGRESS' " +
           "AND b.expectedCloseDate <= :endDate AND b.expectedCloseDate >= :startDate")
    List<Business> findExpiringBusinesses(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 统计商机数量
     */
    long countByOwnerUserId(Long ownerUserId);

    /**
     * 统计各阶段商机数量
     */
    long countByStage(String stage);

    /**
     * 统计已成交商机数量
     */
    long countByStatus(String status);
}
