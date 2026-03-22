package com.crm.repository;

import com.crm.entity.CustomerFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CustomerFollow Repository - 客户跟进记录Repository
 *
 * @author CRM Team
 */
@Repository
public interface CustomerFollowRepository extends JpaRepository<CustomerFollow, Long> {

    /**
     * 根据客户ID查询跟进记录
     */
    List<CustomerFollow> findByCustomerIdOrderByFollowTimeDesc(Long customerId);

    /**
     * 根据客户ID查询跟进记录（分页）
     */
    Page<CustomerFollow> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * 根据跟进人ID查询跟进记录
     */
    List<CustomerFollow> findByFollowUserIdOrderByFollowTimeDesc(Long followUserId);

    /**
     * 查询需要提醒的跟进记录
     */
    @Query("SELECT f FROM CustomerFollow f WHERE f.nextFollowTime IS NOT NULL " +
           "AND f.nextFollowTime <= :now AND f.deleted = false")
    List<CustomerFollow> findFollowReminders(@Param("now") LocalDateTime now);

    /**
     * 统计跟进次数
     */
    long countByCustomerId(Long customerId);

    /**
     * 统计跟进人的跟进次数
     */
    long countByFollowUserId(Long followUserId);
}
