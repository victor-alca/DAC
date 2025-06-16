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
    for(let i = 0; i < 5; i++){
        try {
        const connection = await amqp.connect(RABBITMQ_URL);
        channel = await connection.createChannel();
        console.log('Conectado ao RabbitMQ');
    } catch (error) {
        console.error('Erro ao conectar ao RabbitMQ:', error);
        await new Promise(res => setTimeout(res, 3000));
    }
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

const BASE_URL_AUTH = 'http://auth-api:5000';
const BASE_URL_CLIENTS = 'http://clients:5001';
const BASE_URL_EMPLOYEES = 'http://employee-service:5002';
const BASE_URL_FLIGHTS = 'http://flight-service:5003';
const BASE_URL_RESERVATIONS_COMMAND = 'http://booking-command-service:5004';
const BASE_URL_RESERVATIONS_QUERY = 'http://booking-query-service:5006';
const BASE_URL_SAGA_ORCHESTRATOR = 'http://orchestrator:5005';

// Serviços
const authServiceProxy = httpProxy(BASE_URL_AUTH);
const clientsServiceProxy = httpProxy(BASE_URL_CLIENTS);
const employeesServiceProxy = httpProxy(BASE_URL_EMPLOYEES);
const flightsServiceProxy = httpProxy(BASE_URL_FLIGHTS);
const reservationsServiceProxy = httpProxy(BASE_URL_RESERVATIONS_COMMAND);
const reservationsQueryServiceProxy = httpProxy(BASE_URL_RESERVATIONS_QUERY);

// Set para armazenar tokens invalidados (blacklist)
const blacklistedTokens = new Set();
const JWT_SECRET = Buffer.from(process.env.JWT_SECRET, 'base64');

function verifyJWT(req, res, next) {
    const token = req.headers['x-access-token'] || (req.headers['authorization'] && req.headers['authorization'].split(' ')[1]);
    if (!token)
        return res.status(401).json({ auth: false, message: 'Token não fornecido.' });

    // Verifica se o token está na blacklist
    if (blacklistedTokens.has(token)) {
        return res.status(401).json({ auth: false, message: 'Token invalidado.' });
    }

    jwt.verify(token, JWT_SECRET, function (err, decoded) {
        if (err) {
            console.log('Erro na verificação do token:', err.message);
            return res.status(401).json({ auth: false, message: 'Token inválido ou expirado.' });
        }
        
        req.userId = decoded.sub; // Email do usuário (subject)
        req.userRole = decoded.role; // Tipo de usuário (CLIENTE/FUNCIONARIO)
        req.userEmail = decoded.sub; // Para compatibilidade
        req.user = decoded; // Objeto completo decodificado
        next();
    });
}

// Middleware para checar permissão por tipo de usuário
function authorizeRoles(...allowedRoles) {
    return (req, res, next) => {
        const token = req.headers['x-access-token'] || (req.headers['authorization'] && req.headers['authorization'].split(' ')[1]);
        if (!token) return res.status(401).json({ message: 'Token não fornecido.' });

        jwt.verify(token, JWT_SECRET, function (err, decoded) {
            if (err) {
                console.log('Erro na verificação do token:', err.message);
                return res.status(401).json({ message: 'Token inválido ou expirado.' });
            }
            
            // Pega o role do token decodificado
            const userRole = decoded.role;
            
            // Verifica se o token tem o campo role
            if (!userRole) {
                return res.status(403).json({ message: 'Token não contém informações de permissão.' });
            }
            
            // Verifica se o usuário tem permissão
            if (!allowedRoles.includes(userRole)) {
                return res.status(403).json({ 
                    message: `Acesso negado. Requer: ${allowedRoles.join(' ou ')}. Você é: ${userRole}` 
                });
            }
            
            // Adiciona as informações do usuário na requisição
            req.user = decoded;
            req.userId = decoded.sub;
            req.userRole = decoded.role;
            req.userEmail = decoded.sub;
            next();
        });
    };
}

app.post('/login', async (req, res) => {
    try {
        // Adapta o corpo para o serviço de autenticação
        console.log(req.body)
        const authBody = {
            login: req.body.login || req.body.email,  
            senha: req.body.senha || req.body.password, 
        };
        console.log(authBody)
        // Chamada para o serviço de autenticação
        const authResponse = await axios.post(`${BASE_URL_AUTH}/login`, authBody);
        console.log(authResponse)
        const authData = authResponse.data;

        // Se não veio id, login inválido
        if (!authData.usuario.id) {
            return res.status(401).json({ message: 'Login inválido!' });
        }

        // Descobre tipo e busca dados completos
        let usuarioResponse;
        let tipo = authData.tipo;
        let email = authData.usuario.email
        let codigo = authData.codigo || authData.usuario.id;
        let access_token = authData.access_token
        let token_type = authData.token_type

        if (tipo === 'CLIENTE') {
            usuarioResponse = await axios.get(`${BASE_URL_CLIENTS}/clientes/email/${email}/dto`);
        } else if (tipo === 'FUNCIONARIO') {
            usuarioResponse = await axios.get(`${BASE_URL_EMPLOYEES}/funcionarios/email/${email}`);
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
        console.log(req.body)
        // 1. Inicia a SAGA de criação de cliente
        const sagaResponse = await axios.post(
            `${BASE_URL_SAGA_ORCHESTRATOR}/saga/usuarios/cliente`,
            req.body,
            { 
                headers: {
                        'Content-Type': 'application/json' 
                    } 
                }
        );

        const { correlationId } = sagaResponse.data;
        if (!correlationId) {
            return res.status(500).json({ message: 'Saga não retornou correlationId.' });
        }

        // 3. Polling até finalizar a saga
        const maxAttempts = 20;
        const intervalMs = 1500;
        let attempts = 0;

        async function pollSagaStatus() {
            try {
                const statusResponse = await axios.get(
                    `${BASE_URL_SAGA_ORCHESTRATOR}/saga/${correlationId}`,
                    { headers: { 'Content-Type': 'application/json' } }
                );
                const { status } = statusResponse.data;

                if (status === 'COMPLETED_SUCCESS' || status === 'COMPLETED_ERROR') {
                    if (status === 'COMPLETED_ERROR') {
                        const errorResponse = {
                            status: 'COMPLETED_ERROR',
                            message: 'Falha ao processar o cadastro do cliente',
                            failedServices: statusResponse.data.failedServices || [],
                        };
                        let errorInfo = statusResponse.data.errorInfo
                        console.log(errorInfo)
                        console.log(errorInfo.errorCode)
                        // 409 para conflito, 400 para outros erros
                        if (errorInfo.errorCode == 409) {
                            errorResponse.message = 'Cliente já existente.';
                            return res.status(409).json(errorResponse);
                        }
                        errorResponse.message = errorInfo.errorMessage || 'Ocorreu um erro ao cadastrar o cliente'
                        return res.status(errorInfo.errorCode || 400).json(errorResponse)
                    }
                    // SUCESSO - Busca o cliente criado pelo email
                    if (status === 'COMPLETED_SUCCESS') {
                        try {
                            // Aguarda alguns segundos para garantir que os dados foram persistidos
                            await new Promise(resolve => setTimeout(resolve, 1000));

                            // Busca o cliente pelo email
                            const clienteResponse = await axios.get(`${BASE_URL_CLIENTS}/clientes/email/${req.body.email}/dto`);
                            const clienteCriado = clienteResponse.data;

                            return res.status(201).json(clienteCriado);
                        } catch (clientError) {
                            console.error('Erro ao buscar cliente criado:', clientError.message);
                            // Se não conseguir buscar o cliente, retorna resposta básica da saga
                            return res.status(201).json({
                                message: 'Cliente criado com sucesso',
                                correlationId,
                                email: req.body.email
                            });
                        }
                    }
                    // Fallback
                    return res.status(200).json(statusResponse.data);
                } else if (attempts < maxAttempts) {
                    attempts++;
                    setTimeout(pollSagaStatus, intervalMs);
                } else {
                    return res.status(202).json({
                        message: 'Saga ainda em andamento.',
                        status,
                        correlationId
                    });
                }
            } catch (err) {
                return res.status(500).json({
                    message: 'Erro ao consultar status da SAGA.',
                    error: err.message
                });
            }
        }
        pollSagaStatus();
    } catch (error) {
        console.error('Erro ao processar /clientes:', error.message);
        if (error.response) {
            res.status(error.response.status).json(error.response.data);
        } else {
            res.status(500).json({ message: 'Erro interno ao processar a requisição.' });
        }
    }
});

app.get('/clientes/:codigoCliente', verifyJWT, authorizeRoles('CLIENTE'), (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

app.put('/clientes/:codigoCliente/milhas', verifyJWT, async (req, res, next) => {
    const userRole = req.user.role;
    const clienteId = req.params.codigoCliente;

    // Permite se for FUNCIONARIO ou se for o próprio CLIENTE
    if (userRole === 'FUNCIONARIO') {
        return clientsServiceProxy(req, res, next);
    } else if (userRole === 'CLIENTE') {
        try {
            const clienteResponse = await axios.get(`${BASE_URL_CLIENTS}/clientes/email/${req.user.sub}/dto`);
            const clienteCodigo = clienteResponse.data.codigo.toString();
            
            if (clienteCodigo !== clienteId) {
                return res.status(403).json({ message: 'Acesso negado - você só pode alterar suas próprias milhas' });
            }
            return clientsServiceProxy(req, res, next);
        } catch (error) {
            return res.status(403).json({ message: 'Acesso negado' });
        }
    } else {
        return res.status(403).json({ message: 'Acesso negado' });
    }
});

app.get('/clientes/:codigoCliente/milhas', verifyJWT, async (req, res, next) => {
    const userRole = req.user.role;
    const clienteId = req.params.codigoCliente;

    // Permite se for FUNCIONARIO ou se for o próprio CLIENTE
    if (userRole === 'FUNCIONARIO') {
        return clientsServiceProxy(req, res, next);
    } else if (userRole === 'CLIENTE') {
        try {
            const clienteResponse = await axios.get(`${BASE_URL_CLIENTS}/clientes/email/${req.user.sub}/dto`);
            const clienteCodigo = clienteResponse.data.codigo.toString();
            
            if (clienteCodigo !== clienteId) {
                return res.status(403).json({ message: 'Acesso negado - você só pode ver suas próprias milhas' });
            }
            return clientsServiceProxy(req, res, next);
        } catch (error) {
            return res.status(403).json({ message: 'Acesso negado' });
        }
    } else {
        return res.status(403).json({ message: 'Acesso negado' });
    }
});

app.get('/clientes', verifyJWT, (req, res, next) => {
    clientsServiceProxy(req, res, next);
});

// Rotas para o serviço de Reservas

// (via Orquestrador Saga) FUNCIONAL
app.post('/reservas', verifyJWT, authorizeRoles('CLIENTE'), async (req, res) => {
    try {
        // 1. Inicia a SAGA
        const sagaResponse = await axios.post(
            `${BASE_URL_SAGA_ORCHESTRATOR}/api/orchestrator/reservation`,
            req.body,
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );

        const { correlationId } = sagaResponse.data;
        if (!correlationId) {
            return res.status(500).json({ message: 'Saga não retornou correlationId.' });
        }

        // 2. Polling até finalizar
        const maxAttempts = 20;
        const intervalMs = 1500;
        let attempts = 0;

        async function pollSagaStatus() {
            try {
                const statusResponse = await axios.get(
                    `${BASE_URL_SAGA_ORCHESTRATOR}/saga/${correlationId}`,
                    { headers: { 'Content-Type': 'application/json' } }
                );
                const { status } = statusResponse.data;

                if (status === 'COMPLETED_SUCCESS' || status === 'COMPLETED_ERROR') {
                    // TRATAMENTO DE ERRO (CONFLICT e outros)
                    if (status === 'COMPLETED_ERROR') {
                        const errorResponse = {
                            status: 'COMPLETED_ERROR',
                            message: 'Falha ao processar a reserva',
                            failedServices: statusResponse.data.failedServices || [],
                        };
                        
                        // Verifica se foi erro de MILHAS com código específico
                        if (statusResponse.data.failedServices && 
                            statusResponse.data.failedServices.includes('MILHAS')) {
                            
                            if (statusResponse.data.errorInfo) {
                                const errorCode = statusResponse.data.errorInfo.errorCode;
                                if (errorCode === 404) {
                                    errorResponse.message = 'Cliente não encontrado';
                                    errorResponse.erro = 'Cliente não encontrado';
                                    return res.status(404).json(errorResponse);
                                } else if (errorCode === 409) {
                                    errorResponse.message = 'Saldo de milhas insuficiente';
                                    errorResponse.erro = 'Saldo de milhas insuficiente';
                                    return res.status(400).json(errorResponse);
                                } else {
                                    errorResponse.message = statusResponse.data.errorInfo.errorMessage || 'Erro no serviço de milhas';
                                    errorResponse.erro = statusResponse.data.errorInfo.errorMessage || 'Erro no serviço de milhas';
                                    return res.status(errorCode || 400).json(errorResponse);
                                }
                            } else {
                                errorResponse.message = 'Saldo de milhas insuficiente';
                                errorResponse.erro = 'Saldo de milhas insuficiente';
                                return res.status(400).json(errorResponse);
                            }
                        }
                        
                        // Verifica se foi erro de VOO com código específico
                        if (statusResponse.data.failedServices && 
                            statusResponse.data.failedServices.includes('VOO')) {
                            
                            if (statusResponse.data.errorInfo) {
                                const errorCode = statusResponse.data.errorInfo.errorCode;
                                if (errorCode === 404) {
                                    errorResponse.message = 'Voo não encontrado';
                                    return res.status(404).json(errorResponse);
                                } else if (errorCode === 409) {
                                    errorResponse.message = 'Poltronas insuficientes no voo selecionado';
                                    return res.status(409).json(errorResponse);
                                } else {
                                    errorResponse.message = statusResponse.data.errorInfo.errorMessage || 'Erro no serviço de voo';
                                    return res.status(errorCode || 400).json(errorResponse);
                                }
                            } else {
                                errorResponse.message = 'Poltronas insuficientes no voo selecionado';
                                return res.status(409).json(errorResponse);
                            }
                        }
                        
                        // Outros erros genéricos
                        return res.status(400).json(errorResponse);
                    }
                    
                    // TRATAMENTO DE SUCESSO (API Composition)
                    if (status === 'COMPLETED_SUCCESS' && statusResponse.data.result) {
                        const result = statusResponse.data.result;
                        
                        // Se for uma reserva, faz API Composition
                        if (result.type === 'reservation' && result.codigoReserva) {
                            try {
                                // Aguarda alguns segundos para o CQRS propagar os dados
                                await new Promise(resolve => setTimeout(resolve, 2000));

                                // Busca detalhes da reserva criada
                                const reservaResponse = await axios.get(`${BASE_URL_RESERVATIONS_QUERY}/reservas/${result.codigoReserva}`);
                                const reserva = reservaResponse.data;

                                // Busca detalhes do voo
                                const vooResponse = await axios.get(`${BASE_URL_FLIGHTS}/voos/${reserva.codigo_voo}`);
                                const voo = vooResponse.data;

                                // Remove os campos indesejados da reserva e adiciona o objeto voo
                                const { codigo_voo, codigo_aeroporto_origem, codigo_aeroporto_destino, ...reservaSemCampos } = reserva;
                                
                                const resposta = {
                                    ...reservaSemCampos,
                                    voo
                                };

                                return res.status(201).json(resposta);
                            } catch (error) {
                                console.error('Erro no API Composition:', error);
                                // Se API Composition falhar, retorna só o código da reserva
                                return res.status(201).json({
                                    codigo_reserva: result.codigoReserva,
                                    message: 'Reserva criada com sucesso, mas falha ao buscar detalhes completos'
                                });
                            }
                        }
                    }
                    
                    // Fallback para outros casos de sucesso
                    return res.status(200).json(statusResponse.data);
                    
                } else if (attempts < maxAttempts) {
                    attempts++;
                    setTimeout(pollSagaStatus, intervalMs);
                } else {
                    return res.status(202).json({ 
                        message: 'Saga ainda em andamento.', 
                        status,
                        correlationId 
                    });
                }
            } catch (err) {
                return res.status(500).json({ 
                    message: 'Erro ao consultar status da SAGA.', 
                    error: err.message 
                });
            }
        }

        pollSagaStatus();

    } catch (error) {
        console.error('API Gateway: Erro ao processar POST /reservas via Serviço Saga:', error.message);
        if (error.response) {
            res.status(error.response.status).json(error.response.data);
        } else {
            res.status(500).json({ 
                message: 'Erro interno no API Gateway ao processar a reserva.' 
            });
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
            `${BASE_URL_SAGA_ORCHESTRATOR}/api/orchestrator/reservation/reservas/${codigoReserva}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );

        const { correlationId } = sagaServiceResponse.data;
        if (!correlationId) {
            return res.status(500).json({ message: 'Saga não retornou correlationId.' });
        }

        // Polling até finalizar
        const maxAttempts = 20;
        const intervalMs = 1500;
        let attempts = 0;

        async function pollSagaStatus() {
            try {
                const statusResponse = await axios.get(
                    `${BASE_URL_SAGA_ORCHESTRATOR}/saga/${correlationId}`,
                    { headers: { 'Content-Type': 'application/json' } }
                );
                const { status } = statusResponse.data;

                if (status === 'COMPLETED_SUCCESS' || status === 'COMPLETED_ERROR') {
                    if (status === 'COMPLETED_ERROR') {
                        const errorResponse = {
                            status: 'COMPLETED_ERROR',
                            message: 'Falha ao cancelar a reserva',
                            failedServices: statusResponse.data.failedServices || [],
                        };
                        
                        let errorInfo = statusResponse.data.errorInfo;
                        if (errorInfo) {
                            if (errorInfo.errorCode === 404) {
                                errorResponse.message = 'Reserva não encontrada';
                                return res.status(404).json(errorResponse);
                            } else if (errorInfo.errorMessage && errorInfo.errorMessage.includes('não pode ser cancelada')) {
                                errorResponse.message = 'Reserva não pode ser cancelada no status atual';
                                return res.status(400).json(errorResponse);
                            } else if (errorInfo.errorMessage && errorInfo.errorMessage.includes('Cliente não encontrado')) {
                                errorResponse.message = 'Cliente não encontrado para devolução de milhas';
                                return res.status(400).json(errorResponse);
                            } else {
                                errorResponse.message = errorInfo.errorMessage || 'Erro ao cancelar reserva';
                                return res.status(errorInfo.errorCode || 400).json(errorResponse);
                            }
                        }
                        
                        // Verifica se falhou na devolução de milhas
                        if (statusResponse.data.failedServices && 
                            statusResponse.data.failedServices.includes('DEVOLVER_MILHAS')) {
                            errorResponse.message = 'Falha na devolução de milhas. O cancelamento foi revertido.';
                            return res.status(400).json(errorResponse);
                        }
                        
                        return res.status(400).json(errorResponse);
                    }
                    
                    // SUCESSO - Busca dados da reserva cancelada
                    if (status === 'COMPLETED_SUCCESS' && statusResponse.data.result) {
                        const result = statusResponse.data.result;
                        
                        if (result.type === 'cancellation' && result.codigoReserva) {
                            try {
                                // Aguarda para CQRS propagar
                                await new Promise(resolve => setTimeout(resolve, 2000));

                                // Busca detalhes da reserva cancelada
                                const reservaResponse = await axios.get(`${BASE_URL_RESERVATIONS_QUERY}/reservas/${result.codigoReserva}`);
                                const reserva = reservaResponse.data;

                                // Busca os detalhes do voo associado à reserva
                                const vooResponse = await axios.get(`${BASE_URL_FLIGHTS}/voos/${reserva.codigo_voo}`, {
                                    headers: { Authorization: req.headers['authorization'] }
                                });

                                const voo = vooResponse.data;

                                // Monta resposta final no formato igual ao GET /reservas/:codigoReserva
                                // Remove os campos indesejados da reserva antes de montar a resposta
                                const { codigo_voo, codigo_aeroporto_origem, codigo_aeroporto_destino, ...reservaSemCampos } = reserva;
                                
                                const reservaComVoo = {
                                    ...reservaSemCampos,
                                    voo
                                };

                                return res.status(200).json(reservaComVoo);
                            } catch (error) {
                                console.error('Erro ao buscar reserva cancelada:', error);
                                // Fallback se não conseguir buscar detalhes
                                return res.status(200).json({
                                    codigo: result.codigoReserva,
                                    estado: 'CANCELADA',
                                    message: 'Reserva cancelada com sucesso. Milhas devolvidas ao saldo.'
                                });
                            }
                        }
                    }
                    
                    // Fallback
                    return res.status(200).json(statusResponse.data);
                    
                } else if (attempts < maxAttempts) {
                    attempts++;
                    setTimeout(pollSagaStatus, intervalMs);
                } else {
                    return res.status(202).json({ 
                        message: 'Cancelamento ainda em andamento.', 
                        status,
                        correlationId 
                    });
                }
            } catch (err) {
                return res.status(500).json({ 
                    message: 'Erro ao consultar status da SAGA de cancelamento.', 
                    error: err.message 
                });
            }
        }

        pollSagaStatus();

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
        const reservaResponse = await axios.get(`${BASE_URL_RESERVATIONS_QUERY}/reservas/${req.params.codigoReserva}`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const reserva = reservaResponse.data;

        // Busca os detalhes do voo associado à reserva
        const vooResponse = await axios.get(`${BASE_URL_FLIGHTS}/voos/${reserva.codigo_voo}`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const voo = vooResponse.data;

        // Monta resposta final no formato desejado
        // Remove o campo codigo_voo da reserva antes de montar a resposta
        const { codigo_voo, codigo_aeroporto_origem, codigo_aeroporto_destino, ...reservaSemCampos } = reserva;
        const resposta = {
            ...reservaSemCampos,
            voo
        };

        // Retorna a resposta com os dados combinados
        res.status(200).json(resposta);
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

app.get('/clientes/:codigoCliente/reservas', verifyJWT, async (req, res) => {
    const clienteId = req.params.codigoCliente; 

    try {
        // Busca todas as reservas do cliente
        const reservasResponse = await axios.get(`${BASE_URL_RESERVATIONS_QUERY}/reservas/clientes/${clienteId}/reservas`, {
            headers: { Authorization: req.headers['authorization'] }
        });

        const reservas = reservasResponse.data;

        // Se não há reservas, retorna 204
        if (!reservas || reservas.length === 0) {
            return res.status(204).send();
        }

        // Para cada reserva, busca os detalhes do voo associado
        const reservasComVoos = await Promise.all(reservas.map(async (reserva) => {
            try {
                const vooResponse = await axios.get(`${BASE_URL_FLIGHTS}/voos/${reserva.codigo_voo}`, {
                    headers: { Authorization: req.headers['authorization'] }
                });
                const voo = vooResponse.data;

                // Remove os campos indesejados da reserva
                const { codigo_voo, codigo_aeroporto_origem, codigo_aeroporto_destino, ...reservaSemCampos } = reserva;

                return { ...reservaSemCampos, voo };
            } catch (vooError) {
                console.error(`Erro ao buscar voo para a reserva ${reserva.codigo}:`, vooError.message);
                return { ...reserva, voo: null }; // Retorna a reserva mesmo se o voo não for encontrado
            }
        }));

        // Retorna a resposta consolidada
        res.status(200).json(reservasComVoos);
    } catch (error) {
        console.error('Erro ao buscar reservas:', error.message);
        // Trata erros de resposta da API
        if (error.response) {
            console.log('Status do erro:', error.response.status);
            // Se o booking-query retornar 404 (não encontrado), transformar em 204 (vazio)
            if (error.response.status === 404) {
                return res.status(204).send();
            }
            return res.status(error.response.status).json(error.response.data);
        }
        // Trata erros genéricos
        res.status(500).json({ message: 'Erro ao buscar reservas com detalhes dos voos.', error: error.message });
    }
});

app.patch('/reservas/:codigoReserva/estado', verifyJWT, async (req, res, next) => {
    const { estado } = req.body;
    const userRole = req.user.role;
    const codigoReserva = req.params.codigoReserva;
    
    // Se o estado é CHECK-IN, tanto CLIENTE quanto FUNCIONARIO podem alterar
    if (estado === 'CHECK-IN') {
        if (userRole === 'FUNCIONARIO') {
            return reservationsServiceProxy(req, res, next);
        } else if (userRole === 'CLIENTE') {
            // Verifica se o cliente é o dono da reserva
            try {
                // Busca dados do cliente logado
                const clienteResponse = await axios.get(`${BASE_URL_CLIENTS}/clientes/email/${req.user.sub}/dto`);
                const clienteCodigo = clienteResponse.data.codigo;
                
                // Busca dados da reserva
                const reservaResponse = await axios.get(`${BASE_URL_RESERVATIONS_QUERY}/reservas/${codigoReserva}`);
                const reserva = reservaResponse.data;
                
                // Verifica se o cliente é o dono da reserva
                if (reserva.codigo_cliente === clienteCodigo) {
                    return reservationsServiceProxy(req, res, next);
                } else {
                    return res.status(403).json({ 
                        error: 'Permissão negada', 
                        message: 'Você só pode fazer check-in em suas próprias reservas' 
                    });
                }
            } catch (error) {
                console.error('Erro ao verificar propriedade da reserva:', error.message);
                return res.status(403).json({ 
                    error: 'Permissão negada', 
                    message: 'Erro ao verificar permissões' 
                });
            }
        }
    }
    // Para outros estados, apenas FUNCIONARIO pode alterar
    else {
        if (userRole === 'FUNCIONARIO') {
            return reservationsServiceProxy(req, res, next);
        }
    }
    
    // Se chegou até aqui, não tem permissão
    return res.status(403).json({ 
        error: 'Permissão negada', 
        message: `Apenas funcionários podem alterar o estado para ${estado}` 
    });
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
app.post('/logout', verifyJWT, function (req, res) {
    const token = req.headers['x-access-token'] || (req.headers['authorization'] && req.headers['authorization'].split(' ')[1]);
    
    if (!token) {
        return res.status(400).json({ message: 'Token não fornecido.' });
    }
    
    // Adiciona o token à blacklist para invalidá-lo
    // blacklistedTokens.add(token);
    
    // Opcional: log para debug
    //console.log(`Token invalidado no logout: ${token.substring(0, 20)}...`);
    
    // Retorna confirmação do logout
    res.status(200).json({ 
        login: req.userEmail || req.userId 
    });
});

// Cria o servidor na porta 3000
var server = http.createServer(app);
server.listen(3000);