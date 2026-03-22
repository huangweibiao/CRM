-- Sample Data Insert Script - Updated for new schema
-- This script will be executed after schema creation

USE crm_db;

-- Insert sample users
INSERT INTO users (username, email, full_name, role, provider, provider_id) VALUES
('admin', 'admin@crm.com', 'System Administrator', 'ADMIN', 'oauth2', 'admin123'),
('sales1', 'sales1@crm.com', 'John Smith', 'SALES', 'oauth2', 'sales1'),
('sales2', 'sales2@crm.com', 'Jane Doe', 'SALES', 'oauth2', 'sales2'),
('manager', 'manager@crm.com', 'Sales Manager', 'MANAGER', 'oauth2', 'manager')
ON DUPLICATE KEY UPDATE username=username;

-- Insert sample customer tags
INSERT INTO crm_customer_tag (tag_name, tag_desc, create_user_id) VALUES
('高价值', '高价值客户', 1),
('潜在客户', '有潜力的大客户', 1),
('老客户', '长期合作客户', 1),
('重点跟进', '需要重点跟进的客户', 1),
('VIP', 'VIP客户', 1)
ON DUPLICATE KEY UPDATE tag_name=tag_name;

-- Insert sample customers
INSERT INTO crm_customer (customer_name, phone, email, company_name, industry, address, owner_user_id, status, source, level, remark) VALUES
('阿里巴巴', '+86-571-85022000', 'contact@alibaba.com', '阿里巴巴集团', '互联网', '杭州市余杭区', 1, 'FOLLOWING', '展会', 'HIGH', '重点客户'),
('腾讯科技', '+86-755-86013388', 'contact@tencent.com', '腾讯科技', '互联网', '深圳市南山区', 1, 'FOLLOWING', '老客户推荐', 'HIGH', '重要客户'),
('华为技术', '+86-755-28780808', 'contact@huawei.com', '华为技术', '通信', '深圳市龙岗区', 2, 'FOLLOWING', '线上推广', 'POTENTIAL', '待跟进'),
('京东集团', '+86-10-89111111', 'contact@jd.com', '京东集团', '电商', '北京市亦庄', NULL, 'PUBLIC', '自主获取', 'NORMAL', '公海客户'),
('美团', '+86-10-65208888', 'contact@meituan.com', '美团', '本地生活', '北京市朝阳区', NULL, 'PUBLIC', '线上推广', 'NORMAL', '公海客户'),
('字节跳动', '+86-10-65056666', 'contact@bytedance.com', '字节跳动', '互联网', '北京市海淀区', 2, 'DEAL', '展会', 'HIGH', '已成交客户')
ON DUPLICATE KEY UPDATE customer_name=customer_name;

-- Insert sample customer follow records
INSERT INTO crm_customer_follow (customer_id, follow_user_id, follow_content, follow_time, next_follow_time, follow_type) VALUES
(1, 1, '初次沟通，客户表示有采购意向', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 'PHONE'),
(1, 1, '已发送产品方案，等待客户反馈', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'EMAIL'),
(2, 1, '客户要求提供详细报价', NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), 'WECHAT'),
(3, 2, '客户暂时没有采购计划', NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'MEETING'),
(3, 2, '客户表示感兴趣，安排下次沟通', NOW(), DATE_ADD(NOW(), INTERVAL 10 DAY), 'PHONE')
ON DUPLICATE KEY UPDATE customer_id=customer_id;

-- Insert customer-tag relations
INSERT INTO crm_customer_tag_relation (customer_id, tag_id) VALUES
(1, 1), (1, 5),
(2, 1), (2, 2),
(3, 2),
(5, 3)
ON DUPLICATE KEY UPDATE customer_id=customer_id;

