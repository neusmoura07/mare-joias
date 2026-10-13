# language: pt
Funcionalidade: Tratamento Global de Exceções da API

  Como um desenvolvedor frontend consumindo a API
  Quero que os erros retornem em um formato JSON padronizado
  Para poder exibir mensagens claras e amigáveis para os usuários finais

  Cenário: Tentativa de busca por um produto que não existe
    Dado que a API está rodando perfeitamente
    Quando eu enviar uma requisição GET para "/api/v1/products/slug-inexistente-123"
    Então a API deve interceptar a exceção de entidade não encontrada
    E retornar o status HTTP 404 Not Found
    E o corpo da resposta deve conter o campo "message" com "Produto não encontrado"
    E o corpo da resposta deve conter a data e hora do erro no campo "timestamp"

  Cenário: Erro de validação de formulário (Bad Request)
    Dado que estou autenticado como ADMIN
    Quando eu enviar um POST para "/api/v1/products" com o campo "name" em branco
    Então a API deve interceptar o erro de validação
    E retornar o status HTTP 400 Bad Request
    E a lista de erros deve indicar que o campo "name" é obrigatório