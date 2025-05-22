--Voo --------------------------------

CREATE SCHEMA "Flight";

CREATE TABLE "Flight"."Flight" (
  "code" varchar PRIMARY KEY,
  "date" time,
  "origin_airport" varchar NOT NULL,
  "destination_airport" varchar NOT NULL,
  "total_seats" integer,
  "occupated_seats" integer,
  "status" integer NOT NULL
);

CREATE TABLE "Flight"."Airport" (
  "code" varchar(3) PRIMARY KEY,
  "name" varchar,
  "city" varchar,
  "federative_unit" varchar
);

CREATE TABLE "Flight"."FlightStatus" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "description" varchar
);

ALTER TABLE "Flight"."Flight" ADD FOREIGN KEY ("origin_airport") REFERENCES "Flight"."Airport" ("code");

ALTER TABLE "Flight"."Flight" ADD FOREIGN KEY ("destination_airport") REFERENCES "Flight"."Airport" ("code");

ALTER TABLE "Flight"."Flight" ADD FOREIGN KEY ("status") REFERENCES "Flight"."FlightStatus" ("id");

-- Cliente -------------------------------------------

CREATE SCHEMA "Client";

CREATE TABLE "Client"."Client" (
  "cpf" varchar PRIMARY KEY,
  "name" varchar,
  "miles" integer,
  "email" varchar,
  "phone" varchar,
  "active" boolean,
  "cep" varchar,
  "street" varchar,
  "neighborhood" varchar,
  "city" varchar,
  "number" varchar,
  "complement" varchar,
  "federative_unit" varchar
);

CREATE TABLE "Client"."MilesRecord" (
  "client_cpf" varchar not NULL,
  "transaction_date" timestamp not NULL,
  "value" integer,
  "amount" integer,
  "type" varchar, -- "ENTRADA OU SAÍDA"
  "description" varchar,
  "booking_code" varchar,
  PRIMARY KEY ("client_cpf", "transaction_date")
);

ALTER TABLE "Client"."MilesRecord" ADD FOREIGN KEY ("client_cpf") REFERENCES "Client"."Client" ("cpf");

-- Funcionario ---------------------------------------------------------

CREATE SCHEMA "Employee";

CREATE TABLE "Employee"."Employee" (
  "cpf" varchar PRIMARY KEY,
  "name" varchar,
  "email" varchar,
  "phone" varchar
);

-- Reserva CUD -------------------------------------------------------------------

CREATE SCHEMA "BookingCommand";

CREATE TABLE "BookingCommand"."Booking" (
  "code" varchar PRIMARY KEY,
  "flight_code" varchar NOT NULL,
  "date" date,
  "status" integer NOT NULL,
  "money_spent" integer,
  "miles_spent" integer
);

CREATE TABLE "BookingCommand"."BookingStatus" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "description" varchar
);

CREATE TABLE "BookingCommand"."BookingStatusAlterationRecord" (
  "booking_code" varchar,
  "alteration_date" timestamp,
  "previous_status" integer NOT NULL,
  "post_status" integer NOT NULL,
  PRIMARY KEY ("booking_code", "alteration_date")
);

ALTER TABLE "BookingCommand"."Booking" ADD FOREIGN KEY ("status") REFERENCES "BookingCommand"."BookingStatus" ("id");

ALTER TABLE "BookingCommand"."BookingStatusAlterationRecord" ADD FOREIGN KEY ("previous_status") REFERENCES "BookingCommand"."BookingStatus" ("id");

ALTER TABLE "BookingCommand"."BookingStatusAlterationRecord" ADD FOREIGN KEY ("post_status") REFERENCES "BookingCommand"."BookingStatus" ("id");

ALTER TABLE "BookingCommand"."Booking" ADD FOREIGN KEY ("flight_code") REFERENCES "Flight"."Flight" ("code");

-- RESERVA READ --------------------------------------------

CREATE SCHEMA "BookingQuery"

CREATE TABLE "BookingQuery"."Booking" (
  "code" varchar PRIMARY KEY,
  "date" date,
  "origin_airport" varchar NOT NULL,
  "destination_airport" varchar NOT NULL,
  "total_seats" integer NOT NULL,
  "occupated_seats" integer NOT NULL,
  "statusFlight" varchar NOT NULL,
  "statusBooking" varchar NOT NULL,
  "money_spent" integer,
  "miles_spent" integer
);

