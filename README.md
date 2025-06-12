# Trabalho DAC - Arquitetura de Microsserviços

Este projeto foi construído utilizando uma arquitetura de microsserviços distribuídos, todos gerenciados por contêineres com Docker Compose. O sistema implementa o padrão SAGA para coordenação de transações distribuídas e utiliza CQRS (Command Query Responsibility Segregation) para separação de responsabilidades de leitura e escrita.

## Como executar o projeto

Para executar o projeto, certifique-se de que o Docker e o Docker Compose estejam instalados e devidamente configurados. Em seguida, abra um terminal na raiz do repositório e execute o comando abaixo:

```bash
docker-compose up --build
```

**Nota:** O comando `--build` garante que as imagens sejam reconstruídas com as últimas alterações do código.

## Microsserviços

A seguir, a lista dos microsserviços gerenciados e suas respectivas portas:

- **API Gateway**: exposto na porta **3000**; é o serviço principal para interface com o frontend e orquestra as chamadas entre os demais serviços.
- **Auth Service**: exposto na porta **5000**; responsável pela autenticação e autorização de usuários.
- **Client Service**: exposto na porta **5001**; gerencia dados de clientes e saldo de milhas.
- **Employee Service**: exposto na porta **5002**; gerencia dados de funcionários.
- **Flight Service**: exposto na porta **5003**; gerencia informações de voos e aeroportos.
- **Booking Command Service**: exposto na porta **5004**; responsável pelos comandos de reserva (CQRS - Write).
- **Orchestrator Service (SAGA)**: exposto na porta **5005**; orquestra transações distribuídas utilizando o padrão SAGA.
- **Booking Query Service**: exposto na porta **5006**; responsável pelas consultas de reserva (CQRS - Read).

## Bancos de Dados

O sistema utiliza múltiplos bancos de dados especializados:

### PostgreSQL
- **clients-db**: Porta **5438** - Banco de dados do serviço de clientes
- **booking-command-db**: Porta **5436** - Banco de dados para comandos de reserva
- **booking-query-db**: Porta **5437** - Banco de dados para consultas de reserva
- **flight-db**: Porta **5434** - Banco de dados do serviço de voos
- **employee-db**: Porta **5435** - Banco de dados do serviço de funcionários

### MongoDB
- **auth-db**: Porta **27017** - Banco de dados do serviço de autenticação

## Message Broker

- **RabbitMQ**: Porta **5672** (AMQP) e **15672** (Management UI)
  - Interface de gerenciamento disponível em: `http://localhost:15672`
  - Credenciais padrão: usuário `guest`, senha `guest`

## Arquitetura

### Padrões Implementados

1. **API Gateway Pattern**: Centraliza o acesso aos microsserviços
2. **SAGA Pattern**: Coordena transações distribuídas
3. **CQRS**: Separa operações de leitura e escrita para reservas
4. **Event-Driven Architecture**: Comunicação assíncrona via RabbitMQ

### Autenticação e Autorização

O sistema utiliza JWT (JSON Web Tokens) para autenticação. Os tokens são gerados pelo Auth Service e validados pelo API Gateway, que também gerencia autorização baseada em roles (CLIENTE/FUNCIONARIO).

### Principais Endpoints

Todos os endpoints devem ser acessados através do API Gateway na porta **3000**:

- `POST /login` - Autenticação de usuários
- `POST /clientes` - Cadastro de clientes (via SAGA)
- `POST /reservas` - Criação de reservas (via SAGA)
- `GET /voos` - Listagem de voos
- `GET /reservas/{codigo}` - Detalhes de uma reserva
- `POST /logout` - Logout de usuários

### Observações Importantes

- O API Gateway implementa blacklist de tokens para logout seguro
- As transações distribuídas são coordenadas pelo Orchestrator Service usando RabbitMQ
- O sistema suporta rollback automático em caso de falhas nas transações SAGA
