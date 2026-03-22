package com.crm.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 合同审核实体类
 * 对应数据库表 crm_sales_contract_audit
 * 记录合同的审核历史
 *
 * @author CRM Team
 */
@Entity
@Table(name = "crm_sales_contract_audit")
@EntityListeners(AuditingEntityListener.class)
public class ContractAudit extends BaseEntity {

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(name = "audit_user_id", nullable = false)
    private Long auditUserId;

    @Column(name = "audit_result", nullable = false, length = 20)
    private String auditResult;

    @Column(name = "audit_opinion", columnDefinition = "TEXT")
    private String auditOpinion;

    @CreatedDate
    @Column(name = "audit_time", nullable = false, updatable = false)
    private LocalDateTime auditTime;

    // Getter and Setter methods

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }
}
