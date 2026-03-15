-- Sample Data Insert Script
-- This script will be executed after schema creation

USE crm_db;

-- Additional sample customers
INSERT INTO customers (name, company_name, email, phone, address, industry, status, assigned_user_id, revenue) VALUES
('TechStart Solutions', 'TechStart Solutions', 'info@techstart.com', '+1-555-0300', '100 Tech Park, San Francisco, CA 94105', 'Technology', 'ACTIVE', 2, 15000.00),
('Green Energy Ltd', 'Green Energy Ltd', 'contact@greenenergy.com', '+1-555-0301', '200 Green Way, Austin, TX 78701', 'Energy', 'ACTIVE', 1, 25000.00),
('HealthPlus Inc', 'HealthPlus Inc', 'support@healthplus.com', '+1-555-0302', '300 Medical Blvd, Boston, MA 02101', 'Healthcare', 'INACTIVE', 2, 0.00)
ON DUPLICATE KEY UPDATE name=name;

-- Additional sample contacts
INSERT INTO contacts (first_name, last_name, email, phone, mobile, position, department, customer_id, is_primary) VALUES
('Emma', 'Brown', 'emma.brown@techstart.com', '+1-555-0400', '+1-555-0401', 'Founder', 'Executive', 5, TRUE),
('Frank', 'Garcia', 'frank.garcia@greenenergy.com', '+1-555-0402', '+1-555-0403', 'Director', 'Sales', 6, TRUE),
('Grace', 'Lee', 'grace.lee@healthplus.com', '+1-555-0404', '+1-555-0405', 'Manager', 'Operations', 7, TRUE)
ON DUPLICATE KEY UPDATE first_name=first_name;

-- Additional sample deals
INSERT INTO deals (title, description, amount, currency, customer_id, stage, probability, expected_close_date, status, assigned_user_id, source, priority) VALUES
('Startup Package', 'Complete CRM package for startup', 15000.00, 'USD', 5, 'PROPOSAL', 40, DATE_ADD(CURRENT_DATE, INTERVAL 20 DAY), 'OPEN', 2, 'COLD CALL', 'MEDIUM'),
('Solar Installation', 'Commercial solar panel installation', 100000.00, 'USD', 6, 'NEGOTIATION', 60, DATE_ADD(CURRENT_DATE, INTERVAL 15 DAY), 'OPEN', 1, 'REFERRAL', 'HIGH')
ON DUPLICATE KEY UPDATE title=title;

-- Additional sample activities
INSERT INTO activities (type, title, description, customer_id, contact_id, deal_id, activity_date, duration, status, user_id) VALUES
('CALL', 'Cold Call Introduction', 'Initial introduction to TechStart', 5, 5, NULL, DATE_ADD(NOW(), INTERVAL 4 HOUR), 15, 'COMPLETED', 2),
('MEETING', 'Solar Assessment', 'On-site solar feasibility assessment', 6, 6, 6, DATE_ADD(NOW(), INTERVAL 5 DAY), 120, 'SCHEDULED', 1)
ON DUPLICATE KEY UPDATE title=title;
