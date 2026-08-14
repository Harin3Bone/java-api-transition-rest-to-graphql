-- Drop table if exists
DROP TABLE IF EXISTS "transaction" CASCADE;

-- Create transaction table
CREATE TABLE transaction (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    fund_code VARCHAR(50) NOT NULL,
    account BIGINT NOT NULL,
    dealer INTEGER NOT NULL,
    amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
