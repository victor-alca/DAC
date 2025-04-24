CREATE SCHEMA "Flight";

CREATE SCHEMA "Client";

CREATE SCHEMA "Employee";

CREATE SCHEMA "Booking";

CREATE TABLE "Flight"."Flight" (
  "code" varchar PRIMARY KEY,
  "date" datetime,
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
  "client_cpf" varchar,
  "transaction_date" timestamp,
  "value" integer,
  "in_out" boolean,
  "description" varchar,
  PRIMARY KEY ("client_cpf", "transaction_date")
);

CREATE TABLE "Employee"."Employee" (
  "cpf" varchar PRIMARY KEY,
  "name" varchar,
  "email" varchar,
  "phone" varchar
);

CREATE TABLE "Booking"."Booking" (
  "code" varchar PRIMARY KEY,
  "flight_code" varchar NOT NULL,
  "date" datetime,
  "status" integer NOT NULL,
  "money_spent" integer,
  "miles_spent" integer
);

CREATE TABLE "Booking"."BookingStatus" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "description" varchar
);

CREATE TABLE "Booking"."BookingStatusAlterationRecord" (
  "booking_code" varchar,
  "alteration_date" timestamp,
  "previous_status" integer NOT NULL,
  "post_status" integer NOT NULL,
  PRIMARY KEY ("booking_code", "alteration_date")
);

ALTER TABLE "Flight"."Flight" ADD FOREIGN KEY ("origin_airport") REFERENCES "Flight"."Airport" ("code");

ALTER TABLE "Flight"."Flight" ADD FOREIGN KEY ("destination_airport") REFERENCES "Flight"."Airport" ("code");

ALTER TABLE "Flight"."Flight" ADD FOREIGN KEY ("status") REFERENCES "Flight"."FlightStatus" ("id");

ALTER TABLE "Client"."MilesRecord" ADD FOREIGN KEY ("client_cpf") REFERENCES "Client"."Client" ("cpf");

ALTER TABLE "Booking"."Booking" ADD FOREIGN KEY ("status") REFERENCES "Booking"."BookingStatus" ("id");

ALTER TABLE "Booking"."BookingStatusAlterationRecord" ADD FOREIGN KEY ("previous_status") REFERENCES "Booking"."BookingStatus" ("id");

ALTER TABLE "Booking"."BookingStatusAlterationRecord" ADD FOREIGN KEY ("post_status") REFERENCES "Booking"."BookingStatus" ("id");

ALTER TABLE "Booking"."Booking" ADD FOREIGN KEY ("flight_code") REFERENCES "Flight"."Flight" ("code");
