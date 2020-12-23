
-- https://dzone.com/articles/using-jsonb-in-postgresql-how-to-effectively-store
CREATE TABLE eventstore(
    id UUID,
    at timestamp,
    payload JSONB,
    PRIMARY KEY (id)
);

-- Allow jsonb_ops operator class: ?, ?|, ?&, @>, @@, @? [Index each key and value in the JSONB element]
CREATE INDEX eventpayloadgin ON eventstore USING gin (payload);
-- Allow existence operators in nested documents
CREATE INDEX eventpayloadtypegin ON eventstore USING gin (payload->'type');