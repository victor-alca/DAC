## Mock Services

Instale o json-server:

```bash
npm install -g json-server@0.17.4
```

Inicie o servidor mock:

```bash
json-server --watch db.json --routes routes.json --id codigo --port 5000
```

Inicie o gateway:

```bash
node index.js
```

> Todos os mocks estão rodando na porta 5000 para facilitar, sem precisar de vários servidores separados.
