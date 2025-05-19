ALTER TABLE users
    ADD COLUMN picture_url VARCHAR(255),
    ADD COLUMN is_verified BOOLEAN,
    ADD COLUMN is_two_factor_enabled BOOLEAN,
    ADD COLUMN auth_method VARCHAR(50),
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP;