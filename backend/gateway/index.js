// Configuração de variáveis de ambiente
require("dotenv-safe").config();

// Importações de bibliotecas
const RABBITMQ_URL = process.env.RABBITMQ_URL;
const express = require('express');
const http = require('http');
const httpProxy = require('express-http-proxy');
const jwt = require('jsonwebtoken');
const axios = require('axios');
const helmet = require('helmet');
const logger = require('morgan');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const cors = require('cors');
const amqp = require('amqplib');

let channel;

(async () => {
    try {
        const connection = await amqp.connect(RABBITMQ_URL);
        channel = await connection.createChannel();
        console.log('Conectado ao RabbitMQ');
    } catch (error) {
        console.error('Erro ao conectar ao RabbitMQ:', error);
    }
})();

// Inicialização do app Express
const app = express();

// Middlewares globais
app.use(cors()); // Permite requisições de outros domínios
app.use(bodyParser.urlencoded({ extended: false })); // Parse application/x-www-form-urlencoded
app.use(bodyParser.json()); // Parse application/json
app.use(logger('dev')); // Logger de requisições
app.use(helmet()); // Segurança HTTP    
app.use(cookieParser()); // Parse de cookies

// Base URLs para os serviços (atualize os endereços se necessário)
const BASE_URL_AUTH = 'http://auth-api:5000';
const BASE_URL_CLIENTS = 'http://clients:5001';
const BASE_URL_EMPLOYEES = 'http://employee-service:5002';
const BASE_URL_FLIGHTS = 'http://flight-service:5003';
const BASE_URL_RESERVATIONS = 'http://localhost:5004';
const BASE_URL_SAGA_ORCHESTRATOR = 'http://orchestrator:5005';

// Serviços
const authServiceProxy = httpProxy(BASE_URL_AUTH);
const clientsServiceProxy = httpProxy(BASE_URL_CLIENTS);
const employeesServiceProxy = httpProxy(BASE_URL_EMPLOYEES);
const flightsServiceProxy = httpProxy(BASE_URL_FLIGHTS);
const reservationsServiceProxy = httpProxy(BASE_URL_RESERVATIONS);

function verifyJWT(req, res, next) {
    const token = req.headers['x-access-token'] || (req.headers['authorization'] && req.headers['authorization'].split(' ')[1]);
    if (!token)
        return res.status(401).json({ auth: false, message: 'Token não fornecido.' });

    jwt.verify(token, process.env.SECRET, function (err, decoded) {
        if (err)
            return res.status(500).json({ auth: false, message: 'Falha ao autenticar o token.' });
        req.userId = decoded.id;
        next();
    });
}

// Middleware para checar permissão por tipo de usuário
function authorizeRoles(...allowedRoles) {
    return (req, res, next) => {
        const token = req.headers['x-access-token'] || (req.headers['authorization'] && req.headers['authorization'].split(' ')[1]);
        if (!token) return res.status(401).json({ message: 'Token não fornecido.' });

        jwt.verify(token, process.env.SECRET, function (err, decoded) {
            if (err) return res.status(401).json({ message: 'Token inválido.' });
            // O endpoint de login gera o token com { sub: usuarioAuth.email, role: usuarioAuth.tipo || 'CLIENTE' }
            const userRole = decoded.role;
            if (!allowedRoles.includes(userRole)) {
                return res.status(403).json({ message: 'Acesso negado.' });
            }
            req.user = decoded;
            next();
        });
    };
}

