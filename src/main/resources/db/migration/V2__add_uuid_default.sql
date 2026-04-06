CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE users
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE refresh_tokens
    ALTER COLUMN id SET DEFAULT gen_random_uuid();