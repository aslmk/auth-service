CREATE TABLE User_roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO user_roles (role_name) VALUES ('USER'), ('ADMIN');