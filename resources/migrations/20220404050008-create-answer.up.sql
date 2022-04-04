CREATE TABLE answer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    response_id UUID NOT NULL REFERENCES response(id),
    question_id UUID NOT NULL REFERENCES question(id),
    text text,
    created TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated TIMESTAMP NOT NULL DEFAULT current_timestamp
);
