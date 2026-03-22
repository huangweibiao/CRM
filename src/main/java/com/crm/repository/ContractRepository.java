package com.crm.repository;

import com.crm.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contract Repository - 合同Repository
 *
 * @author CRM Team
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * 根据合同编号查询
     */
    Optional<Contract> findByContractNo(String contractNo);

    /**
     * 检查合同编号是否存在
     */
    boolean existsByContractNo(String contractNo);

    /**
     * 根据客户ID查询合同列表
     */
    List<Contract> findByCustomerId(Long customerId);

    /**
     * 根据客户ID查询合同（分页）
     */
    Page<Contract> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * 根据商机ID查询合同
     */
    Optional<Contract> findByBusinessId(Long businessId);

    /**
     * 根据创建人ID查询合同
     */
    Page<Contract> findByCreateUserId(Long createUserId, Pageable pageable);

    /**
     * 根据状态查询合同
     */
    List<Contract> findByStatus(String status);

    Page<Contract> findByStatus(String status, Pageable pageable);

    /**
     * 搜索合同
     */
    @Query("SELECT c FROM Contract c WHERE c.deleted = false AND " +
           "(LOWER(c.contractNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Contract> searchContracts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询即将到期的合同
     */
    @Query("SELECT c FROM Contract c WHERE c.validEndDate IS NOT NULL " +
           "AND c.validEndDate <= :endDate AND c.validEndDate >= :startDate " +
           "AND c.status = 'EFFECTIVE'")
    List<Contract> findExpiringContracts(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 统计合同数量
     */
    long countByStatus(String status);

    /**
     * 统计合同总金额
     */
    @Query("SELECT SUM(c.contractAmount) FROM Contract c WHERE c.status = :status")
    java.math.BigDecimal sumAmountByStatus(@Param("status") String status);
}
