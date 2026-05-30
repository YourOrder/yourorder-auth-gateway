CREATE TABLE IF NOT EXISTS users (
                       id UUID PRIMARY KEY,
                       tenant_id UUID,
                       username VARCHAR(255) UNIQUE,
                       email VARCHAR(255) UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
                                id UUID PRIMARY KEY,
                                user_id UUID NOT NULL,
                                token VARCHAR(255) NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE
);
