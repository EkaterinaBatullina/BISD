ALTER TABLE task
DROP CONSTRAINT IF EXISTS task_project_fk;

ALTER TABLE task
DROP CONSTRAINT IF EXISTS task_assigned_to_fk;

ALTER TABLE task
    ADD CONSTRAINT task_project_fk
        FOREIGN KEY (project_id)
            REFERENCES project(id)
            ON DELETE CASCADE;

ALTER TABLE task
    ADD CONSTRAINT task_assigned_to_fk
        FOREIGN KEY (assigned_to)
            REFERENCES "user"(id)
            ON DELETE CASCADE;
