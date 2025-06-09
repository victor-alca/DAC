create schema if not exists flight;

create table if not exists flight.airport (
  code varchar(3) primary key,
  name varchar,
  city varchar,
  federative_unit varchar
);

create table if not exists flight.flightstatus (
  id integer primary key,
  code varchar,
  description varchar
);

create table if not exists flight.flight (
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

-- Aeroportos
insert into flight.airport (code, name, city, federative_unit) values
('GRU', 'Aeroporto Internacional de São Paulo/Guarulhos', 'Guarulhos', 'SP'),
('GIG', 'Aeroporto Internacional do Rio de Janeiro/Galeão', 'Rio de Janeiro', 'RJ'),
('CWB', 'Aeroporto Internacional de Curitiba', 'Curitiba', 'PR'),
('POA', 'Aeroporto Internacional Salgado Filho', 'Porto Alegre', 'RS');

-- Status de voo (exemplo)
insert into flight.flightstatus (id, code, description) values
(1, 'ATIVO', 'Voo ativo'),
(2, 'CANCELADO', 'Voo cancelado')
on conflict do nothing;

-- Voos
insert into flight.flight (code, date, origin_airport, destination_airport, total_seats, occupated_seats, status, valor_passagem) values
('TADS0002', '2025-08-10T10:30:00-03:00', 'POA', 'CWB', 180, 50, 1, 450.00),
('TADS0003', '2025-09-11T09:30:00-03:00', 'CWB', 'GIG', 180, 60, 1, 500.00),
('TADS0004', '2025-10-12T08:30:00-03:00', 'CWB', 'POA', 180, 70, 1, 420.00);