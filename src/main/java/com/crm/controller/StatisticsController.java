package com.crm.controller;

import com.crm.dto.ApiResponse;
import com.crm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Statistics Controller - 数据统计分析API
 *
 * @author CRM Team
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);

    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;
    private final ContractRepository contractRepository;
    private final SalesPerformanceRepository salesPerformanceRepository;

    public StatisticsController(CustomerRepository customerRepository, BusinessRepository businessRepository,
                                 ContractRepository contractRepository, SalesPerformanceRepository salesPerformanceRepository) {
        this.customerRepository = customerRepository;
        this.businessRepository = businessRepository;
        this.contractRepository = contractRepository;
        this.salesPerformanceRepository = salesPerformanceRepository;
    }

    /**
     * 获取客户统计
     */
    @GetMapping("/customers")
    public ApiResponse<Map<String, Object>> getCustomerStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", customerRepository.count());
        stats.put("pending", customerRepository.countByStatus("PENDING"));
        stats.put("following", customerRepository.countByStatus("FOLLOWING"));
        stats.put("deal", customerRepository.countByStatus("DEAL"));
        stats.put("archived", customerRepository.countByStatus("ARCHIVED"));
        stats.put("publicPool", customerRepository.countPublicCustomers());

        return ApiResponse.success(stats);
    }

    /**
     * 获取销售统计（商机）
     */
    @GetMapping("/sales")
    public ApiResponse<Map<String, Object>> getSalesStats(
            @RequestParam(required = false) Long userId) {

        Map<String, Object> stats = new HashMap<>();

        if (userId != null) {
            stats.put("total", businessRepository.countByOwnerUserId(userId));
        } else {
            stats.put("total", businessRepository.count());
        }

        stats.put("inProgress", businessRepository.countByStatus("IN_PROGRESS"));
        stats.put("won", businessRepository.countByStatus("WON"));
        stats.put("failed", businessRepository.countByStatus("FAILED"));

        // 按阶段统计
        Map<String, Long> stageStats = new HashMap<>();
        stageStats.put("INITIAL_CONTACT", businessRepository.countByStage("INITIAL_CONTACT"));
        stageStats.put("REQUIREMENT", businessRepository.countByStage("REQUIREMENT"));
        stageStats.put("PROPOSAL", businessRepository.countByStage("PROPOSAL"));
        stageStats.put("NEGOTIATION", businessRepository.countByStage("NEGOTIATION"));
        stageStats.put("WON", businessRepository.countByStage("WON"));
        stats.put("byStage", stageStats);

        return ApiResponse.success(stats);
    }

    /**
     * 获取业绩统计
     */
    @GetMapping("/performance")
    public ApiResponse<Map<String, Object>> getPerformanceStats(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long deptId) {

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Map<String, Object> stats = new HashMap<>();

        BigDecimal totalAmount;
        Long dealCount;

        if (userId != null) {
            totalAmount = salesPerformanceRepository.sumAmountByUserIdAndDateRange(userId, startDate, endDate);
            dealCount = salesPerformanceRepository.countByUserId(userId);
        } else if (deptId != null) {
            totalAmount = salesPerformanceRepository.sumAmountByDeptId(deptId);
            dealCount = 0L;
        } else {
            totalAmount = salesPerformanceRepository.sumAmountByDateRange(startDate, endDate);
            // 统计合同数量作为成单数量
            dealCount = contractRepository.countByStatus("EFFECTIVE");
        }

        stats.put("startDate", startDate);
        stats.put("endDate", endDate);
        stats.put("totalAmount", totalAmount);
        stats.put("dealCount", dealCount);

        return ApiResponse.success(stats);
    }

    /**
     * 获取合同统计
     */
    @GetMapping("/contracts")
    public ApiResponse<Map<String, Object>> getContractStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("pending", contractRepository.countByStatus("PENDING_AUDIT"));
        stats.put("effective", contractRepository.countByStatus("EFFECTIVE"));
        stats.put("rejected", contractRepository.countByStatus("REJECTED"));
        stats.put("terminated", contractRepository.countByStatus("TERMINATED"));
        stats.put("effectiveAmount", contractRepository.sumAmountByStatus("EFFECTIVE"));

        return ApiResponse.success(stats);
    }

    /**
     * 获取概览统计
     */
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview(
            @RequestParam(required = false) Long userId) {

        Map<String, Object> overview = new HashMap<>();

        // 客户统计
        long totalCustomers;
        if (userId != null) {
            totalCustomers = customerRepository.findByOwnerUserId(
                    userId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        } else {
            totalCustomers = customerRepository.count();
        }
        overview.put("totalCustomers", totalCustomers);

        // 商机统计
        long totalBusinesses;
        if (userId != null) {
            totalBusinesses = businessRepository.countByOwnerUserId(userId);
        } else {
            totalBusinesses = businessRepository.count();
        }
        overview.put("totalBusinesses", totalBusinesses);

        // 合同统计
        overview.put("totalContracts", contractRepository.count());
        overview.put("effectiveContracts", contractRepository.countByStatus("EFFECTIVE"));

        // 业绩统计
        BigDecimal totalPerformance;
        if (userId != null) {
            totalPerformance = salesPerformanceRepository.sumAmountByUserId(userId);
        } else {
            totalPerformance = salesPerformanceRepository.sumAmountByDateRange(
                    LocalDate.of(2000, 1, 1), LocalDate.now());
        }
        overview.put("totalPerformance", totalPerformance);

        return ApiResponse.success(overview);
    }
}
