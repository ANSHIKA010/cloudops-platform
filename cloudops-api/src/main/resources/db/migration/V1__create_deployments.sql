CREATE TABLE deployments (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    image_reference VARCHAR(255) NOT NULL,
    replicas INTEGER NOT NULL,
    status VARCHAR(32) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_deployments_replicas CHECK (replicas BETWEEN 1 AND 5),
    CONSTRAINT ck_deployments_status CHECK (status IN ('REQUESTED', 'DEPLOYING', 'RUNNING')),
    CONSTRAINT ck_deployments_timestamp_order CHECK (updated_at >= created_at)
);
