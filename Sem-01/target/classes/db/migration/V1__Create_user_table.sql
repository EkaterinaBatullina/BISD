CREATE SEQUENCE user_sequence
    START WITH 100000
    INCREMENT BY 1
    CACHE 50;

CREATE TABLE "user" (
                        id BIGINT NOT NULL DEFAULT nextval('user_sequence'),
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        ---------------------------------------------
                        CONSTRAINT user_id_pk PRIMARY KEY(id),
                        CONSTRAINT user_username_uq UNIQUE (username),
                        CONSTRAINT user_email_uq UNIQUE (email)
);