-- Insert large dataset for pagination tests
INSERT INTO todos (title, description, completed, user_id, created_at, updated_at)
SELECT
    'Todo ' || generate_series,
    'Description for todo ' || generate_series,
    (generate_series % 2 = 0),
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM generate_series(1, 25);
