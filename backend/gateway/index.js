// Configuração de variáveis de ambiente
require("dotenv-safe").config();

// Importações de bibliotecas
const express = require('express');
const http = require('http');
const httpProxy = require('express-http-proxy');
const jwt = require('jsonwebtoken');
const axios = require('axios');
const helmet = require('helmet');
const logger = require('morgan');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');

// Inicialização do app Express
const app = express();

// Middlewares globais
app.use(bodyParser.urlencoded({ extended: false })); // Parse application/x-www-form-urlencoded
app.use(bodyParser.json()); // Parse application/json
app.use(logger('dev')); // Logger de requisições
app.use(helmet()); // Segurança HTTP    
app.use(cookieParser()); // Parse de cookies

// Base URLs para os serviços (atualize os endereços se necessário)
const BASE_URL_AUTH = 'http://localhost:5000';
const BASE_URL_CLIENTS = 'http://localhost:5001';
const BASE_URL_EMPLOYEES = 'http://localhost:5002';
const BASE_URL_FLIGHTS = 'http://localhost:5003';
const BASE_URL_RESERVATIONS = 'http://localhost:5004';
const BASE_URL_SAGA_ORCHESTRATOR = 'http://localhost:5005';

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

app.post('/login', async (req, res) => {
    try {
        // Chamada para o serviço de autenticação
        const authResponse = await axios.post(`${BASE_URL_AUTH}/login`, {
            login: req.body.login,
            senha: req.body.senha
        });

        const { access_token, token_type, tipo, codigo } = authResponse.data;

        // Determina o serviço correto com base no tipo
        let usuarioResponse;
        if (tipo === 'CLIENTE') {
            usuarioResponse = await axios.get(`${BASE_URL_CLIENTS}/clientes/${codigo}`, {
                headers: { Authorization: `${token_type} ${access_token}` }
            });
        } else if (tipo === 'FUNCIONARIO') {
            usuarioResponse = await axios.get(`${BASE_URL_EMPLOYEES}/funcionarios/${codigo}`, {
                headers: { Authorization: `${token_type} ${access_token}` }
            });
        } else {
            return res.status(500).json({ message: 'Tipo de usuário desconhecido.' });
        }

        // Resposta final combinada
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
app.post('/clientes', (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

app.get('/clientes/:codigoCliente', verifyJWT, (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

app.put('/clientes/:codigoCliente/milhas', verifyJWT, (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

app.get('/clientes/:codigoCliente/milhas', verifyJWT, (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

// Rotas para o serviço de Reservas

// (via Orquestrador Saga)
app.post('/reservas', verifyJWT, async (req, res) => {
    try {
        const sagaPayload = req.body;

        console.log('API Gateway: Recebido POST /reservas. Payload:', sagaPayload);

        // Chamada para o Serviço Saga (Spring Boot) - Endpoint de criação de reserva
        const sagaServiceResponse = await axios.post(
            `${BASE_URL_SAGA_ORCHESTRATOR}/reservas`,
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
app.delete('/reservas/:codigoReserva', verifyJWT, async (req, res) => {
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

app.get('/clientes/:codigoCliente/reservas', verifyJWT, async (req, res) => {
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

app.patch('/reservas/:codigoReserva/estado', verifyJWT, (req, res, next) => {
    reservationsServiceProxy(req, res, next);
});

// Rotas para o serviço de Voos
app.get('/voos', (req, res, next) => {
    flightsServiceProxy(req, res, next);
});

app.get('/voos/:codigoVoo', (req, res, next) => {
    flightsServiceProxy(req, res, next);    
});

app.post('/voos', verifyJWT, (req, res, next) => {
    flightsServiceProxy(req, res, next);
});

// (via Orquestrador Saga)
app.patch('/voos/:codigoVoo/estado', verifyJWT, async (req, res) => {
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

app.get('/aeroportos', (req, res, next) => {
    flightsServiceProxy(req, res, next);
});

// Rotas para o serviço de Funcionários
app.get('/funcionarios', verifyJWT, (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

app.post('/funcionarios', verifyJWT, (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

app.put('/funcionarios/:codigoFuncionario', verifyJWT, (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

app.delete('/funcionarios/:codigoFuncionario', verifyJWT, (req, res, next) => {
    employeesServiceProxy(req, res, next);
});

// Logout
app.post('/logout', function (req, res) {
    res.json({ auth: false, token: null });
});

// Cria o servidor na porta 3000
var server = http.createServer(app);
server.listen(3000);