CREATE SCHEMA IF NOT EXISTS bookingquery;

CREATE TABLE IF NOT EXISTS bookingquery.booking (
  code varchar PRIMARY KEY,
  "date" timestamp,
  origin_airport varchar NOT NULL,
  destination_airport varchar NOT NULL,
  total_seats integer NOT NULL,
  statusbooking varchar NOT NULL,
  money_spent integer,
  miles_spent integer,
  client_id integer,
  flight_code varchar NOT NULL
);