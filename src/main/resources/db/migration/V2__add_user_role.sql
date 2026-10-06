-- Adds role-based access. Existing and newly registered users are regular users;
-- admins are created by AdminBootstrapService or promoted manually:
--   UPDATE users SET role = 'ADMIN' WHERE username = '...';

ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER' NOT NULL;

ALTER TABLE users ADD CONSTRAINT ck_users_role CHECK (role IN ('USER', 'ADMIN'));
