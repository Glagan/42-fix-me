CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    market VARCHAR (36) NOT NULL,
    broker VARCHAR (36) NOT NULL,
    broker_order_id INTEGER NOT NULL,
    instrument VARCHAR (36) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL NOT NULL,
    status VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);