package com.crm.service;

import com.crm.dto.CustomerDTO;
import com.crm.dto.PageResult;
import com.crm.entity.Customer;
import com.crm.entity.CustomerTag;
import com.crm.exception.BusinessException;
import com.crm.repository.CustomerRepository;
import com.crm.repository.CustomerTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户管理服务类
 * 处理客户的创建、更新、删除、查询等业务逻辑
 * 负责客户的全生命周期管理，包括公海客户、分配、标签等功能
 *
 * @author CRM Team
 */
@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CustomerTagRepository customerTagRepository;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerTagRepository customerTagRepository) {
        this.customerRepository = customerRepository;
        this.customerTagRepository = customerTagRepository;
    }

    /**
     * 创建新客户
     * 验证客户名称和手机号的唯一性，设置初始状态为"待跟进"
     *
     * @param customerDTO 客户数据传输对象，包含客户信息
     * @param ownerUserId 归属用户ID，指定客户的负责人
     * @return 创建成功后的客户对象
     * @throws BusinessException 如果客户名称或手机号已存在
     */
    @Transactional
    public Customer createCustomer(CustomerDTO customerDTO, Long ownerUserId) {
        log.info("Creating customer: {}", customerDTO.getCustomerName());

        // 检查客户名称是否已存在
        if (customerRepository.existsByCustomerName(customerDTO.getCustomerName())) {
            throw new BusinessException("客户名称已存在");
        }

        // 检查手机号是否已存在
        if (customerRepository.existsByPhone(customerDTO.getPhone())) {
            throw new BusinessException("手机号已被使用");
        }

        // 构建新客户对象
        Customer customer = new Customer();
        customer.setCustomerName(customerDTO.getCustomerName());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setCompanyName(customerDTO.getCompanyName());
        customer.setIndustry(customerDTO.getIndustry());
        customer.setAddress(customerDTO.getAddress());
        customer.setRemark(customerDTO.getRemark());
        customer.setSource(customerDTO.getSource());
        customer.setLevel(customerDTO.getLevel());
        customer.setOwnerUserId(ownerUserId);
        customer.setStatus("PENDING");  // 待跟进

        return customerRepository.save(customer);
    }

    /**
     * 更新客户信息
     * 验证权限，确保只有客户归属人或管理员可以编辑
     *
     * @param customerId 客户ID
     * @param customerDTO 客户数据传输对象，包含更新的信息
     * @param currentUserId 当前操作用户ID，用于权限校验
     * @return 更新后的客户对象
     * @throws BusinessException 客户不存在或无权限
     */
    @Transactional
    public Customer updateCustomer(Long customerId, CustomerDTO customerDTO, Long currentUserId) {
        log.info("Updating customer: {}", customerId);

        // 获取客户对象
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 权限校验：只有归属人或管理员可以编辑
        if (!isAdmin(currentUserId) && !customer.getOwnerUserId().equals(currentUserId)) {
            throw new BusinessException("无权限编辑此客户");
        }

        // 更新客户名称（需检查唯一性）
        if (customerDTO.getCustomerName() != null) {
            if (!customer.getCustomerName().equals(customerDTO.getCustomerName())
                    && customerRepository.existsByCustomerName(customerDTO.getCustomerName())) {
                throw new BusinessException("客户名称已存在");
            }
            customer.setCustomerName(customerDTO.getCustomerName());
        }

        // 更新手机号（需检查唯一性）
        if (customerDTO.getPhone() != null) {
            if (!customer.getPhone().equals(customerDTO.getPhone())
                    && customerRepository.existsByPhone(customerDTO.getPhone())) {
                throw new BusinessException("手机号已被使用");
            }
            customer.setPhone(customerDTO.getPhone());
        }

        // 更新其他可选字段
        if (customerDTO.getEmail() != null) {
            customer.setEmail(customerDTO.getEmail());
        }
        if (customerDTO.getCompanyName() != null) {
            customer.setCompanyName(customerDTO.getCompanyName());
        }
        if (customerDTO.getIndustry() != null) {
            customer.setIndustry(customerDTO.getIndustry());
        }
        if (customerDTO.getAddress() != null) {
            customer.setAddress(customerDTO.getAddress());
        }
        if (customerDTO.getRemark() != null) {
            customer.setRemark(customerDTO.getRemark());
        }
        if (customerDTO.getSource() != null) {
            customer.setSource(customerDTO.getSource());
        }
        if (customerDTO.getLevel() != null) {
            customer.setLevel(customerDTO.getLevel());
        }

        return customerRepository.save(customer);
    }

    /**
     * 删除客户（逻辑删除）
     * 实际是将deleted标志设置为true，而非物理删除
     *
     * @param customerId 客户ID
     * @param currentUserId 当前操作用户ID，用于权限校验
     * @throws BusinessException 客户不存在或无权限
     */
    @Transactional
    public void deleteCustomer(Long customerId, Long currentUserId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 权限校验：只有归属人或管理员可以删除
        if (!isAdmin(currentUserId) && !customer.getOwnerUserId().equals(currentUserId)) {
            throw new BusinessException("无权限删除此客户");
        }

        // 逻辑删除：设置deleted标志为true
        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    /**
     * 根据ID获取客户详情
     *
     * @param customerId 客户ID
     * @return 客户对象
     * @throws BusinessException 客户不存在
     */
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));
    }

    /**
     * 分页查询客户列表
     * 支持多种查询条件：关键词搜索、状态筛选、归属人筛选、公海客户筛选
     *
     * @param keyword 关键词，用于客户名称、手机号、公司名搜索
     * @param status 客户状态筛选
     * @param ownerUserId 归属用户ID筛选
     * @param isPublic 是否查询公海客户
     * @param page 页码，从0开始
     * @param size 每页数量
     * @return 分页结果
     */
    public PageResult<Customer> getCustomerList(
            String keyword,
            String status,
            Long ownerUserId,
            Boolean isPublic,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Customer> customerPage;

        // 公海客户查询：无归属人的客户
        if (isPublic != null && isPublic) {
            customerPage = customerRepository.findPublicCustomers(pageable);
        }
        // 按归属人查询
        else if (ownerUserId != null) {
            if (status != null && !status.isEmpty()) {
                customerPage = customerRepository.findByOwnerUserIdAndStatus(ownerUserId, status, pageable);
            } else {
                customerPage = customerRepository.findByOwnerUserId(ownerUserId, pageable);
            }
        }
        // 按状态查询
        else if (status != null && !status.isEmpty()) {
            customerPage = customerRepository.findByStatus(status, pageable);
        }
        // 关键词搜索：匹配客户名称、手机号、公司名
        else if (keyword != null && !keyword.isEmpty()) {
            customerPage = customerRepository.searchCustomers(keyword, pageable);
        }
        // 查询所有客户
        else {
            customerPage = customerRepository.findAll(pageable);
        }

        return new PageResult<>(
                customerPage.getContent(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.getNumber(),
                customerPage.getSize()
        );
    }

    /**
     * 认领公海客户
     * 将无归属人的客户分配给当前用户
     *
     * @param customerId 公海客户ID
     * @param userId 认领用户ID
     * @return 认领后的客户对象
     * @throws BusinessException 客户不存在或不是公海客户
     */
    @Transactional
    public Customer claimPublicCustomer(Long customerId, Long userId) {
        log.info("User {} claiming customer {}", userId, customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 检查是否公海客户（无归属人）
        if (customer.getOwnerUserId() != null) {
            throw new BusinessException("该客户不是公海客户，无法认领");
        }

        // 设置归属人并更新状态为待跟进
        customer.setOwnerUserId(userId);
        customer.setStatus("PENDING");  // 待跟进

        return customerRepository.save(customer);
    }

    /**
     * 退回客户到公海
     * 将有归属人的客户释放为公海客户
     *
     * @param customerId 客户ID
     * @param currentUserId 当前操作用户ID，用于权限校验
     * @return 退回后的客户对象
     * @throws BusinessException 客户不存在或无权限
     */
    @Transactional
    public Customer returnToPublic(Long customerId, Long currentUserId) {
        log.info("Returning customer {} to public pool", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 权限校验
        if (!isAdmin(currentUserId) && !customer.getOwnerUserId().equals(currentUserId)) {
            throw new BusinessException("无权限操作此客户");
        }

        // 清除归属人，设置状态为公海
        customer.setOwnerUserId(null);
        customer.setStatus("PUBLIC");  // 公海

        return customerRepository.save(customer);
    }

    /**
     * 分配客户给指定用户
     * 仅管理员有此权限，用于销售主管分配客户资源
     *
     * @param customerId 客户ID
     * @param targetUserId 目标用户ID（被分配的用户）
     * @param currentUserId 当前操作用户ID（管理员）
     * @return 分配后的客户对象
     * @throws BusinessException 客户不存在或无权限
     */
    @Transactional
    public Customer assignCustomer(Long customerId, Long targetUserId, Long currentUserId) {
        // 权限校验：只有管理员可以分配客户
        if (!isAdmin(currentUserId)) {
            throw new BusinessException("无权限分配客户");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 更新归属人
        customer.setOwnerUserId(targetUserId);
        customer.setStatus("PENDING");

        return customerRepository.save(customer);
    }

    /**
     * 更新客户最后跟进时间
     * 在进行客户跟进时自动调用，记录跟进时间并更新状态为"跟进中"
     *
     * @param customerId 客户ID
     * @throws BusinessException 客户不存在
     */
    @Transactional
    public void updateLastFollowTime(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 更新最后跟进时间并设置状态为跟进中
        customer.setLastFollowTime(LocalDateTime.now());
        customer.setStatus("FOLLOWING");  // 跟进中

        customerRepository.save(customer);
    }

    /**
     * 添加客户标签
     * 用于客户分类和营销管理
     *
     * @param customerId 客户ID
     * @param tagId 标签ID
     * @throws BusinessException 客户或标签不存在
     */
    @Transactional
    public void addTag(Long customerId, Long tagId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        CustomerTag tag = customerTagRepository.findById(tagId)
                .orElseThrow(() -> new BusinessException("标签不存在"));

        // 避免重复添加
        if (!customer.getTags().contains(tag)) {
            customer.getTags().add(tag);
            customerRepository.save(customer);
        }
    }

    /**
     * 移除客户标签
     *
     * @param customerId 客户ID
     * @param tagId 标签ID
     * @throws BusinessException 客户不存在
     */
    @Transactional
    public void removeTag(Long customerId, Long tagId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 移除指定标签
        customer.getTags().removeIf(tag -> tag.getId().equals(tagId));
        customerRepository.save(customer);
    }

    /**
     * 获取客户统计信息
     * 返回各类状态的客户数量统计
     *
     * @return 包含各状态客户数量的统计对象
     */
    public Object getCustomerStats() {
        return new Object() {
            public final long total = customerRepository.count();
            public final long pending = customerRepository.countByStatus("PENDING");
            public final long following = customerRepository.countByStatus("FOLLOWING");
            public final long deal = customerRepository.countByStatus("DEAL");
            public final long archived = customerRepository.countByStatus("ARCHIVED");
            public final long publicPool = customerRepository.countPublicCustomers();
        };
    }

    /**
     * 判断是否为管理员
     * 用于权限校验，目前为临时实现
     *
     * @param userId 用户ID
     * @return true=管理员，false=普通用户
     */
    private boolean isAdmin(Long userId) {
        // TODO: 实现基于角色的权限检查
        return true;  // 临时返回true，生产环境需要查询角色
    }
}
