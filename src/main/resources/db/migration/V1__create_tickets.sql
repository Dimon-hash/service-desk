CREATE TABLE tickets (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         student_id BIGINT NOT NULL,
                         description VARCHAR(2000) NOT NULL,
                         location VARCHAR(255) NOT NULL,
                         status VARCHAR(30) NOT NULL,
                         priority VARCHAR(30) NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                         assigned_specialist_id BIGINT NULL,
                         failure_reason VARCHAR(1000) NULL
);