package com.crm.repository;

import com.crm.entity.ContractAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ContractAudit Repository - 合同审核Repository
 *
 * @author CRM Team
 */
@Repository
public interface ContractAuditRepository extends JpaRepository<ContractAudit, Long> {

    /**
     * 根据合同ID查询审核记录
     */
    List<ContractAudit> findByContractIdOrderByAuditTimeDesc(Long contractId);

    /**
     * 根据审核人ID查询审核记录
     */
    List<ContractAudit> findByAuditUserIdOrderByAuditTimeDesc(Long auditUserId);

    /**
     * 统计审核次数
     */
    long countByContractId(Long contractId);
}
