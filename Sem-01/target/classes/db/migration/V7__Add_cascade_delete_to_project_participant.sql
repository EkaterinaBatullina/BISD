ALTER TABLE project_participant
DROP CONSTRAINT IF EXISTS project_participant_project_fk;

ALTER TABLE project_participant
DROP CONSTRAINT IF EXISTS project_participant_user_fk;

ALTER TABLE project_participant
    ADD CONSTRAINT project_participant_project_fk
        FOREIGN KEY (project_id)
            REFERENCES project(id)
            ON DELETE CASCADE;

ALTER TABLE project_participant
    ADD CONSTRAINT project_participant_user_fk
        FOREIGN KEY (user_id)
            REFERENCES "user"(id)
            ON DELETE CASCADE;
