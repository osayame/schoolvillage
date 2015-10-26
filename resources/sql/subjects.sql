INSERT INTO subjects(id, path, name)
VALUES (nextval('subjects_serial'), ARRAY[currval('subjects_serial')]::integer[], 'Mathematics');

INSERT INTO subjects(id, path, name)
VALUES (nextval('subjects_serial'), ARRAY[currval('subjects_serial')]::integer[], 'Science');

INSERT INTO subjects(id, path, name)
VALUES (nextval('subjects_serial'), ARRAY[currval('subjects_serial')]::integer[], 'Finance');

INSERT INTO subjects(id, path, name)
VALUES (nextval('subjects_serial'), ARRAY[currval('subjects_serial')]::integer[], 'Economics');

INSERT INTO subjects(id, path, name)
VALUES (nextval('subjects_serial'), ARRAY[currval('subjects_serial')]::integer[], 'English');

INSERT INTO subjects(id, path, name)
VALUES (nextval('subjects_serial'), ARRAY[currval('subjects_serial')]::integer[], 'Test Prep');

INSERT INTO subjects(id, path, name)
VALUES (
nextval('subjects_serial'),
(select path from subjects where name='Science') || ARRAY[currval('subjects_serial')]::integer[],
'Biology');


