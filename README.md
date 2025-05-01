## Funcionamento do Cadastro e Login (Protótipo)

### Cadastro
- O cadastro é completamente **fake** e não realiza validações reais no backend.
- Os dados do cliente são armazenados no `localStorage` do navegador.
- Após o cadastro, uma mensagem de confirmação é exibida informando que a senha foi enviada para o email (simulação). A senha padrão é sempre **"1234"**.
- O usuário é redirecionado para a página de login após o cadastro.

### Login
- O login também é **fake** e não realiza autenticação real.
- Qualquer email e senha podem ser usados para fazer login como **cliente**.
- Se o email utilizado for **funcionario@empresa.com**, o sistema identifica o usuário como **funcionário** e redireciona para a página de funcionário.
- Para qualquer outro email, o sistema redireciona para a página de cliente.

### Observação
Este comportamento é apenas para fins de prototipação e demonstração. Em um sistema real, seria necessário implementar autenticação e autorização seguras com backend e banco de dados.