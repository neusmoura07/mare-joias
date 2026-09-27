# language: pt
Funcionalidade: Gestão de Catálogo e Montra de Produtos
  Como um cliente ou administrador do e-commerce
  Quero interagir com a montra de produtos e gerir o inventário
  Para que os clientes possam navegar e a administração possa controlar o stock

  Cenário: Listagem de produtos ativos na montra pública
    Dado que existem produtos cadastrados e ativos no sistema
    Quando um utilizador aceder à listagem de produtos da montra
    Então o sistema deve retornar os produtos com status HTTP 200 (OK)
    E apenas os produtos com o estado ativo devem ser exibidos

  Cenário: Filtrar produtos por categoria com sucesso
    Dado que existe uma categoria cadastrada com o slug "anel-de-ouro" contendo produtos ativos
    Quando um cliente filtrar os produtos utilizando o slug "anel-de-ouro"
    Então o sistema deve retornar apenas os produtos pertencentes a essa categoria
    E retornar o status HTTP 200 (OK)

  Cenário: Bloqueio de cadastro de produto por utilizador sem permissão de Administrador
    Dado que estou autenticado como um cliente comum (CUSTOMER)
    Quando tentar enviar uma requisição POST para criar um novo produto no catálogo
    Então o sistema deve recusar a operação
    E retornar o status HTTP 403 (Forbidden)
