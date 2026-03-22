-- CRM Database Schema - Updated according to Design Document
-- Create database if not exists

CREATE DATABASE IF NOT EXISTS crm_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE crm_db;

-- Drop existing tables if they exist (for fresh setup)
-- DROP TABLE IF EXISTS crm_sales_performance;
-- DROP TABLE IF EXISTS crm_sales_contract_audit;
-- DROP TABLE IF EXISTS crm_sales_contract;
-- DROP TABLE IF EXISTS crm_sales_business;
-- DROP TABLE IF EXISTS crm_customer_tag_relation;
-- DROP TABLE IF EXISTS crm_customer_follow;
-- DROP TABLE IF EXISTS crm_customer_tag;
-- DROP TABLE IF EXISTS contacts;
-- DROP TABLE IF EXISTS customers;
-- DROP TABLE IF EXISTS users;

-- =====================================================
-- Users table
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    avatar_url VARCHAR(255),
    provider VARCHAR(20),
    provider_id VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_provider (provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Customer table - 客户表 (crm_customer)
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL UNIQUE COMMENT '客户名称',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    industry VARCHAR(50) COMMENT '所属行业',
    address VARCHAR(255) COMMENT '地址',
    owner_user_id BIGINT COMMENT '归属人ID，为空表示公海客户',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '客户状态: PENDING(待跟进), FOLLOWING(跟进中), DEAL(已成交), ARCHIVED(已归档), PUBLIC(公海)',
    last_follow_time DATETIME COMMENT '最后跟进时间',
    email VARCHAR(100) COMMENT '邮箱',
    company_name VARCHAR(200) COMMENT '公司名称',
    remark TEXT COMMENT '备注',
    source VARCHAR(50) COMMENT '客户来源: 线上推广、展会、老客户推荐、自主获取、公海领取',
    level VARCHAR(20) DEFAULT 'NORMAL' COMMENT '客户等级: HIGH(高价值), POTENTIAL(潜力), NORMAL(普通), SLEEPING(沉睡)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_customer_name (customer_name),
    INDEX idx_phone (phone),
    INDEX idx_owner_user (owner_user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Customer Tag table - 客户标签表 (crm_customer_tag)
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_customer_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_desc VARCHAR(255) COMMENT '标签描述',
    create_user_id BIGINT NOT NULL COMMENT '创建人ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_tag_name (tag_name),
    INDEX idx_create_user (create_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Customer-Tag Relation table - 客户标签关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_customer_tag_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE KEY uk_customer_tag (customer_id, tag_id),
    INDEX idx_customer (customer_id),
    INDEX idx_tag (tag_id),
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES crm_customer_tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Customer Follow table - 客户跟进记录表 (crm_customer_follow)
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_customer_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    follow_user_id BIGINT NOT NULL COMMENT '跟进人ID',
    follow_content TEXT NOT NULL COMMENT '跟进内容',
    follow_time DATETIME NOT NULL COMMENT '跟进时间',
    next_follow_time DATETIME COMMENT '下次跟进时间',
    follow_type VARCHAR(20) DEFAULT 'PHONE' COMMENT '跟进类型: PHONE(电话), WECHAT(微信), MEETING(面谈), EMAIL(邮件)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_customer (customer_id),
    INDEX idx_follow_user (follow_user_id),
    INDEX idx_follow_time (follow_time),
    INDEX idx_next_follow_time (next_follow_time),
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Contacts table - 联系人表 (保留原表结构)
-- =====================================================
CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    mobile VARCHAR(20),
    position VARCHAR(100),
    department VARCHAR(100),
    customer_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    notes TEXT,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Business table - 商机表 (crm_sales_business)
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_sales_business (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    business_name VARCHAR(100) NOT NULL COMMENT '商机名称',
    estimate_amount DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '预估金额',
    stage VARCHAR(30) NOT NULL DEFAULT 'INITIAL_CONTACT' COMMENT '商机阶段: INITIAL_CONTACT(初步沟通), REQUIREMENT(需求确认), PROPOSAL(方案报价), NEGOTIATION(商务谈判), WON(成单), FAILED(失败)',
    owner_user_id BIGINT NOT NULL COMMENT '商机归属人ID',
    fail_reason TEXT COMMENT '失败原因',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    archive_time DATETIME COMMENT '归档时间',
    status VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '商机状态: IN_PROGRESS(进行中), WON(已成单), FAILED(已失败)',
    expected_close_date DATE COMMENT '预计成交日期',
    actual_close_date DATE COMMENT '实际成交日期',
    probability INT DEFAULT 0 COMMENT '赢单概率 0-100',
    source VARCHAR(50) COMMENT '商机来源',
    remark TEXT COMMENT '备注',
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_customer (customer_id),
    INDEX idx_owner_user (owner_user_id),
    INDEX idx_stage (stage),
    INDEX idx_status (status),
    INDEX idx_expected_close (expected_close_date),
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Contract table - 合同表 (crm_sales_contract)
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_sales_contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_no VARCHAR(50) NOT NULL UNIQUE COMMENT '合同编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    business_id BIGINT COMMENT '关联商机ID',
    contract_amount DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '合同金额',
    sign_date DATE NOT NULL COMMENT '签订日期',
    valid_start_date DATE COMMENT '合同生效日期',
    valid_end_date DATE COMMENT '合同失效日期',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_AUDIT' COMMENT '合同状态: PENDING_AUDIT(待审核), EFFECTIVE(已生效), REJECTED(已驳回), TERMINATED(已终止)',
    attachment_url VARCHAR(255) COMMENT '合同附件地址',
    create_user_id BIGINT NOT NULL COMMENT '创建人ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    content TEXT COMMENT '合同内容/条款',
    payment_type VARCHAR(20) DEFAULT 'ONE_TIME' COMMENT '付款方式: ONE_TIME(一次性), INSTALLMENT(分期)',
    delivery_days INT COMMENT '交付周期(天)',
    remark TEXT COMMENT '备注',
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_contract_no (contract_no),
    INDEX idx_customer (customer_id),
    INDEX idx_business (business_id),
    INDEX idx_create_user (create_user_id),
    INDEX idx_status (status),
    INDEX idx_valid_dates (valid_start_date, valid_end_date),
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE,
    FOREIGN KEY (business_id) REFERENCES crm_sales_business(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Contract Audit table - 合同审核表 (crm_sales_contract_audit)
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_sales_contract_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    audit_user_id BIGINT NOT NULL COMMENT '审核人ID',
    audit_result VARCHAR(20) NOT NULL COMMENT '审核结果: PASS(通过), REJECT(驳回)',
    audit_opinion TEXT COMMENT '审核意见',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_contract (contract_id),
    INDEX idx_audit_user (audit_user_id),
    INDEX idx_audit_time (created_at),
    FOREIGN KEY (contract_id) REFERENCES crm_sales_contract(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Sales Performance table - 销售业绩表 (crm_sales_performance)
-- =====================================================
CREATE TABLE IF NOT EXISTS crm_sales_performance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '销售用户ID',
    dept_id BIGINT COMMENT '部门ID',
    contract_id BIGINT NOT NULL COMMENT '关联合同ID',
    amount DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '业绩金额',
    performance_date DATE NOT NULL COMMENT '业绩归属日期',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    performance_type VARCHAR(20) DEFAULT 'CONTRACT_PAYMENT' COMMENT '业绩类型: CONTRACT_PAYMENT(合同回款), RENEWAL(续费), OTHER(其他)',
    remark TEXT COMMENT '备注',
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE KEY uk_contract (contract_id),
    INDEX idx_user (user_id),
    INDEX idx_dept (dept_id),
    INDEX idx_performance_date (performance_date),
    FOREIGN KEY (contract_id) REFERENCES crm_sales_contract(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Activities table (保留原表结构)
-- =====================================================
CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    customer_id BIGINT,
    contact_id BIGINT,
    deal_id BIGINT,
    activity_date TIMESTAMP,
    duration INT,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    location VARCHAR(200),
    user_id BIGINT,
    outcome TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE SET NULL,
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE SET NULL,
    FOREIGN KEY (deal_id) REFERENCES crm_sales_business(id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_contact (contact_id),
    INDEX idx_deal (deal_id),
    INDEX idx_activity_date (activity_date),
    INDEX idx_status (status),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
