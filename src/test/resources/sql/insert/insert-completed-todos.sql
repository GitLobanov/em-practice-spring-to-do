-- Insert completed todos for specific tests
INSERT INTO todos (id, title, description, completed, user_id, created_at, updated_at) VALUES
(1, 'Completed Todo 1', 'First completed todo', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Completed Todo 2', 'Second completed todo', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Completed Todo 3', 'Third completed todo', true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER SEQUENCE todos_id_seq RESTART WITH 4;
