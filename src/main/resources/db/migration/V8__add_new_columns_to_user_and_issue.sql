-- Add due date to issues
ALTER TABLE issues ADD COLUMN due_date TIMESTAMP;

-- Add avatar and notification preferences to users
ALTER TABLE users ADD COLUMN avatar_url VARCHAR(255);
ALTER TABLE users ADD COLUMN notify_new_task BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE users ADD COLUMN notify_comment BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE users ADD COLUMN notify_mention BOOLEAN DEFAULT FALSE NOT NULL;
