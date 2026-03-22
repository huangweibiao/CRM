package com.crm.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户实体类
 * 对应数据库表 crm_customer
 * 表示客户公司信息，是CRM系统的核心业务数据
 *
 * @author CRM Team
 */
@Entity
@Table(name = "crm_customer")
public class Customer extends BaseEntity {

    @Column(name = "customer_name", nullable = false, length = 100, unique = true)
    private String customerName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "industry", length = 50)
    private String industry;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "last_follow_time")
    private LocalDateTime lastFollowTime;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "level", length = 20)
    private String level = "NORMAL";

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerFollow> follows = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Business> businesses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "crm_customer_tag_relation",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<CustomerTag> tags = new ArrayList<>();

    // Getter and Setter methods

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastFollowTime() {
        return lastFollowTime;
    }

    public void setLastFollowTime(LocalDateTime lastFollowTime) {
        this.lastFollowTime = lastFollowTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<CustomerFollow> getFollows() {
        return follows;
    }

    public void setFollows(List<CustomerFollow> follows) {
        this.follows = follows;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    public List<CustomerTag> getTags() {
        return tags;
    }

    public void setTags(List<CustomerTag> tags) {
        this.tags = tags;
    }
}
