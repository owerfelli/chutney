DROP TABLE users;
CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY,
  name VARCHAR(30),
  email  VARCHAR(50)
);
