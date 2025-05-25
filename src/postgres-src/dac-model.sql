--Voo --------------------------------

CREATE SCHEMA "flight";

CREATE TABLE "flight"."flight" (
  "code" varchar PRIMARY KEY,
  "date" time,
  "origin_airport" varchar NOT NULL,
  "destination_airport" varchar NOT NULL,
  "total_seats" integer,
  "occupated_seats" integer,
  "status" integer NOT NULL
);

CREATE TABLE "flight"."airport" (
  "code" varchar(3) PRIMARY KEY,
  "name" varchar,
  "city" varchar,
  "federative_unit" varchar
);

CREATE TABLE "flight"."flight_status" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "description" varchar
);

ALTER TABLE "flight"."flight" ADD FOREIGN KEY ("origin_airport") REFERENCES "flight"."airport" ("code");

ALTER TABLE "flight"."flight" ADD FOREIGN KEY ("destination_airport") REFERENCES "flight"."airport" ("code");

ALTER TABLE "flight"."flight" ADD FOREIGN KEY ("status") REFERENCES "flight"."flight_status" ("id");

-- Cliente -------------------------------------------

CREATE SCHEMA "client";

CREATE TABLE "client"."client" (
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

CREATE TABLE "client"."miles_record" (
  "client_cpf" varchar not NULL,
  "transaction_date" timestamp not NULL,
  "value" integer,
  "amount" integer,
  "type" varchar, -- "ENTRADA OU SAÍDA"
  "description" varchar,
  "booking_code" varchar,
  PRIMARY KEY ("client_cpf", "transaction_date")
);

ALTER TABLE "client"."miles_record" ADD FOREIGN KEY ("client_cpf") REFERENCES "client"."client" ("cpf");

-- Funcionario ---------------------------------------------------------

CREATE SCHEMA "employee";

CREATE TABLE "employee"."employee" (
  "cpf" varchar PRIMARY KEY,
  "name" varchar,
  "email" varchar,
  "phone" varchar
);

-- Reserva CUD -------------------------------------------------------------------

CREATE SCHEMA "booking_command";

CREATE TABLE "booking_command"."booking" (
  "code" varchar PRIMARY KEY,
  "flight_code" varchar NOT NULL,
  "date" date,
  "status" integer NOT NULL,
  "money_spent" integer,
  "miles_spent" integer
);

CREATE TABLE "booking_command"."booking_status" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "description" varchar
);

CREATE TABLE "booking_command"."booking_status_alteration_record" (
  "booking_code" varchar,
  "alteration_date" timestamp,
  "previous_status" integer NOT NULL,
  "post_status" integer NOT NULL,
  PRIMARY KEY ("booking_code", "alteration_date")
);

ALTER TABLE "booking_command"."booking" ADD FOREIGN KEY ("status") REFERENCES "booking_command"."booking_status" ("id");

ALTER TABLE "booking_command"."booking_status_alteration_record" ADD FOREIGN KEY ("previous_status") REFERENCES "booking_command"."booking_status" ("id");

ALTER TABLE "booking_command"."booking_status_alteration_record" ADD FOREIGN KEY ("post_status") REFERENCES "booking_command"."booking_status" ("id");

ALTER TABLE "booking_command"."booking" ADD FOREIGN KEY ("flight_code") REFERENCES "flight"."flight" ("code");

-- RESERVA READ --------------------------------------------

CREATE SCHEMA "booking_query"

CREATE TABLE "booking_query"."booking" (
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

