CREATE TABLE ticket_events
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id   BIGINT                   NOT NULL,
    event_type  VARCHAR(30)              NOT NULL,
    CONSTRAINT chk_event_type
        CHECK (event_type IN ('CREATED','ASSIGNED','REASSIGNED','WORK_STARTED','BLOCKED','COMPLETED')),
    actor_type  VARCHAR(30)              NOT NULL,
    CONSTRAINT chk_actor_type
        CHECK (actor_type IN ('STUDENT','SPECIALIST','ADMIN','SYSTEM')),
    actor_id    BIGINT NULL,
    details     VARCHAR(1000) NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_ticket_events_ticket
        FOREIGN KEY (ticket_id)
            REFERENCES tickets(id),

    CONSTRAINT chk_ticket_events_actor
        CHECK (
            (actor_type = 'SYSTEM' AND actor_id IS NULL)
                OR
            (
                actor_type IN ('STUDENT', 'SPECIALIST', 'ADMIN')
                    AND actor_id IS NOT NULL
                    AND actor_id > 0
                )
            )
);