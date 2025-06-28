-- Initialize database with basic structure
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create sequences for ID generation
CREATE SEQUENCE IF NOT EXISTS user_seq START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS role_seq START 1 INCREMENT 1;
