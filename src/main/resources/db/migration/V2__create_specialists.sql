CREATE TABLE specialists
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    location  VARCHAR(255) NOT NULL,
    level     VARCHAR(30)  NOT NULL,
    CONSTRAINT chk_specialists_level
        CHECK (level IN ('JUNIOR', 'MIDDLE', 'SENIOR')),
    status    VARCHAR(30)  NOT NULL,
    CONSTRAINT chk_specialists_status
        CHECK (status IN ('AVAILABLE', 'BUSY', 'OFF_DUTY')),
    rating    NUMERIC(3, 2) NULL,
    CONSTRAINT chk_specialists_rating
        CHECK (rating IS NULL OR rating BETWEEN 0.00 AND 5.00)
);