app.post('/login', async (req, res) => {
    try {
        // Adapta o corpo para o serviço de autenticação
        const authBody = {
            email: req.body.login,
            password: req.body.senha
        };

        // Chamada para o serviço de autenticação
        const authResponse = await axios.post(`${BASE_URL_AUTH}/login`, authBody);

        const usuarioAuth = authResponse.data;

        // Se não veio id, login inválido
        if (!usuarioAuth.id) {
            return res.status(401).json({ message: 'Login inválido!' });
        }

        // Gera token JWT
        const access_token = jwt.sign({ sub: usuarioAuth.email, role: usuarioAuth.tipo || 'CLIENTE' }, process.env.SECRET, {
            expiresIn: 3600, // 1h
        });

        const token_type = 'bearer';

        // Descobre tipo e busca dados completos
        let usuarioResponse;
        let tipo = usuarioAuth.tipo || 'CLIENTE';
        let codigo = usuarioAuth.codigo || usuarioAuth.id;

        if (tipo === 'CLIENTE') {
            usuarioResponse = await axios.get(`${BASE_URL_CLIENTS}/clientes/${codigo}`);
        } else if (tipo === 'FUNCIONARIO') {
            usuarioResponse = await axios.get(`${BASE_URL_EMPLOYEES}/funcionarios/${codigo}`);
        } else {
            return res.status(500).json({ message: 'Tipo de usuário desconhecido.' });
        }

        // Monta resposta final
        return res.status(200).json({
            access_token,
            token_type,
            tipo,
            usuario: usuarioResponse.data
        });

    } catch (error) {
        if (error.response) {
            return res.status(error.response.status).json(error.response.data);
        }
        return res.status(500).json({ message: 'Erro ao efetuar login.', error: error.message });
    }
});

// Rotas para o serviço de Clientes
app.post('/clientes', async (req, res, next) => {
    try {
        // Envia a requisição para o serviço de clientes
        const response = await axios.post(`${BASE_URL_CLIENTS}/clientes`, req.body);

        // Verifica se o cliente foi criado com sucesso
        if (response.status == 201) {
            // Publica os dados do cliente na fila "create-auth-user"
            if (channel) {
                const userPayload = {
                    id: response.data.id, // Inclui o ID do cliente
                    email: req.body.email,
                    nome: req.body.nome,
                    ...response.data // Inclui outros dados retornados pelo serviço de clientes
                };
                channel.assertQueue('create-auth-user', { durable: true})
                channel.sendToQueue('create-auth-user', Buffer.from(JSON.stringify(userPayload)));
                console.log('Mensagem enviada para a fila create-auth-user:', userPayload);
            } else {
                console.error('Canal RabbitMQ não está configurado.');
                return res.status(500).json({ message: 'Erro interno: Canal RabbitMQ não configurado.' });
            }

            // Retorna a resposta para o cliente
            res.status(response.status).json(response.data);
        } else {
            console.error('Erro ao criar cliente no serviço de clientes:', response.data);
            res.status(400).json({ message: 'Erro ao criar cliente.', details: response.data });
        }
    } catch (error) {
        console.error('Erro ao processar /clientes:', error.message);
        if (!error.response) {
            res.status(500).json({ message: 'Erro interno ao processar a requisição.' });
        }
    }
});

