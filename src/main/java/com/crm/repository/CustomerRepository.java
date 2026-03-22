package com.crm.repository;

import com.crm.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 客户数据访问层
 * 继承JpaRepository，提供客户的数据库操作方法
 *
 * @author CRM Team
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * 根据客户名称精确查询客户
     *
     * @param customerName 客户名称
     * @return 客户对象（可选）
     */
    Optional<Customer> findByCustomerName(String customerName);

    /**
     * 根据手机号精确查询客户
     *
     * @param phone 手机号
     * @return 客户对象（可选）
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * 检查客户名称是否已存在（用于创建时的唯一性校验）
     *
     * @param customerName 客户名称
     * @return true=存在，false=不存在
     */
    boolean existsByCustomerName(String customerName);

    /**
     * 检查手机号是否已存在（用于创建时的唯一性校验）
     *
     * @param phone 手机号
     * @return true=存在，false=不存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据状态查询客户列表（不分页）
     *
     * @param status 客户状态
     * @return 客户列表
     */
    List<Customer> findByStatus(String status);

    /**
     * 根据状态分页查询客户列表
     *
     * @param status 客户状态
     * @param pageable 分页参数
     * @return 分页客户列表
     */
    Page<Customer> findByStatus(String status, Pageable pageable);

    /**
     * 根据归属人ID分页查询客户列表
     *
     * @param ownerUserId 归属用户ID
     * @param pageable 分页参数
     * @return 分页客户列表
     */
    Page<Customer> findByOwnerUserId(Long ownerUserId, Pageable pageable);

    /**
     * 查询公海客户（无归属人的客户）
     * 使用JPQL自定义查询，排除已逻辑删除的客户
     *
     * @param pageable 分页参数
     * @return 分页公海客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.ownerUserId IS NULL AND c.deleted = false")
    Page<Customer> findPublicCustomers(Pageable pageable);

    /**
     * 根据关键词搜索客户
     * 匹配客户名称、手机号、公司名（不区分大小写）
     *
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页搜索结果
     */
    @Query("SELECT c FROM Customer c WHERE c.deleted = false AND " +
           "(LOWER(c.customerName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据归属人和状态组合查询客户
     *
     * @param ownerUserId 归属用户ID
     * @param status 客户状态
     * @param pageable 分页参数
     * @return 分页客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.ownerUserId = :ownerUserId AND c.status = :status AND c.deleted = false")
    Page<Customer> findByOwnerUserIdAndStatus(
        @Param("ownerUserId") Long ownerUserId,
        @Param("status") String status,
        Pageable pageable
    );

    /**
     * 统计指定状态的客户数量
     *
     * @param status 客户状态
     * @return 客户数量
     */
    long countByStatus(String status);

    /**
     * 统计公海客户数量（无归属人的客户）
     *
     * @return 公海客户数量
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.ownerUserId IS NULL AND c.deleted = false")
    long countPublicCustomers();
}
