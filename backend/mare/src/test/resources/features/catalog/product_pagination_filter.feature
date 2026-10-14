# language: pt
Funcionalidade: Paginação, Filtros e Busca de Produtos

  Como um cliente ou visitante do e-commerce
  Quero poder listar os produtos aplicando paginação e filtros por nome ou categoria
  Para encontrar os itens desejados de forma rápida e eficiente

  Cenário: Listar produtos com paginação padrão com sucesso
    Dado que existem produtos cadastrados no catálogo
    Quando eu requisitar a listagem de produtos paginada na página 0 com tamanho 10
    Então o resultado HTTP retornado deve ser 200 OK
    E a resposta deve conter uma página com os produtos

  Cenário: Filtrar produtos por nome parcial com sucesso
    Dado que o catálogo possui o produto "Colar de Ouro" e "Brinco de Prata"
    Quando eu requisitar a listagem de produtos filtrando pelo nome "Colar"
    Então o resultado HTTP retornado deve ser 200 OK
    E a listagem deve conter o produto "Colar de Ouro"
    E a listagem não deve conter o produto "Brinco de Prata"

  Cenário: Filtrar produtos por categoria específica
    Dado que o catálogo possui o produto "Anel de Ouro 18k" na categoria "Anéis" e "Pulseira de Prata" na categoria "Pulseiras"
    Quando eu requisitar a listagem de produtos filtrando pela categoria "Anéis"
    Então o resultado HTTP retornado deve ser 200 OK
    E a listagem deve conter o produto "Anel de Ouro 18k"
    E a listagem não deve conter o produto "Pulseira de Prata"