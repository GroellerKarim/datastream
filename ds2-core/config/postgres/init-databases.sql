-- Create the datastream database and user
-- (The default keycloak database/user is created by POSTGRES_DB/POSTGRES_USER env vars)

CREATE USER datastream WITH PASSWORD 'datastream';
CREATE DATABASE datastream OWNER datastream;

GRANT ALL PRIVILEGES ON DATABASE datastream TO datastream;
