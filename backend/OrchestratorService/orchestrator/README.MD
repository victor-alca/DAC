# Orchestrator Service

Este serviço orquestra o fluxo de criação de reservas utilizando o padrão Saga com RabbitMQ e comunicação assíncrona entre microsserviços.

## Fluxo de uma Saga

1. **Início:** O Orchestrator recebe uma requisição para criar uma reserva.
2. **Reserva:** Envia mensagem para o serviço de reservas criar a reserva.
3. **Voo:** Após confirmação, envia mensagem para o serviço de voos atualizar o voo.
4. **Cliente:** Após confirmação, envia mensagem para o serviço de clientes atualizar o cliente.
5. **Finalização:** Se todas etapas forem bem-sucedidas, a saga é finalizada.
6. **Rollback:** Se qualquer etapa falhar, o Orchestrator inicia o rollback, desfazendo as operações já realizadas.

