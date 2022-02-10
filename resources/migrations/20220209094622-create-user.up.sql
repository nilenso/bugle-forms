CREATE TABLE IF NOT EXISTS user_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    email text NOT NULL UNIQUE,
    password text NOT NULL
);
