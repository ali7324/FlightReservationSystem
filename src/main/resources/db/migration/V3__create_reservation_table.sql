CREATE TABLE reservation (
                             id SERIAL PRIMARY KEY,
                             flight_id BIGINT NOT NULL,
                             passenger_id BIGINT NOT NULL,
                             reservation_date TIMESTAMP NOT NULL,
                             CONSTRAINT fk_flight
                                 FOREIGN KEY(flight_id)
                                     REFERENCES flight(id),
                             CONSTRAINT fk_passenger
                                 FOREIGN KEY(passenger_id)
                                     REFERENCES passenger(id)
);