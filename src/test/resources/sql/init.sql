CREATE SCHEMA IF NOT EXISTS test;
SET search_path TO test;
CREATE TABLE sample_entity(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    some_value TEXT
);
INSERT INTO sample_entity( some_value)
VALUES
    ('val1'), -- TODO: rename
    ('val1'),
    ('val2');

INSERT INTO sample_entity(id, some_value) VALUES (1001, 'recordToUpdate');

INSERT INTO sample_entity(id, some_value) VALUES (1002, 'recordToDelete');