app.get('/clientes/:codigoCliente', verifyJWT, authorizeRoles('CLIENTE'), (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

app.put('/clientes/:codigoCliente/milhas', verifyJWT, authorizeRoles('CLIENTE'), (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

app.get('/clientes/:codigoCliente/milhas', verifyJWT, authorizeRoles('CLIENTE'), (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

// Rotas para o serviço de Reservas

// (via Orquestrador Saga)
app.post('/reservas', verifyJWT, authorizeRoles('CLIENTE'), async (req, res) => {
    try {
        const sagaPayload = req.body;

        console.log('API Gateway: Recebido POST /reservas. Payload:', sagaPayload);

        // Chamada para o Serviço Saga (Spring Boot) - Endpoint de criação de reserva
        const sagaServiceResponse = await axios.post(
            `${BASE_URL_SAGA_ORCHESTRATOR}/api/orchestrator/reservation/start-saga`,
            sagaPayload,
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );

        console.log('API Gateway: Resposta do Serviço Saga (criar reserva):', sagaServiceResponse.data);

        // Retorna a resposta do Serviço Saga para o cliente.
        res.status(sagaServiceResponse.status).json(sagaServiceResponse.data);

    } catch (error) {
        console.error('API Gateway: Erro ao processar POST /reservas via Serviço Saga:', error.message);
        if (error.response) {
            res.status(error.response.status).json(error.response.data);
        } else {
            res.status(500).json({ message: 'Erro interno no API Gateway ao processar a reserva.' });
        }
    }
});

// (via Orquestrador Saga)
app.delete('/reservas/:codigoReserva', verifyJWT, authorizeRoles('CLIENTE'), async (req, res) => {
    try {
        const codigoReserva = req.params.codigoReserva;

        console.log(`API Gateway: Recebido DELETE /reservas/${codigoReserva}`);

        // Chamada para o Serviço Saga (Spring Boot) - Endpoint de cancelamento de reserva
        const sagaServiceResponse = await axios.delete(
            `${BASE_URL_SAGA_ORCHESTRATOR}/reservas/${codigoReserva}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );

        console.log('API Gateway: Resposta do Serviço Saga (cancelar reserva):', sagaServiceResponse.data);

        // Retorna a resposta do Serviço Saga para o cliente.
        res.status(sagaServiceResponse.status).json(sagaServiceResponse.data);

    } catch (error) {
        console.error(`API Gateway: Erro ao processar DELETE /reservas/${req.params.codigoReserva} via Serviço Saga:`, error.message);
        if (error.response) {
            res.status(error.response.status).json(error.response.data);
        } else {
            res.status(500).json({ message: 'Erro interno no API Gateway ao processar o cancelamento da reserva.' });
        }
    }
});

// Rota para buscar detalhes de uma reserva específica, incluindo informações do voo
app.get('/reservas/:codigoReserva', verifyJWT, async (req, res) => {
    try {
        // Busca os detalhes da reserva
        const reservaResponse = await axios.get(`${BASE_URL_RESERVATIONS}/reservas/${req.params.codigoReserva}`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const reserva = reservaResponse.data;

        // Busca os detalhes do voo associado à reserva
        const vooResponse = await axios.get(`${BASE_URL_FLIGHTS}/voos/${reserva.codigo_voo}`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const voo = vooResponse.data;

        // Combina os detalhes da reserva com os detalhes do voo
        const reservaComVoo = {
            ...reserva,
            voo
        };

        // Retorna a resposta com os dados combinados
        res.status(200).json(reservaComVoo);
    } catch (error) {
        // Trata erros de resposta da API
        if (error.response) {
            return res.status(error.response.status).json(error.response.data);
        }
        // Trata erros genéricos
        res.status(500).json({ message: 'Erro ao buscar reserva com detalhes do voo.', error: error.message });
    }
});

app.get('/clientes/:codigoCliente/reservas', verifyJWT, authorizeRoles('CLIENTE'), async (req, res) => {
    try {
        // Busca todas as reservas do cliente
        const reservasResponse = await axios.get(`${BASE_URL_RESERVATIONS}/clientes/${req.params.codigoCliente}/reservas`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const reservas = reservasResponse.data;

        // Para cada reserva, busca os detalhes do voo associado
        const reservasComVoos = await Promise.all(reservas.map(async (reserva) => {
            try {
                const vooResponse = await axios.get(`${BASE_URL_FLIGHTS}/voos/${reserva.codigo_voo}`, {
                    headers: { Authorization: req.headers['authorization'] }
                });
                const voo = vooResponse.data;

                // Combina os dados da reserva com os dados do voo
                return { ...reserva, voo };
            } catch (vooError) {
                console.error(`Erro ao buscar voo para a reserva ${reserva.codigo}:`, vooError.message);
                return { ...reserva, voo: null }; // Retorna a reserva mesmo se o voo não for encontrado
            }
        }));

        // Retorna a resposta consolidada
        res.status(200).json(reservasComVoos);
    } catch (error) {
        // Trata erros de resposta da API
        if (error.response) {
            return res.status(error.response.status).json(error.response.data);
        }
        // Trata erros genéricos
        res.status(500).json({ message: 'Erro ao buscar reservas com detalhes dos voos.', error: error.message });
    }
});

app.get('/reservas/:codigoReserva', verifyJWT, async (req, res) => {
    try {
        // Busca os detalhes da reserva no serviço de Reservas
        const reservaResponse = await axios.get(`${BASE_URL_RESERVATIONS}/reservas/${req.params.codigoReserva}`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const reserva = reservaResponse.data;

        // Busca os detalhes do voo associado à reserva no serviço de Voos
        const vooResponse = await axios.get(`${BASE_URL_FLIGHTS}/voos/${reserva.codigo_voo}`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const voo = vooResponse.data;

        // Combina os dados da reserva com os dados do voo
        const reservaComVoo = {
            ...reserva,
            voo
        };

        // Retorna a resposta consolidada
        res.status(200).json(reservaComVoo);
    } catch (error) {
        // Trata erros de resposta da API
        if (error.response) {
            if (error.response.status === 404) {
                return res.status(404).json({ message: 'Reserva ou voo não encontrado.' });
            }
            return res.status(error.response.status).json(error.response.data);
        }
        // Trata erros genéricos
        res.status(500).json({ message: 'Erro ao buscar reserva com detalhes do voo.', error: error.message });
    }
});

app.patch('/reservas/:codigoReserva/estado', verifyJWT, authorizeRoles('CLIENTE', 'FUNCIONARIO'), (req, res, next) => {
    reservationsServiceProxy(req, res, next);
});

// Rotas para o serviço de Voos
app.get('/voos', (req, res, next) => {
    flightsServiceProxy(req, res, next);
});

app.get('/voos/:codigoVoo', (req, res, next) => {
    flightsServiceProxy(req, res, next);    
});

app.post('/voos', verifyJWT, authorizeRoles('FUNCIONARIO'), (req, res, next) => {
    flightsServiceProxy(req, res, next);
});

// (via Orquestrador Saga)
app.patch('/voos/:codigoVoo/estado', verifyJWT, authorizeRoles('FUNCIONARIO'), async (req, res) => {
    try {
        const codigoVoo = req.params.codigoVoo;
        const { estado } = req.body; 

        console.log(`API Gateway: Recebido PATCH /voos/${codigoVoo}/estado com estado: ${estado}`);

        // Chamada para o Serviço Saga (Spring Boot) - Endpoint de alteração de estado do voo
        const sagaServiceResponse = await axios.patch(
            `${BASE_URL_SAGA_ORCHESTRATOR}/voos/${codigoVoo}/estado`,
            { estado: estado }, // Envia o estado no corpo da requisição
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );

        console.log('API Gateway: Resposta do Serviço Saga (alterar estado do voo):', sagaServiceResponse.data);

        // Retorna a resposta do Serviço Saga para o cliente.
        res.status(sagaServiceResponse.status).json(sagaServiceResponse.data);

    } catch (error) {
        console.error(`API Gateway: Erro ao processar PATCH /voos/${req.params.codigoVoo}/estado via Serviço Saga:`, error.message);
        if (error.response) {
            res.status(error.response.status).json(error.response.data);
        } else {
            res.status(500).json({ message: 'Erro interno no API Gateway ao processar a alteração do estado do voo.' });
        }
    }
});

app.get('/aeroportos', verifyJWT, authorizeRoles('FUNCIONARIO'), (req, res, next) => {
    flightsServiceProxy(req, res, next);
});

// Rotas para o serviço de Funcionários
app.get('/funcionarios', verifyJWT, authorizeRoles('FUNCIONARIO'), (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

app.post('/funcionarios', verifyJWT, authorizeRoles('FUNCIONARIO'), (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

app.put('/funcionarios/:codigoFuncionario', verifyJWT, authorizeRoles('FUNCIONARIO'), (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

app.delete('/funcionarios/:codigoFuncionario', verifyJWT, authorizeRoles('FUNCIONARIO'), (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

// Logout
app.post('/logout', function (req, res) {
    res.json({ auth: false, token: null });
});

// Cria o servidor na porta 3000
var server = http.createServer(app);
server.listen(3000);