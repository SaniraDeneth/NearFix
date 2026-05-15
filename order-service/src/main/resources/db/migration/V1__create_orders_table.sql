CREATE TABLE orders (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    provider_id UUID NOT NULL,
    gig_id UUID NOT NULL,
    category_id UUID NOT NULL,
    service_mode VARCHAR(50) NOT NULL,
    base_price DECIMAL(19, 2) NOT NULL,
    travel_fee DECIMAL(19, 2) DEFAULT 0,
    total_price DECIMAL(19, 2) NOT NULL,
    client_address TEXT,
    client_latitude DOUBLE PRECISION,
    client_longitude DOUBLE PRECISION,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
