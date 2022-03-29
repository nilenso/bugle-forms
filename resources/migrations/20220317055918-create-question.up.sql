CREATE TABLE IF NOT EXISTS question (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID NOT NULL REFERENCES form(id),
    text text NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated TIMESTAMP NOT NULL DEFAULT current_timestamp
);
