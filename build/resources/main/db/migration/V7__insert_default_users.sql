-- Default admin user: admin@jiralike.com / Admin@123
-- Password hash generated with BCrypt (strength 10)
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
VALUES (
    'Administrator',
    'admin@jiralike.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Sample regular user: user@jiralike.com / User@123
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
VALUES (
    'João Silva',
    'user@jiralike.com',
    '$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77bqin',
    'USER',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
