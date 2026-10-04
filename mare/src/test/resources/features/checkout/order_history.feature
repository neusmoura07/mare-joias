# language: pt
Funcionalidade: Histórico e Gestão de Pedidos

  Como um utilizador do sistema (Cliente ou Admin)
  Quero visualizar meus pedidos ou gerenciar o envio
  Para acompanhar as minhas compras ou despachar mercadorias

  Cenário: Cliente visualizando apenas os seus próprios pedidos
    Dado que estou autenticado na API como o cliente "maria@teste.com"
    E existem pedidos salvos para a "maria@teste.com" e para o "joao@teste.com"
    Quando eu fizer uma requisição GET para o meu histórico de pedidos
    Então a API deve retornar o status HTTP 200
    E a lista retornada deve conter apenas os pedidos da "maria@teste.com"
    E a lista não deve conter nenhum pedido do "joao@teste.com"

  Cenário: Administrador atualizando pedido para enviado
    Dado que estou autenticado na API como um Administrador
    E existe um pedido com o status "PAID"
    Quando eu enviar uma requisição PATCH para atualizar o status deste pedido para "SHIPPED"
    Então a API deve retornar o status HTTP 204
    E o status do pedido na base de dados deve ser atualizado para "SHIPPED"
