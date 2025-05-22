CREATE TABLE passenger (
                           id SERIAL PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           age INT NOT NULL,
                           gender VARCHAR(10) NOT NULL,
                           date_of_birth TIMESTAMP NOT NULL,
                           gmail VARCHAR(255) NOT NULL
);