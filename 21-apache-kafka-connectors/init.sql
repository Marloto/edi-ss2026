-- init.sql
CREATE TABLE orders (
  id         SERIAL PRIMARY KEY,
  customer   VARCHAR(100),
  product    VARCHAR(100),
  amount     NUMERIC,
  created_at TIMESTAMP DEFAULT now()
);

ALTER TABLE orders REPLICA IDENTITY FULL;