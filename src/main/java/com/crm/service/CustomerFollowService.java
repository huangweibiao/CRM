package com.crm.service;

import com.crm.entity.CustomerFollow;
import com.crm.entity.Customer;
import com.crm.exception.BusinessException;
import com.crm.repository.CustomerFollowRepository;
import com.crm.repository.CustomerRepository;
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
 * CustomerFollow Service - 客户跟进服务
 *
 * @author CRM Team
 */
@Service
public class CustomerFollowService {

    private static final Logger log = LoggerFactory.getLogger(CustomerFollowService.class);

    private final CustomerFollowRepository customerFollowRepository;
    private final CustomerRepository customerRepository;

    public CustomerFollowService(CustomerFollowRepository customerFollowRepository,
                                 CustomerRepository customerRepository) {
        this.customerFollowRepository = customerFollowRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * 创建跟进记录
     */
    @Transactional
    public CustomerFollow createFollow(Long customerId, Long followUserId,
                                       String followContent, String followType,
                                       LocalDateTime nextFollowTime) {
        log.info("Creating follow record for customer: {}", customerId);

        // 校验客户是否存在
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));

        // 权限校验：只有归属人或管理员可以跟进
        // TODO: 实现基于角色的权限检查

        // 创建跟进记录
        CustomerFollow follow = new CustomerFollow();
        follow.setCustomerId(customerId);
        follow.setFollowUserId(followUserId);
        follow.setFollowContent(followContent);
        follow.setFollowType(followType);
        follow.setFollowTime(LocalDateTime.now());
        follow.setNextFollowTime(nextFollowTime);

        CustomerFollow savedFollow = customerFollowRepository.save(follow);

        // 更新客户最后跟进时间
        customer.setLastFollowTime(LocalDateTime.now());
        customer.setStatus("FOLLOWING");  // 跟进中
        customerRepository.save(customer);

        return savedFollow;
    }

    /**
     * 获取客户的跟进记录列表
     */
    public List<CustomerFollow> getFollowsByCustomerId(Long customerId) {
        return customerFollowRepository.findByCustomerIdOrderByFollowTimeDesc(customerId);
    }

    /**
     * 分页获取客户跟进记录
     */
    public Page<CustomerFollow> getFollowsByCustomerId(Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("followTime").descending());
        return customerFollowRepository.findByCustomerId(customerId, pageable);
    }

    /**
     * 获取跟进人的跟进记录
     */
    public List<CustomerFollow> getFollowsByUserId(Long userId) {
        return customerFollowRepository.findByFollowUserIdOrderByFollowTimeDesc(userId);
    }

    /**
     * 获取需要提醒的跟进记录
     */
    public List<CustomerFollow> getFollowReminders() {
        return customerFollowRepository.findFollowReminders(LocalDateTime.now());
    }

    /**
     * 统计客户跟进次数
     */
    public long countFollowsByCustomerId(Long customerId) {
        return customerFollowRepository.countByCustomerId(customerId);
    }

    /**
     * 统计用户跟进次数
     */
    public long countFollowsByUserId(Long userId) {
        return customerFollowRepository.countByFollowUserId(userId);
    }
}