-- Insert sample contacts
INSERT INTO contacts (first_name, last_name, email, phone, mobile, position, department, customer_id, is_primary) VALUES
('张三', '采购经理', 'zhangsan@alibaba.com', '+86-571-85022001', '+86-13800001111', '采购经理', '采购部', 1, TRUE),
('李四', '技术负责人', 'lisi@alibaba.com', '+86-571-85022002', '+86-13800002222', '技术负责人', '技术部', 1, FALSE),
('王五', '运营总监', 'wangwu@tencent.com', '+86-755-86013389', '+86-13800003333', '运营总监', '运营部', 2, TRUE),
('赵六', '项目经理', 'zhaoliu@huawei.com', '+86-755-28780809', '+86-13800004444', '项目经理', '项目部', 3, TRUE)
ON DUPLICATE KEY UPDATE first_name=first_name;

-- Insert sample businesses (opportunities)
INSERT INTO crm_sales_business (customer_id, business_name, estimate_amount, stage, owner_user_id, status, expected_close_date, source, probability, remark) VALUES
(1, '企业SaaS采购', 500000.00, 'PROPOSAL', 1, 'IN_PROGRESS', DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), '展会', 60, '客户要求提供详细方案'),
(1, '企业邮箱采购', 50000.00, 'INITIAL_CONTACT', 1, 'IN_PROGRESS', DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY), '线上推广', 20, '初步沟通阶段'),
(2, '企业微信年度合同', 300000.00, 'NEGOTIATION', 1, 'IN_PROGRESS', DATE_ADD(CURRENT_DATE, INTERVAL 15 DAY), '老客户推荐', 80, '商务谈判中'),
(3, '云计算服务采购', 200000.00, 'INITIAL_CONTACT', 2, 'IN_PROGRESS', DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), '线上推广', 30, '需求了解中'),
(6, '企业培训服务', 150000.00, 'WON', 2, 'WON', CURRENT_DATE, '展会', 100, '已成交')
ON DUPLICATE KEY UPDATE business_name=business_name;

-- Insert sample contracts
INSERT INTO crm_sales_contract (contract_no, customer_id, business_id, contract_amount, sign_date, valid_start_date, valid_end_date, status, create_user_id, payment_type, delivery_days, remark) VALUES
('CT-2024-001', 6, 5, 150000.00, '2024-01-15', '2024-01-15', '2025-01-14', 'EFFECTIVE', 2, 'ONE_TIME', 365, '年度培训服务合同'),
('CT-2024-002', 1, NULL, 80000.00, '2024-02-01', '2024-02-01', NULL, 'PENDING_AUDIT', 1, 'INSTALLMENT', 180, '等待审核')
ON DUPLICATE KEY UPDATE contract_no=contract_no;

-- Insert sample contract audits
INSERT INTO crm_sales_contract_audit (contract_id, audit_user_id, audit_result, audit_opinion) VALUES
(1, 3, 'PASS', '合同内容合规，同意通过')
ON DUPLICATE KEY UPDATE contract_id=contract_id;

-- Insert sample sales performance
INSERT INTO crm_sales_performance (user_id, contract_id, amount, performance_date, performance_type) VALUES
(2, 1, 150000.00, '2024-01-15', 'CONTRACT_PAYMENT')
ON DUPLICATE KEY UPDATE contract_id=contract_id;

-- Insert sample activities
INSERT INTO activities (type, title, description, customer_id, contact_id, deal_id, activity_date, duration, status, user_id) VALUES
('MEETING', '客户拜访', '拜访阿里巴巴，洽谈合作', 1, 1, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 120, 'SCHEDULED', 1),
('CALL', '电话回访', '回访腾讯客户，确认需求', 2, 3, 3, DATE_ADD(NOW(), INTERVAL 2 DAY), 30, 'SCHEDULED', 1),
('EMAIL', '发送报价', '发送详细报价方案', 1, 2, 1, DATE_ADD(NOW(), INTERVAL 3 DAY), 0, 'SCHEDULED', 1)
ON DUPLICATE KEY UPDATE title=title;
