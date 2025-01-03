CREATE SEQUENCE task_sequence
    START WITH 100000
    INCREMENT BY 1
    CACHE 50;

CREATE TABLE task (
                      id BIGINT NOT NULL DEFAULT nextval('task_sequence'),
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      status VARCHAR(50) DEFAULT 'TODO',
                      project_id BIGINT NOT NULL,
                      assigned_to BIGINT,
                      -----------------------------------------------------------
                      CONSTRAINT task_id_pk PRIMARY KEY(id),
                      CONSTRAINT task_project_fk FOREIGN KEY (project_id)
                          REFERENCES project(id),
                      CONSTRAINT task_assigned_to_fk FOREIGN KEY (assigned_to)
                          REFERENCES "user"(id)
);
