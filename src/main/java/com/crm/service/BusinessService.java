package com.crm.service;

import com.crm.entity.Business;
import com.crm.entity.Contract;
import com.crm.entity.Customer;
import com.crm.entity.SalesPerformance;
import com.crm.exception.BusinessException;
import com.crm.repository.BusinessRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.CustomerRepository;
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
 * 商机管理服务类
 * 处理商机的创建、更新、阶段推进、转化等业务逻辑
 * 负责销售机会的全生命周期管理，是销售流程的核心服务
 *
 * @author CRM Team
 */
@Service
public class BusinessService {

    private static final Logger log = LoggerFactory.getLogger(BusinessService.class);

    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;
    private final SalesPerformanceRepository salesPerformanceRepository;

    public BusinessService(BusinessRepository businessRepository,
                           CustomerRepository customerRepository,
                           ContractRepository contractRepository,
                           SalesPerformanceRepository salesPerformanceRepository) {
        this.businessRepository = businessRepository;
        this.customerRepository = customerRepository;
        this.contractRepository = contractRepository;
        this.salesPerformanceRepository = salesPerformanceRepository;
    }

    /**
     * 创建新商机
     * 为客户创建销售机会，设置初始阶段为"初步接触"
     *
     * @param customerId 关联的客户ID
     * @param ownerUserId 商机负责人ID
     * @param businessName 商机名称
     * @param estimateAmount 预估成交金额
     * @param stage 商机阶段（可选）
     * @param source 商机来源（可选）
     * @return 创建成功后的商机对象
     * @throws BusinessException 客户不存在
     */
    @Transactional
    public Business createBusiness(Long customerId, Long ownerUserId,
                                   String businessName, BigDecimal estimateAmount,
                                   String stage, String source) {
        log.info("Creating business: {} for customer: {}", businessName, customerId);

        // 校验客户是否存在
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 构建新商机对象
        Business business = new Business();
        business.setCustomerId(customerId);
        business.setOwnerUserId(ownerUserId);
        business.setBusinessName(businessName);
        business.setEstimateAmount(estimateAmount);
        business.setStage(stage != null ? stage : "INITIAL_CONTACT");
        business.setSource(source);
        business.setStatus("IN_PROGRESS");
        business.setProbability(0);

        return businessRepository.save(business);
    }

    /**
     * 更新商机阶段
     * 推进商机销售流程，记录阶段变更和备注
     *
     * @param businessId 商机ID
     * @param stage 新阶段
     * @param remark 备注信息
     * @param currentUserId 当前操作用户ID
     * @return 更新后的商机对象
     * @throws BusinessException 商机不存在或无权限
     */
    @Transactional
    public Business updateStage(Long businessId, String stage, String remark, Long currentUserId) {
        log.info("Updating business stage: {} to {}", businessId, stage);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException("商机不存在"));

        // 权限校验：只有负责人或管理员可以更新
        if (!isAdmin(currentUserId) && !business.getOwnerUserId().equals(currentUserId)) {
            throw new BusinessException("无权限操作此商机");
        }

        // 更新阶段
        business.setStage(stage);
        if (remark != null) {
            business.setRemark(remark);
        }

        // 如果阶段变为"成单"，更新状态为"已成单"并记录成交日期
        if ("WON".equals(stage)) {
            business.setStatus("WON");
            business.setActualCloseDate(LocalDate.now());
        }

