# language: pt
Funcionalidade: Exibição do Catálogo de Joias

  Cenário: Listagem da vitrine principal apenas com produtos ativos
    Dado que existe um "Anel de Prata" ativo no banco de dados
    E existe um "Colar Antigo" inativo no banco de dados
    Quando o sistema solicitar a lista de produtos da vitrine
    Então o sistema deve retornar 1 produto
    E o produto retornado deve ser o "Anel de Prata"

  Cenário: Filtragem de produtos por categoria
    Dado que existe um "Anel de Prata" da categoria "aneis"
    E existe uma "Pulseira de Ouro" da categoria "pulseiras"
    Quando o sistema buscar os produtos pela categoria "aneis"
    Então o sistema deve retornar 1 produto
    E o produto retornado deve ser o "Anel de Prata"