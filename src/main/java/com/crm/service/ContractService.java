package com.crm.service;

import com.crm.entity.Business;
import com.crm.entity.Contract;
import com.crm.entity.ContractAudit;
import com.crm.entity.SalesPerformance;
import com.crm.exception.BusinessException;
import com.crm.repository.BusinessRepository;
import com.crm.repository.ContractAuditRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.SalesPerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contract Service - 合同管理服务
 *
 * @author CRM Team
 */
@Service
public class ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final ContractAuditRepository contractAuditRepository;
    private final BusinessRepository businessRepository;
    private final SalesPerformanceRepository salesPerformanceRepository;

    public ContractService(ContractRepository contractRepository,
                           ContractAuditRepository contractAuditRepository,
                           BusinessRepository businessRepository,
                           SalesPerformanceRepository salesPerformanceRepository) {
        this.contractRepository = contractRepository;
        this.contractAuditRepository = contractAuditRepository;
        this.businessRepository = businessRepository;
        this.salesPerformanceRepository = salesPerformanceRepository;
    }

    /**
     * 创建合同
     */
    @Transactional
    public Contract createContract(Long customerId, Long businessId, Long createUserId,
                                   String contractNo, BigDecimal contractAmount,
                                   LocalDate signDate, LocalDate validEndDate,
                                   String paymentType, Integer deliveryDays,
                                   String content, String attachmentUrl) {
        log.info("Creating contract: {}", contractNo);

        // 检查合同编号是否存在
        if (contractRepository.existsByContractNo(contractNo)) {
            throw new BusinessException("合同编号已存在");
        }

        // 校验客户
        if (customerId != null) {
            // 客户存在性校验
        }

        // 校验商机
        if (businessId != null) {
            Business business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new BusinessException("商机不存在"));
            // 校验商机状态
            if (!"WON".equals(business.getStatus())) {
                throw new BusinessException("只能为已成单商机创建合同");
            }
        }

        Contract contract = new Contract();
        contract.setContractNo(contractNo);
        contract.setCustomerId(customerId);
        contract.setBusinessId(businessId);
        contract.setContractAmount(contractAmount);
        contract.setSignDate(signDate);
        contract.setValidStartDate(signDate);  // 生效日期默认签订日期
        contract.setValidEndDate(validEndDate);
        contract.setCreateUserId(createUserId);
        contract.setStatus("PENDING_AUDIT");
        contract.setPaymentType(paymentType);
        contract.setDeliveryDays(deliveryDays);
        contract.setContent(content);
        contract.setAttachmentUrl(attachmentUrl);

        return contractRepository.save(contract);
    }

    /**
     * 审核合同
     */
    @Transactional
    public Contract auditContract(Long contractId, Long auditUserId,
                                  String auditResult, String auditOpinion) {
        log.info("Auditing contract: {}, result: {}", contractId, auditResult);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("合同不存在"));

        // 状态校验
        if (!"PENDING_AUDIT".equals(contract.getStatus())) {
            throw new BusinessException("该合同不是待审核状态");
        }

        // 创建审核记录
        ContractAudit audit = new ContractAudit();
        audit.setContractId(contractId);
        audit.setAuditUserId(auditUserId);
        audit.setAuditResult(auditResult);
        audit.setAuditOpinion(auditOpinion);
        contractAuditRepository.save(audit);

        // 更新合同状态
        if ("PASS".equals(auditResult)) {
            contract.setStatus("EFFECTIVE");
            contract.setValidStartDate(LocalDate.now());

            // 自动生成销售业绩
            Business business = null;
            if (contract.getBusinessId() != null) {
                business = businessRepository.findById(contract.getBusinessId()).orElse(null);
            }

            Long userId = contract.getCreateUserId();
            if (business != null) {
                userId = business.getOwnerUserId();
            }

            SalesPerformance performance = new SalesPerformance();
            performance.setUserId(userId);
            performance.setContractId(contractId);
            performance.setAmount(contract.getContractAmount());
            performance.setPerformanceDate(contract.getValidStartDate());
            performance.setPerformanceType("CONTRACT_PAYMENT");
            salesPerformanceRepository.save(performance);

        } else {
            contract.setStatus("REJECTED");
        }

        return contractRepository.save(contract);
    }

    /**
     * 终止合同
     */
    @Transactional
    public Contract terminateContract(Long contractId, Long currentUserId) {
        log.info("Terminating contract: {}", contractId);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("合同不存在"));

        // 状态校验
        if (!"EFFECTIVE".equals(contract.getStatus())) {
            throw new BusinessException("只能终止已生效的合同");
        }

        contract.setStatus("TERMINATED");
        return contractRepository.save(contract);
    }

    /**
     * 变更合同
     */
    @Transactional
    public Contract changeContract(Long contractId, BigDecimal newAmount,
                                   LocalDate newEndDate, String remark) {
        log.info("Changing contract: {}", contractId);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("合同不存在"));

        // 状态校验
        if (!"EFFECTIVE".equals(contract.getStatus())) {
            throw new BusinessException("只能变更已生效的合同");
        }

        if (newAmount != null) {
            contract.setContractAmount(newAmount);
        }
        if (newEndDate != null) {
            contract.setValidEndDate(newEndDate);
        }
        if (remark != null) {
            contract.setRemark(remark);
        }

        return contractRepository.save(contract);
    }

    /**
     * 获取合同详情
     */
    public Contract getContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("合同不存在"));
    }

    /**
     * 分页查询合同列表
     */
    public Page<Contract> getContractList(String keyword, String status,
                                          Long customerId, Long createUserId,
                                          int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());

        if (createUserId != null) {
            return contractRepository.findByCreateUserId(createUserId, pageable);
        }

        if (customerId != null) {
            return contractRepository.findByCustomerId(customerId, pageable);
        }

        if (status != null && !status.isEmpty()) {
            return contractRepository.findByStatus(status, pageable);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return contractRepository.searchContracts(keyword, pageable);
        }

        return contractRepository.findAll(pageable);
    }

    /**
     * 获取合同审核记录
     */
    public List<ContractAudit> getAuditRecords(Long contractId) {
        return contractAuditRepository.findByContractIdOrderByAuditTimeDesc(contractId);
    }

    /**
     * 统计合同数据
     */
    public Object getContractStats() {
        return new Object() {
            public final long pending = contractRepository.countByStatus("PENDING_AUDIT");
            public final long effective = contractRepository.countByStatus("EFFECTIVE");
            public final long rejected = contractRepository.countByStatus("REJECTED");
            public final long terminated = contractRepository.countByStatus("TERMINATED");
            public final BigDecimal effectiveAmount = contractRepository.sumAmountByStatus("EFFECTIVE");
        };
    }
}
