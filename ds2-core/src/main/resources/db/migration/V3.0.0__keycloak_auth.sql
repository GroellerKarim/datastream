-- Add keycloak_id column and make password nullable
ALTER TABLE users ADD COLUMN keycloak_id VARCHAR(255) UNIQUE;
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;
