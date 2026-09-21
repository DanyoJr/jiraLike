CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    "key" VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    owner_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE INDEX idx_projects_key ON projects("key");
CREATE INDEX idx_projects_owner ON projects(owner_id);
CREATE INDEX idx_projects_is_active ON projects(is_active);