        return businessRepository.save(business);
    }

    /**
     * 商机转化为合同
     * 将已成交的商机转化为正式销售合同
     *
     * @param businessId 商机ID
     * @param currentUserId 当前操作用户ID
     * @param contractNo 合同编号
     * @param contractAmount 合同金额
     * @param signDate 签订日期
     * @return 创建的合同对象
     * @throws BusinessException 商机不存在或状态不允许转化
     */
    @Transactional
    public Contract convertToContract(Long businessId, Long currentUserId,
                                      String contractNo, BigDecimal contractAmount,
                                      LocalDate signDate) {
        log.info("Converting business {} to contract", businessId);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException("商机不存在"));

        // 状态校验：只有已成交的商机才能转化为合同
        if (!"WON".equals(business.getStatus())) {
            throw new BusinessException("只有已成单商机才能转化为合同");
        }

        // 检查合同编号是否存在
        if (contractRepository.existsByContractNo(contractNo)) {
            throw new BusinessException("合同编号已存在");
        }

        // 创建合同对象，关联商机和客户
        Contract contract = new Contract();
        contract.setContractNo(contractNo);
        contract.setCustomerId(business.getCustomerId());
        contract.setBusinessId(businessId);
        contract.setContractAmount(contractAmount);
        contract.setSignDate(signDate);
        contract.setCreateUserId(currentUserId);
        contract.setStatus("PENDING_AUDIT");

        Contract savedContract = contractRepository.save(contract);

        // 更新商机归档时间
        business.setArchiveTime(LocalDateTime.now());
        businessRepository.save(business);

        return savedContract;
    }

    /**
     * 标记商机为失败/终止
     * 记录失败原因并归档商机
     *
     * @param businessId 商机ID
     * @param failReason 失败原因
     * @param currentUserId 当前操作用户ID
     * @return 更新后的商机对象
     * @throws BusinessException 商机不存在或无权限
     */
    @Transactional
    public Business failBusiness(Long businessId, String failReason, Long currentUserId) {
        log.info("Failing business: {}", businessId);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException("商机不存在"));

        // 权限校验
        if (!isAdmin(currentUserId) && !business.getOwnerUserId().equals(currentUserId)) {
            throw new BusinessException("无权限操作此商机");
        }

        // 设置失败原因、状态和归档时间
        business.setFailReason(failReason);
        business.setStatus("FAILED");
        business.setArchiveTime(LocalDateTime.now());

        return businessRepository.save(business);
    }

    /**
     * 根据ID获取商机详情
     *
     * @param businessId 商机ID
     * @return 商机对象
     * @throws BusinessException 商机不存在
     */
    public Business getBusinessById(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException("商机不存在"));
    }

    /**
     * 分页查询商机列表
     * 支持按负责人、状态、关键词筛选
     *
     * @param keyword 关键词搜索
     * @param status 商机状态筛选
     * @param stage 商机阶段筛选
     * @param ownerUserId 负责人ID筛选
     * @param page 页码
     * @param size 每页数量
     * @return 分页商机列表
     */
    public Page<Business> getBusinessList(String keyword, String status, String stage,
                                          Long ownerUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());

        // 按负责人筛选
        if (ownerUserId != null) {
            if (status != null && !status.isEmpty()) {
                return businessRepository.findByOwnerUserIdAndStatus(ownerUserId, status, pageable);
            }
            return businessRepository.findByOwnerUserId(ownerUserId, pageable);
        }

        // 按状态筛选
        if (status != null && !status.isEmpty()) {
            return businessRepository.findByStatus(status, pageable);
        }

        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            return businessRepository.searchBusinesses(keyword, pageable);
        }

        // 查询所有
        return businessRepository.findAll(pageable);
    }

    /**
     * 根据客户ID获取关联的商机列表
     *
     * @param customerId 客户ID
     * @return 商机列表
     */
    public List<Business> getBusinessesByCustomerId(Long customerId) {
        return businessRepository.findByCustomerId(customerId);
    }

    /**
     * 获取商机统计数据
     * 返回各状态的商机数量统计
     *
     * @param ownerUserId 负责人ID（可选，为空则统计全部）
     * @return 包含商机数量统计的对象
     */
    public Object getBusinessStats(Long ownerUserId) {
        return new Object() {
            public final long total = ownerUserId != null
                ? businessRepository.countByOwnerUserId(ownerUserId)
                : businessRepository.count();
            public final long inProgress = businessRepository.countByStatus("IN_PROGRESS");
            public final long won = businessRepository.countByStatus("WON");
            public final long failed = businessRepository.countByStatus("FAILED");
        };
    }

    /**
     * 判断是否为管理员
     *
     * @param userId 用户ID
     * @return true=管理员，false=普通用户
     */
    private boolean isAdmin(Long userId) {
        // TODO: 实现基于角色的权限检查
        return true;
    }
}
