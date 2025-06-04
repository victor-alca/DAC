### 🧩 Exemplo de como funciona a SAGA com RabbitMQ

#### 🧾 Criação de cliente

1. O **Gateway** envia uma requisição `POST` para o Orquestrador:

```
POST /saga/usuarios
```

2. O **Orquestrador** inicia a SAGA e envia uma mensagem para a fila:

```
Routing Key: cliente.cadastro.iniciado
Exchange: saga.exchange
```

```
Orquestrador-MS ->> RabbitMQ: Publish cliente.cadastro.iniciado
```

---

#### 🗂️ Filas envolvidas

O RabbitMQ deve ter duas **queues** com `routing_key = cliente.cadastro.iniciado`, configuradas via `bindings` no arquivo `rabbitmq-definitions.json`.

As filas são:

* `cliente.cadastro.iniciado.cliente`
* `cliente.cadastro.iniciado.auth`

Com essa configuração, **quando uma mensagem é publicada na `cliente.cadastro.iniciado`**, ela é **replicada automaticamente** para as duas filas acima, permitindo que múltiplos microsserviços atuem de forma independente.

---

#### 🤖 Consumidores

Cada microsserviço terá uma função escutando sua respectiva fila:

* **MS Cliente** → escuta `cliente.cadastro.iniciado.cliente`
* **MS Auth** → escuta `cliente.cadastro.iniciado.auth`

Cada um faz o seu processo de cadastro do usuário com base no payload recebido.

---

#### ✅ Resultado do processamento

Após processarem a mensagem:

* Se o cadastro for **bem-sucedido**, publicam em:

```
cliente.cadastro.sucesso
```

* Se ocorrer **falha**, publicam em:

```
cliente.cadastro.falhou
```

Essas filas são consumidas **exclusivamente pelo Orquestrador**, que:

* Atualiza o estado da SAGA
* Verifica quais serviços concluíram ou falharam

Para rastrear corretamente cada fluxo, é utilizado um **`correlationId`**, que **deve ser incluído em todas as mensagens** da saga.

---

#### ❌ Compensação

Se algum dos serviços falhar, a SAGA deve ser **compensada**.

A compensação consiste em **enviar mensagens para os serviços que tiveram sucesso**, para que revertam a operação (por exemplo, **deletem os dados salvos**).

Essas mensagens de compensação são publicadas em novas filas específicas, como:

* `cliente.client.compensar`
* `cliente.auth.compensar`

Cada microsserviço escuta sua fila de compensação e executa o rollback.

---

### 🧠 Como o Orquestrador gerencia o estado da SAGA

Para que o Orquestrador saiba **quais serviços responderam** e **quando a saga foi concluída com sucesso ou precisa ser compensada**, ele utiliza duas classes:

---

### 🧩 `SagaStateManager`

```java
@Component
public class SagaStateManager {
    private final Map<String, SagaStatus> sagaMap = new ConcurrentHashMap<>();
    
    // Cria uma nova saga com os serviços esperados
    public void createSaga(String correlationId, Set<String> expectedServices)

    // Retorna o status atual da saga
    public SagaStatus get(String correlationId)

    // Marca que um serviço respondeu com sucesso
    public void markSuccess(String correlationId, String service)

    // Verifica se todos os serviços responderam com sucesso
    public boolean isComplete(String correlationId)

    // Remove a saga da memória
    public void clear(String correlationId)
}
```

#### ✅ O que ela faz:

* Gerencia um **mapa de Sagas**, onde a **chave é o `correlationId`**.
* Cada entrada contém um objeto `SagaStatus` que controla o progresso da saga.
* Pode adicionar uma nova saga, verificar status, marcar resposta de serviços, e limpar dados quando terminar.

---

### 🗂️ `SagaStatus`

```java
public class SagaStatus {
    private final Set<String> expectedServices;
    private final Set<String> respondedServices = new HashSet<>();
    
    // Marca que um serviço respondeu com sucesso
    public void markSuccess(String serviceName)

    // Verifica se um serviço específico já respondeu
    public boolean hasServiceResponded(String serviceName)

    // Verifica se todos os serviços esperados responderam
    public boolean isComplete()

    // Retorna os serviços que ainda não responderam
    public Set<String> getPendingServices()

    // Retorna os serviços que já responderam
    public Set<String> getRespondedServices()

    // Retorna os serviços esperados
    public Set<String> getExpectedServices()
}
```

#### 📌 Exemplo de uso na prática:

```java
// Ao iniciar a saga
sagaStateManager.createSaga("abc-123", Set.of("CLIENTE", "AUTH"));

// Quando o serviço de cliente responder com sucesso
sagaStateManager.markSuccess("abc-123", "CLIENTE");

// Verificar se a saga está concluída
if (sagaStateManager.isComplete("abc-123")) {
    // prosseguir com a próxima etapa ou encerrar a saga
}
```

---

### 🎯 Objetivo

Esse mecanismo permite que o Orquestrador:

* **Rastreie o progresso da saga em memória**
* **Aja apenas quando todos os serviços envolvidos tiverem respondido**
* **Evite duplicidade** de respostas e problemas de ordem
* **Implemente compensações** caso alguma resposta falhe

---

### 📌 Como o Gateway vai usar o Orquestrador?

1. **Chamar o Orquestrador** para iniciar a SAGA com os dados do novo usuário:

   ```
   POST /saga/usuarios
   ```

   * O Orquestrador irá retornar um `correlationId`, que identifica unicamente essa instância da saga.

2. **Guardar o `correlationId`** retornado para acompanhamento posterior.

3. Iniciar um **polling** (consulta periódica).

4. **Consultar o status da saga**:

   ```
   GET /saga/{correlationId}
   ```

5. Avaliar o campo `status` da resposta:

   * `IN_PROGRESS` → a saga ainda está em execução
   * `COMPLETED_SUCCESS` → todos os serviços responderam com sucesso
   * `COMPLETED_ERROR` → houve falha em pelo menos um dos serviços, possivelmente já compensado

---

diagrama para entender o fluxo (utilizar o https://mermaid.live/edit)

```
sequenceDiagram
    participant Cliente
    participant Gateway
    participant Orquestrador
    participant RabbitMQ
    participant MS_Cliente
    participant MS_Auth

    Cliente->>Gateway: POST /clientes
    Gateway->>Orquestrador: POST /saga/usuarios
    Orquestrador-->>Gateway: 202 Accepted + correlationId

    Orquestrador->>RabbitMQ: Publish cliente.cadastro.iniciado

    Note over RabbitMQ: Mensagem replicada via binding para os MS

    RabbitMQ->>MS_Cliente: cliente.cadastro.iniciado.cliente
    RabbitMQ->>MS_Auth: cliente.cadastro.iniciado.auth

    MS_Cliente->>Orquestrador: cliente.cadastro.sucesso
    MS_Auth->>Orquestrador: cliente.cadastro.sucesso

    Note over Orquestrador: Saga marcada como COMPLETED_SUCCESS

    loop Verificação
        Gateway->>Orquestrador: GET /saga/{correlationId}
        Orquestrador-->>Gateway: status = COMPLETED_SUCCESS
    end
```
