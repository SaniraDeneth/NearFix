ALTER TABLE gigs
    ADD COLUMN updated_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_gigs_location
    ON gigs
    USING GIST (location);