CREATE SCHEMA IF NOT EXISTS bookingcommand;

CREATE TABLE IF NOT EXISTS bookingcommand.booking (
    code varchar PRIMARY KEY,
    flight_code varchar NOT NULL,
    date date,
    status integer NOT NULL,
    money_spent integer,
    miles_spent integer,
    client_id integer,
    total_seats integer,
    origin_airport varchar NOT NULL,
    destination_airport varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS bookingcommand.bookingstatus (
    id integer PRIMARY KEY,
    code varchar,
    description varchar
);

CREATE TABLE IF NOT EXISTS bookingcommand.bookingstatusalterationrecord (
    booking_code varchar,
    alteration_date timestamp,
    previous_status integer NOT NULL,
    post_status integer NOT NULL,
    PRIMARY KEY (booking_code, alteration_date)
);

ALTER TABLE bookingcommand.booking ADD FOREIGN KEY (status) REFERENCES bookingcommand.bookingstatus (id);
ALTER TABLE bookingcommand.bookingstatusalterationrecord ADD FOREIGN KEY (previous_status) REFERENCES bookingcommand.bookingstatus (id);
ALTER TABLE bookingcommand.bookingstatusalterationrecord ADD FOREIGN KEY (post_status) REFERENCES bookingcommand.bookingstatus (id);

INSERT INTO bookingcommand.bookingstatus (id, code, description) VALUES
    (1, 'CRIADA', 'CRIADA'),
    (2, 'NAO_REALIZADA', 'NÃO REALIZADA'),
    (3, 'CANCELADA', 'CANCELADA'),
    (4, 'CHECKIN', 'CHECK-IN'),
    (5, 'CANCELADA_VOO', 'CANCELADA VOO'),
    (6, 'EMBARCADA', 'EMBARCADA'),
    (7, 'REALIZADA', 'REALIZADA');
