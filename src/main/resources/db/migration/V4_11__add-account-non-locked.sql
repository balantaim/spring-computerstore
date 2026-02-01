ALTER TABLE users
    ADD COLUMN account_non_locked BIT NOT NULL DEFAULT 1;