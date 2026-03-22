package com.crm.controller;

import com.crm.dto.ApiResponse;
import com.crm.entity.Contract;
import com.crm.entity.ContractAudit;
import com.crm.service.ContractService;
import com.crm.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Contract Controller - 合同管理API
 *
 * @author CRM Team
 */
@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private static final Logger log = LoggerFactory.getLogger(ContractController.class);

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * 创建合同
     */
    @PostMapping
    public ApiResponse<Contract> createContract(
            @RequestParam Long customerId,
            @RequestParam(required = false) Long businessId,
            @RequestParam String contractNo,
            @RequestParam BigDecimal contractAmount,
            @RequestParam LocalDate signDate,
            @RequestParam(required = false) LocalDate validEndDate,
            @RequestParam(required = false, defaultValue = "ONE_TIME") String paymentType,
            @RequestParam(required = false) Integer deliveryDays,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String attachmentUrl) {

        Long userId = SecurityUtils.getCurrentUserId();
        Contract contract = contractService.createContract(
                customerId, businessId, userId, contractNo, contractAmount,
                signDate, validEndDate, paymentType, deliveryDays, content, attachmentUrl);
        return ApiResponse.success("合同创建成功", contract);
    }

    /**
     * 审核合同
     */
    @PostMapping("/{id}/audit")
    public ApiResponse<Contract> auditContract(
            @PathVariable Long id,
            @RequestParam String auditResult,
            @RequestParam(required = false) String auditOpinion) {

        Long userId = SecurityUtils.getCurrentUserId();
        Contract contract = contractService.auditContract(id, userId, auditResult, auditOpinion);
        return ApiResponse.success("合同审核完成", contract);
    }

    /**
     * 终止合同
     */
    @PostMapping("/{id}/terminate")
    public ApiResponse<Contract> terminateContract(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Contract contract = contractService.terminateContract(id, userId);
        return ApiResponse.success("合同已终止", contract);
    }

    /**
     * 变更合同
     */
    @PutMapping("/{id}")
    public ApiResponse<Contract> changeContract(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal newAmount,
            @RequestParam(required = false) LocalDate newEndDate,
            @RequestParam(required = false) String remark) {

        Contract contract = contractService.changeContract(id, newAmount, newEndDate, remark);
        return ApiResponse.success("合同变更成功", contract);
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Contract> getContract(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        return ApiResponse.success(contract);
    }

    /**
     * 分页查询合同列表
     */
    @GetMapping
    public ApiResponse<Page<Contract>> getContractList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long createUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Contract> contracts = contractService.getContractList(
                keyword, status, customerId, createUserId, page, size);
        return ApiResponse.success(contracts);
    }

    /**
     * 获取合同审核记录
     */
    @GetMapping("/{id}/audits")
    public ApiResponse<List<ContractAudit>> getAuditRecords(@PathVariable Long id) {
        List<ContractAudit> audits = contractService.getAuditRecords(id);
        return ApiResponse.success(audits);
    }

    /**
     * 获取合同统计
     */
    @GetMapping("/stats")
    public ApiResponse<Object> getStats() {
        Object stats = contractService.getContractStats();
        return ApiResponse.success(stats);
    }

}
