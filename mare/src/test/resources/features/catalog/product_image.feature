# language: pt
Funcionalidade: Upload de Imagens do Produto

  Como um Administrador do catálogo
  Quero fazer o upload de uma imagem para um produto
  Para que os clientes possam visualizar fotos reais da joia no frontend

  Cenário: Administrador faz upload de imagem com sucesso
    Dado que estou autenticado na API como um Administrador
    E existe um produto "Colar de Pérolas" cadastrado sem imagem
    Quando eu enviar um arquivo de imagem válido para a rota de upload deste produto
    Então a API de catálogo deve retornar o status HTTP 200
    E o produto na base de dados deve ser atualizado com a URL "https://res.cloudinary.com/demo/image/upload/v1234/colar.jpg"

  Cenário: Impedir acesso de clientes comuns na rota de upload
    Dado que estou autenticado na API como um Cliente Comum
    E existe um produto "Anel de Prata" cadastrado sem imagem
    Quando eu tentar enviar um arquivo de imagem para a rota de upload deste produto
    Então a API de catálogo deve recusar a operação
    E a API de catálogo deve retornar o status HTTP 403

  Cenário: Retornar erro ao tentar fazer upload para produto inexistente
    Dado que estou autenticado na API como um Administrador
    E não existe nenhum produto com o ID informado
    Quando eu enviar um arquivo de imagem válido para a rota de upload de um produto inexistente
    Então a API de catálogo deve retornar o status HTTP 404
    E a API de catálogo deve retornar uma mensagem de erro informando "Produto não encontrado para atualização de imagem"