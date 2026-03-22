package com.crm.repository;

import com.crm.entity.CustomerTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CustomerTag Repository - 客户标签Repository
 *
 * @author CRM Team
 */
@Repository
public interface CustomerTagRepository extends JpaRepository<CustomerTag, Long> {

    /**
     * 根据标签名称查询
     */
    Optional<CustomerTag> findByTagName(String tagName);

    /**
     * 检查标签名称是否存在
     */
    boolean existsByTagName(String tagName);

    /**
     * 根据创建人ID查询标签
     */
    List<CustomerTag> findByCreateUserId(Long createUserId);

    /**
     * 查询所有标签
     */
    List<CustomerTag> findAllByOrderByCreateTimeDesc();
}
