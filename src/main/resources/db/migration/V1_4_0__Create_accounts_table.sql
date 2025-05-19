CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    refresh_token VARCHAR(255),
    access_token VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    user_id INTEGER REFERENCES users (id)
);