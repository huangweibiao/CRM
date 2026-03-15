-- CRM Database Initialization Script
-- Create database if not exists

CREATE DATABASE IF NOT EXISTS crm_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE crm_db;

-- Users table
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

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    company_name VARCHAR(200),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(500),
    industry VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    notes TEXT,
    assigned_user_id BIGINT,
    revenue DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_company_name (company_name),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_assigned_user (assigned_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Contacts table
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
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Deals table
CREATE TABLE IF NOT EXISTS deals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    amount DOUBLE,
    currency VARCHAR(3) DEFAULT 'USD',
    customer_id BIGINT NOT NULL,
    stage VARCHAR(50) DEFAULT 'PROSPECT',
    probability INT DEFAULT 0,
    expected_close_date DATE,
    actual_close_date DATE,
    status VARCHAR(20) DEFAULT 'OPEN',
    assigned_user_id BIGINT,
    source VARCHAR(50),
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id),
    INDEX idx_status (status),
    INDEX idx_stage (stage),
    INDEX idx_assigned_user (assigned_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Activities table
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
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE SET NULL,
    FOREIGN KEY (deal_id) REFERENCES deals(id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_contact (contact_id),
    INDEX idx_deal (deal_id),
    INDEX idx_activity_date (activity_date),
    INDEX idx_status (status),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO users (username, email, full_name, role, provider, provider_id) VALUES
('admin', 'admin@crm.com', 'Administrator', 'ADMIN', 'oauth2', 'admin123'),
('user1', 'user1@crm.com', 'John Smith', 'USER', 'oauth2', 'user123')
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO customers (name, company_name, email, phone, address, industry, status, assigned_user_id, revenue) VALUES
('Acme Corporation', 'Acme Corporation', 'contact@acme.com', '+1-555-0100', '123 Business Ave, New York, NY 10001', 'Technology', 'ACTIVE', 1, 50000.00),
('Globex Inc', 'Globex Inc', 'info@globex.com', '+1-555-0101', '456 Enterprise Blvd, Los Angeles, CA 90001', 'Manufacturing', 'ACTIVE', 1, 75000.00),
('Soylent Corp', 'Soylent Corp', 'hello@soylent.com', '+1-555-0102', '789 Innovation Dr, Chicago, IL 60601', 'Food & Beverage', 'ACTIVE', 2, 30000.00)
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO contacts (first_name, last_name, email, phone, mobile, position, department, customer_id, is_primary) VALUES
('Alice', 'Johnson', 'alice.johnson@acme.com', '+1-555-0200', '+1-555-0201', 'CEO', 'Executive', 1, TRUE),
('Bob', 'Williams', 'bob.williams@acme.com', '+1-555-0202', '+1-555-0203', 'CTO', 'Technology', 1, FALSE),
('Carol', 'Davis', 'carol.davis@globex.com', '+1-555-0204', '+1-555-0205', 'VP Sales', 'Sales', 2, TRUE),
('David', 'Miller', 'david.miller@soylent.com', '+1-555-0206', '+1-555-0207', 'COO', 'Operations', 3, TRUE)
ON DUPLICATE KEY UPDATE first_name=first_name;

INSERT INTO deals (title, description, amount, currency, customer_id, stage, probability, expected_close_date, status, assigned_user_id, source, priority) VALUES
('Enterprise Software License', 'Annual enterprise software license agreement', 50000.00, 'USD', 1, 'NEGOTIATION', 70, DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), 'OPEN', 1, 'REFERRAL', 'HIGH'),
('Manufacturing Equipment', 'New manufacturing equipment purchase', 75000.00, 'USD', 2, 'PROPOSAL', 50, DATE_ADD(CURRENT_DATE, INTERVAL 45 DAY), 'OPEN', 1, 'WEBSITE', 'MEDIUM'),
('Supply Contract', 'Annual supply contract for ingredients', 30000.00, 'USD', 3, 'QUALIFICATION', 30, DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY), 'OPEN', 2, 'TRADE SHOW', 'LOW')
ON DUPLICATE KEY UPDATE title=title;

INSERT INTO activities (type, title, description, customer_id, contact_id, deal_id, activity_date, duration, status, user_id) VALUES
('MEETING', 'Initial Discovery Meeting', 'Discuss requirements and timeline', 1, 1, 1, DATE_ADD(NOW(), INTERVAL 1 HOUR), 60, 'COMPLETED', 1),
('CALL', 'Follow-up Call', 'Follow up on proposal feedback', 1, 1, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 30, 'SCHEDULED', 1),
('EMAIL', 'Send Quote', 'Send detailed quote for manufacturing equipment', 2, 3, 2, DATE_ADD(NOW(), INTERVAL 2 HOUR), 0, 'COMPLETED', 1),
('MEETING', 'Product Demo', 'Demonstrate product capabilities', 3, 4, 3, DATE_ADD(NOW(), INTERVAL 3 DAY), 90, 'SCHEDULED', 2)
ON DUPLICATE KEY UPDATE title=title;
