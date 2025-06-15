-- ===== SCHEMA PER SERVICE - Um banco, vários schemas =====

-- Criar schemas para cada serviço
CREATE SCHEMA IF NOT EXISTS client;
CREATE SCHEMA IF NOT EXISTS bookingcommand;
CREATE SCHEMA IF NOT EXISTS bookingquery;
CREATE SCHEMA IF NOT EXISTS flight;
CREATE SCHEMA IF NOT EXISTS employee;

-- ===== SCHEMA: clients =====

-- Tabelas do serviço de clientes
CREATE TABLE IF NOT EXISTS client.clients (
    code SERIAL PRIMARY KEY,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    miles DOUBLE PRECISION DEFAULT 0,
    cep VARCHAR(8),
    federative_unit VARCHAR(2),
    city VARCHAR(255),
    neighborhood VARCHAR(255),
    street VARCHAR(255),
    number VARCHAR(10),
    complement VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS client.miles_records (
    id SERIAL PRIMARY KEY,
    client_code INTEGER NOT NULL REFERENCES client.clients(code),
    transaction_date TIMESTAMP NOT NULL,
    value INTEGER NOT NULL,
    amount INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    booking_code VARCHAR(255)
);

-- ===== SCHEMA: bookingcommand =====

CREATE TABLE IF NOT EXISTS bookingcommand.booking (
    code varchar PRIMARY KEY,
    flight_code varchar NOT NULL,
    date timestamp,
    status integer NOT NULL,
    money_spent integer,
    miles_spent integer,
    client_id integer,
    total_seats integer,
    origin_airport varchar,
    destination_airport varchar
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

-- Foreign keys para bookingcommand
ALTER TABLE bookingcommand.booking ADD FOREIGN KEY (status) REFERENCES bookingcommand.bookingstatus (id);
ALTER TABLE bookingcommand.bookingstatusalterationrecord ADD FOREIGN KEY (previous_status) REFERENCES bookingcommand.bookingstatus (id);
ALTER TABLE bookingcommand.bookingstatusalterationrecord ADD FOREIGN KEY (post_status) REFERENCES bookingcommand.bookingstatus (id);

-- Inserir status para bookingcommand
INSERT INTO bookingcommand.bookingstatus (id, code, description) VALUES
    (1, 'CRIADA', 'CRIADA'),
    (2, 'NÃO REALIZADA', 'NÃO REALIZADA'),
    (3, 'CANCELADA', 'CANCELADA'),
    (4, 'CHECK-IN', 'CHECK-IN'),
    (5, 'CANCELADA VOO', 'CANCELADA VOO'),
    (6, 'EMBARCADA', 'EMBARCADA'),
    (7, 'REALIZADA', 'REALIZADA')
ON CONFLICT (id) DO NOTHING;

-- ===== SCHEMA: bookingquery =====

CREATE TABLE IF NOT EXISTS bookingquery.booking (
  code varchar PRIMARY KEY,
  "date" timestamp,
  origin_airport varchar,
  destination_airport varchar,
  total_seats integer NOT NULL,
  statusbooking varchar NOT NULL,
  money_spent integer,
  miles_spent integer,
  client_id integer,
  flight_code varchar NOT NULL
);

-- ===== SCHEMA: flight =====

CREATE TABLE IF NOT EXISTS flight.airport (
  code varchar(3) primary key,
  name varchar,
  city varchar,
  federative_unit varchar
);

CREATE TABLE IF NOT EXISTS flight.flightstatus (
  id integer primary key,
  code varchar,
  description varchar
);

CREATE TABLE IF NOT EXISTS flight.flight (
  code varchar primary key,
  date timestamp,
  origin_airport varchar not null,
  destination_airport varchar not null,
  total_seats integer,
  occupated_seats integer,
  status integer not null,
  valor_passagem double precision,
  foreign key (origin_airport) references flight.airport (code),
  foreign key (destination_airport) references flight.airport (code),
  foreign key (status) references flight.flightstatus (id)
);

-- Inserir dados básicos no schema flight
INSERT INTO flight.airport (code, name, city, federative_unit) VALUES
('GRU', 'Aeroporto Internacional de São Paulo/Guarulhos', 'Guarulhos', 'SP'),
('GIG', 'Aeroporto Internacional do Rio de Janeiro/Galeão', 'Rio de Janeiro', 'RJ'),
('CWB', 'Aeroporto Internacional de Curitiba', 'Curitiba', 'PR'),
('POA', 'Aeroporto Internacional Salgado Filho', 'Porto Alegre', 'RS')
ON CONFLICT (code) DO NOTHING;

INSERT INTO flight.flightstatus (id, code, description) VALUES
(1, 'ATIVO', 'Voo ativo'),
(2, 'CANCELADO', 'Voo cancelado')
ON CONFLICT (id) DO NOTHING;

INSERT INTO flight.flight (code, date, origin_airport, destination_airport, total_seats, occupated_seats, status, valor_passagem) VALUES
('TADS0002', '2025-08-10T10:30:00-03:00', 'POA', 'CWB', 180, 50, 1, 450.00),
('TADS0003', '2025-09-11T09:30:00-03:00', 'CWB', 'GIG', 180, 60, 1, 500.00),
('TADS0004', '2025-10-12T08:30:00-03:00', 'CWB', 'POA', 180, 70, 1, 420.00)
ON CONFLICT (code) DO NOTHING;

-- ===== SCHEMA: employee =====

CREATE TABLE IF NOT EXISTS employee.employee (
    id SERIAL PRIMARY KEY,
    active BOOLEAN NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL
);

-- Inserir funcionário pré-cadastrado
INSERT INTO employee.employee (active, cpf, name, email, phone)
VALUES (TRUE, '90769281001', 'Funcionario Pré-cadastrado', 'func_pre@gmail.com', '11999999999')
ON CONFLICT (cpf) DO NOTHING;