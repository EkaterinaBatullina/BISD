CREATE SEQUENCE project_sequence
    START WITH 100000
    INCREMENT BY 1
    CACHE 50;

CREATE TABLE project (
                         id BIGINT NOT NULL DEFAULT nextval('project_sequence'),
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         owner_id BIGINT NOT NULL,
                         is_private BOOLEAN DEFAULT FALSE,
                         --------------------------------------------------
                         CONSTRAINT project_id_pk PRIMARY KEY(id),
                         CONSTRAINT project_name_uq UNIQUE (name),
                         CONSTRAINT project_owner_fk FOREIGN KEY (owner_id)
                             REFERENCES "user"(id)
);
