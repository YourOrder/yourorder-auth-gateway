CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       tenant_id UUID,
                       username VARCHAR(255),
                       email VARCHAR(255),
                       password VARCHAR(255),
                       role VARCHAR(50),
                       created_at TIMESTAMP
);

CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY,
                                user_id UUID,
                                token VARCHAR(255),
                                expires_at TIMESTAMP,
                                revoked BOOLEAN
);