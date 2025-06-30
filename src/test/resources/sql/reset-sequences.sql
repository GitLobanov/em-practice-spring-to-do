-- Сбрасываем счетчики, чтобы избежать конфликтов PK
ALTER SEQUENCE todos_id_seq RESTART WITH 10;
ALTER SEQUENCE users_id_seq RESTART WITH 10;