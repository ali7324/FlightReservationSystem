CREATE TABLE flights (
                         id SERIAL PRIMARY KEY,
                         flight_number VARCHAR(100) NOT NULL,
                         departure_city VARCHAR(100) NOT NULL,
                         arrival_city VARCHAR(100) NOT NULL,
                         departure_time TIMESTAMP NOT NULL,
                         arrival_time TIMESTAMP NOT NULL,
                         price DECIMAL(10, 2) NOT NULL,
                         seats_available INT NOT NULL
);
