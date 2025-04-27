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

// Serviços
const authServiceProxy = httpProxy(BASE_URL_AUTH);
const clientsServiceProxy = httpProxy(BASE_URL_CLIENTS);
const emplooyeesServiceProxy = httpProxy(BASE_URL_EMPLOYEES);
const flightsServiceProxy = httpProxy(BASE_URL_FLIGHTS);

function verifyJWT(req, res, next) {
    const token = req.headers['x-access-token'];
    if (!token)
        return res.status(401).json({ auth: false, message: 'Token não fornecido.' });
    jwt.verify(token, process.env.SECRET, function (err, decoded) {
        if (err)
            return res.status(500).json({ auth: false, message: 'Falha ao autenticar o token.' });
        // se tudo estiver ok, salva no request para uso posterior
        req.userId = decoded.id;
        next();
    });
}

// Rotas públicas
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
            return res.status(400).json({ message: 'Tipo de usuário desconhecido.' });
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

app.post('/logout', function (req, res) {
    res.json({ auth: false, token: null });
})

app.post('/clientes', (req, res, next) => {
    clientsServiceProxy(req, res, next); 
});

// Rotas autenticadas com verifyJWT 
// app.get('/clientes/:id', verifyJWT, (req, res, next) => {
//     clientesServiceProxy(req, res, next);
// });


// Configurações do app
app.use(logger('dev'));
app.use(helmet());
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());

// Cria o servidor na porta 3000
var server = http.createServer(app);
server.listen(3000);