package com.crm.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 客户跟进记录实体类
 * 对应数据库表 crm_customer_follow
 * 记录与客户的所有沟通跟进历史，是销售过程管理的重要组成部分
 *
 * @author CRM Team
 */
@Entity
@Table(name = "crm_customer_follow")
@EntityListeners(AuditingEntityListener.class)
public class CustomerFollow extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "follow_user_id", nullable = false)
    private Long followUserId;

    @Column(name = "follow_content", nullable = false, columnDefinition = "TEXT")
    private String followContent;

    @Column(name = "follow_time", nullable = false)
    private LocalDateTime followTime;

    @Column(name = "next_follow_time")
    private LocalDateTime nextFollowTime;

    @Column(name = "follow_type", length = 20)
    private String followType = "PHONE";

    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    // Getter and Setter methods

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(Long followUserId) {
        this.followUserId = followUserId;
    }

    public String getFollowContent() {
        return followContent;
    }

    public void setFollowContent(String followContent) {
        this.followContent = followContent;
    }

    public LocalDateTime getFollowTime() {
        return followTime;
    }

    public void setFollowTime(LocalDateTime followTime) {
        this.followTime = followTime;
    }

    public LocalDateTime getNextFollowTime() {
        return nextFollowTime;
    }

    public void setNextFollowTime(LocalDateTime nextFollowTime) {
        this.nextFollowTime = nextFollowTime;
    }

    public String getFollowType() {
        return followType;
    }

    public void setFollowType(String followType) {
        this.followType = followType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
