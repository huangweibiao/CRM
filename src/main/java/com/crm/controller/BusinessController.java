package com.crm.controller;

import com.crm.dto.ApiResponse;
import com.crm.entity.Business;
import com.crm.entity.Contract;
import com.crm.service.BusinessService;
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
 * 商机管理REST API控制器
 * 提供商机的增删改查、阶段更新、转化为合同等接口
 *
 * @author CRM Team
 */
@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    private static final Logger log = LoggerFactory.getLogger(BusinessController.class);

    private final BusinessService businessService;
    private final ContractService contractService;

    /**
     * 构造函数，手动注入依赖
     *
     * @param businessService 商机服务
     * @param contractService 合同服务
     */
    public BusinessController(BusinessService businessService, ContractService contractService) {
        this.businessService = businessService;
        this.contractService = contractService;
    }

    /**
     * 创建新商机
     * 为指定客户创建销售机会
     *
     * @param customerId 客户ID
     * @param businessName 商机名称
     * @param estimateAmount 预估金额
     * @param stage 商机阶段（可选）
     * @param source 商机来源（可选）
     * @return 包含创建结果的响应
     */
    @PostMapping
    public ApiResponse<Business> createBusiness(
            @RequestParam Long customerId,
            @RequestParam String businessName,
            @RequestParam BigDecimal estimateAmount,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String source) {

        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        Business business = businessService.createBusiness(
                customerId, userId, businessName, estimateAmount, stage, source);
        return ApiResponse.success("商机创建成功", business);
    }

    /**
     * 更新商机阶段
     * 推进商机销售流程
     *
     * @param id 商机ID
     * @param stage 新阶段
     * @param remark 备注（可选）
     * @return 包含更新结果的响应
     */
    @PutMapping("/{id}/stage")
    public ApiResponse<Business> updateStage(
            @PathVariable Long id,
            @RequestParam String stage,
            @RequestParam(required = false) String remark) {

        Long userId = SecurityUtils.getCurrentUserId();
        Business business = businessService.updateStage(id, stage, remark, userId);
        return ApiResponse.success("商机阶段更新成功", business);
    }

    /**
     * 商机转化为合同
     * 将已成交的商机转化为正式销售合同
     *
     * @param id 商机ID
     * @param contractNo 合同编号
     * @param contractAmount 合同金额
     * @param signDate 签订日期
     * @return 包含转化结果的响应
     */
    @PostMapping("/{id}/convert")
    public ApiResponse<Contract> convertToContract(
            @PathVariable Long id,
            @RequestParam String contractNo,
            @RequestParam BigDecimal contractAmount,
            @RequestParam LocalDate signDate) {

        Long userId = SecurityUtils.getCurrentUserId();
        Contract contract = businessService.convertToContract(
                id, userId, contractNo, contractAmount, signDate);
        return ApiResponse.success("商机转化合同成功", contract);
    }

    /**
     * 标记商机为失败
     * 记录失败原因并归档商机
     *
     * @param id 商机ID
     * @param failReason 失败原因
     * @return 包含操作结果的响应
     */
    @PostMapping("/{id}/fail")
    public ApiResponse<Business> failBusiness(
            @PathVariable Long id,
            @RequestParam String failReason) {

        Long userId = SecurityUtils.getCurrentUserId();
        Business business = businessService.failBusiness(id, failReason, userId);
        return ApiResponse.success("商机已标记为失败", business);
    }

    /**
     * 获取商机详情
     *
     * @param id 商机ID
     * @return 包含商机信息的响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Business> getBusiness(@PathVariable Long id) {
        Business business = businessService.getBusinessById(id);
        return ApiResponse.success(business);
    }

    /**
     * 分页查询商机列表
     * 支持多条件筛选
     *
     * @param keyword 关键词搜索
     * @param status 商机状态
     * @param stage 商机阶段
     * @param ownerUserId 负责人ID
     * @param page 页码
     * @param size 每页数量
     * @return 包含分页商机列表的响应
     */
    @GetMapping
    public ApiResponse<Page<Business>> getBusinessList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Business> businesses = businessService.getBusinessList(
                keyword, status, stage, ownerUserId, page, size);
        return ApiResponse.success(businesses);
    }

    /**
     * 根据客户ID获取关联的商机列表
     *
     * @param customerId 客户ID
     * @return 包含商机列表的响应
     */
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Business>> getBusinessesByCustomerId(@PathVariable Long customerId) {
        List<Business> businesses = businessService.getBusinessesByCustomerId(customerId);
        return ApiResponse.success(businesses);
    }

    /**
     * 获取商机统计数据
     *
     * @param ownerUserId 负责人ID（可选）
     * @return 包含统计数据的响应
     */
    @GetMapping("/stats")
    public ApiResponse<Object> getStats(@RequestParam(required = false) Long ownerUserId) {
        Object stats = businessService.getBusinessStats(ownerUserId);
        return ApiResponse.success(stats);
    }
}
