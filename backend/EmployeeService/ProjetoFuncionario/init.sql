CREATE SCHEMA IF NOT EXISTS employee;

CREATE TABLE IF NOT EXISTS employee.employee (
    id SERIAL PRIMARY KEY,
    active BOOLEAN NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL
);

INSERT INTO employee.employee (active, cpf, name, email, phone)
VALUES (TRUE, '90769281001', 'Funcionario Pré-cadastrado', 'func_pre@gmail.com', '11999999999')
ON CONFLICT (cpf) DO NOTHING;
