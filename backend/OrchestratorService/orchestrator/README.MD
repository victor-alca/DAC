# Orquestrador Service

Este microsserviço orquestra operações complexas envolvendo múltiplos outros microsserviços. Ele usa o RabbitMQ para se comunicar de forma assíncrona com esses serviços, embora neste exemplo, ele utilize a comunicação síncrona (request/reply) do RabbitMQ.

## Funcionalidades

O orquestrador service atualmente implementa as seguintes operações:

* **Criar Reserva:** Orquestra o processo de criação de uma nova reserva de voo.
* **Cancelar Reserva:** Orquestra o cancelamento de uma reserva existente.
* **Cancelar Voo:** Orquestra o cancelamento de um voo, o que pode envolver o cancelamento ou alteração de reservas associadas.
* **Realizar Voo:** Orquestra a marcação de um voo como realizado, atualizando o status do voo e das reservas associadas.

## Arquitetura

O serviço é construído usando Spring Boot e se comunica com outros serviços através do RabbitMQ.

### Componentes

* **`OrquestradorApplication.java`**: Ponto de entrada da aplicação Spring Boot.
* **`config/RabbitMQConfig.java`**: Configura a conexão com o RabbitMQ, declara as filas e o `RabbitTemplate`.
* **`controller/OrquestradorController.java`**: Expõe os endpoints REST e orquestra as chamadas aos outros serviços.
* **`service/OrquestradorService.java`**: Contém a lógica de negócios para manipular as respostas dos outros serviços e determinar a resposta apropriada para o cliente.
* **`util/MessageUtils.java`**: Fornece métodos utilitários para criar mensagens no formato JSON esperado pelos outros serviços.

### Fluxo de Comunicação

1.  O API Gateway faz uma requisição HTTP para o `OrquestradorController`.
2.  O `OrquestradorController` cria uma mensagem formatada (JSON) usando `MessageUtils`.
3.  O `OrquestradorController` envia a mensagem para a fila do serviço apropriado usando `RabbitTemplate.convertSendAndReceive()`. Este método aguarda uma resposta síncrona.
4.  O serviço de destino consome a mensagem, realiza a operação e envia uma mensagem de resposta para a fila de resposta do `RabbitTemplate`.
5.  O `OrquestradorController` recebe a resposta.
6.  O `OrquestradorController` chama o `OrquestradorService` para processar a resposta.
7.  O `OrquestradorService` determina o status HTTP e o corpo da resposta a serem enviados de volta ao cliente.
8.  O `OrquestradorController` retorna a resposta HTTP.
9.  O API Gateway recebe a resposta.

### Exemplo de Fluxo: Criar Reserva

1.  API Gateway -> `POST /reservas` -> `OrquestradorController.criarReserva()`
2.  `OrquestradorController.criarReserva()` -> `MessageUtils.empacotaMensagem("CriarReserva", jsonReserva)`
3.  `OrquestradorController.criarReserva()` -> `rabbitTemplate.convertSendAndReceive("reserva.v1.reserva", message)`
4.  Serviço de Reserva (consome da fila `reserva.v1.reserva`) -> Processa a reserva -> Envia resposta para fila de resposta do `RabbitTemplate`
5.  `OrquestradorController.criarReserva()` recebe a resposta
6.  `OrquestradorController.criarReserva()` -> `OrquestradorService.handleReservaResponse(response)`
7.  `OrquestradorService.handleReservaResponse()` -> `ResponseEntity<String>`
8.  `OrquestradorController.criarReserva()` -> API Gateway






* **Comunicação Síncrona vs. Assíncrona:**
    *     Este exemplo usa `rabbitTemplate.convertSendAndReceive()`, que é síncrono. Isso simplifica a implementação, mas pode levar a problemas de performance e escalabilidade sob alta carga, pois as threads ficam bloqueadas aguardando respostas.

Este README fornece uma visão geral do microsserviço orquestrador, sua arquitetura e considerações importantes para produção.