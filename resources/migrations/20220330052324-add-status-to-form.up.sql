CREATE TYPE form_status AS ENUM ('draft', 'published');
--;;
ALTER TABLE form ADD COLUMN status form_status NOT NULL DEFAULT 'draft';
