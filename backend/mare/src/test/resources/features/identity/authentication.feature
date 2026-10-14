# language: pt
Funcionalidade: Autenticação e Registo de Utilizadores
  Como um cliente da MareJoias
  Quero poder registar-me e iniciar sessão
  Para obter um token de acesso às rotas protegidas

  Cenário: Registo de um novo utilizador com sucesso
    Dado que eu tenho os seguintes dados de registo:
      | name  | email             | password | cpf         | role     |
      | Nuno  | nuno@teste.com    | 123456   | 11122233344 | CUSTOMER |
    Quando eu envio um pedido POST para "/api/v1/auth/register" com esses dados
    Então a resposta deve ter o status 201

  Cenário: Falha ao registar utilizador com email duplicado
    Dado que já existe um utilizador com o email "nuno@teste.com" na base de dados
    E que eu tenho os seguintes dados de registo:
      | name  | email             | password | cpf         | role     |
      | Nuno2 | nuno@teste.com    | 654321   | 99988877766 | CUSTOMER |
    Quando eu envio um pedido POST para "/api/v1/auth/register" com esses dados
    Então a resposta deve ter o status 400

  Cenário: Login com sucesso e geração de Token JWT
    Dado que já existe um utilizador com o email "login@teste.com" e senha "senha123" na base de dados
    Quando eu envio um pedido POST para "/api/v1/auth/login" com o email "login@teste.com" e senha "senha123"
    Então a resposta deve ter o status 200
    E o corpo da resposta deve conter o campo "token" preenchido