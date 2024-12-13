CREATE SEQUENCE inventory_items_sequence
    START WITH 100000
    INCREMENT BY 1
    CACHE 50;

CREATE TABLE inventory_items (
                                 id BIGINT NOT NULL DEFAULT nextval('inventory_items_sequence'),
                                 item_name VARCHAR(255) NOT NULL,
                                 parameters TEXT,
                                 age INT,
                                 placement_conditions VARCHAR(255) NOT NULL
);