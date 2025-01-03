CREATE TABLE project_participant (
                                     project_id BIGINT NOT NULL,
                                     user_id BIGINT NOT NULL,
                                     CONSTRAINT project_participant_pk PRIMARY KEY (project_id, user_id),
                                     CONSTRAINT project_participant_project_fk FOREIGN KEY (project_id) REFERENCES project(id),
                                     CONSTRAINT project_participant_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id)
);

INSERT INTO project_participant (project_id, user_id)
SELECT DISTINCT project_id, assigned_to
FROM task
WHERE assigned_to IS NOT NULL;

INSERT INTO project_participant (project_id, user_id)
SELECT DISTINCT p.id, p.owner_id
FROM project p
WHERE p.owner_id IS NOT NULL
    ON CONFLICT DO NOTHING;