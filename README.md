# Exemplo de Transactional Outbox Pattern

Este projeto é um exemplo didático de implementação do padrão *Transactional Outbox Pattern* utilizando Spring Boot, Java 21, RabbitMQ e banco de dados H2.

O objetivo do padrão é garantir a consistência entre o estado do banco de dados e o envio de mensagens para um sistema de mensageria. A ideia principal é que uma mensagem só deve ser enviada se a transação no banco de dados for concluída com sucesso.

## Como funciona

1.  Quando uma operação de negócio que precisa notificar outros sistemas é executada (neste exemplo, a atualização de senha de um usuário), a mensagem a ser enviada é salva em uma tabela "outbox" no mesmo banco de dados e dentro da mesma transação.
2.  Se a transação for bem-sucedida, a mensagem estará salva na tabela `outbox_message` com o status `PENDING`.
3.  Um processo em segundo plano (`OutboxMessageProducer`) verifica periodicamente a tabela `outbox_message` em busca de mensagens pendentes.
4.  Ao encontrar uma mensagem pendente, o processo a envia para o RabbitMQ e atualiza o status da mensagem para `SENT`.

Isso garante que, mesmo que o serviço de mensageria esteja indisponível no momento da transação, a mensagem não será perdida e será enviada assim que o serviço se restabelecer.

## Como executar o projeto

### Pré-requisitos

*   Java 21 ou superior
*   Docker

### 1. Iniciar o RabbitMQ

O projeto utiliza RabbitMQ como sistema de mensageria. Para iniciá-lo, execute o seguinte comando na raiz do projeto:

```bash
./scripts/rabbit_start.sh
```

Este comando irá baixar a imagem do RabbitMQ e iniciar um container Docker com as portas necessárias expostas.

### 2. Executar a aplicação

Para executar a aplicação, utilize o Gradle Wrapper:

```bash
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`.

### 3. Testando o fluxo

Para testar o fluxo do *Transactional Outbox Pattern*, você pode utilizar os scripts na pasta `scripts`:

1.  **Crie alguns usuários:**

    ```bash
    ./scripts/create_users.sh
    ```

2.  **Atualize a senha de um usuário:**

    ```bash
    ./scripts/test.sh
    ```

Ao executar o script `test.sh`, a senha do usuário será atualizada e uma mensagem será enviada para a fila do RabbitMQ. Você pode observar os logs da aplicação para ver o processo acontecendo.

### Testes

Para demonstrar o comportamento transacional, fundamental para a correta implementação do _Transactional Outbox Pattern_, foram escritos os testes *UserServiceTest* e *UserApiControllerTest*.

#### UserApiControllerTest:

O teste _shouldCreateOutboxMessageWhenPasswordIsUpdated_ primeiro cria um novo usuário no banco de dados e depois executa uma alteração de senha, que será concluída com sucesso.

Espera-se neste contexto que seja persistido o usuário com a senmha atualizado a tabela _users_ e que seja criada uma nova mensagem na tabela _outbox_messages_.

#### UserServiceTest:

O teste _shouldRollbackUserUpdateIfOutboxMessageRepositoryFails_ se inicia criando um novo usuário na tabela _uses_ porém, após a realização da alteração da senha do usuário, é simulada uma falha ao criar uma mensagem em _outbox_messages_.

O comportamento transacional é demonstrado porque o registro do usuário na tabela _users_ é persistido com a senha original, demonstrando que a transação mal sucedida passar por _rollback_ devido à falha ao persistir a mensagem.
