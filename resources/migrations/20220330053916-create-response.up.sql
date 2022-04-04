CREATE TABLE response (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID NOT NULL REFERENCES form(id),
    created TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated TIMESTAMP NOT NULL DEFAULT current_timestamp
);
