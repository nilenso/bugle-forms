CREATE TABLE IF NOT EXISTS form (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    owner UUID NOT NULL REFERENCES user_account(id),
    created TIMESTAMP NOT NULL DEFAULT current_timestamp
);
