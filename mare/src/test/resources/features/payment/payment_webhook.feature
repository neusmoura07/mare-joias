# language: pt
Funcionalidade: Processamento de Webhook de Pagamento

  Como um Gateway de Pagamento Externo
  Quero enviar notificações de mudança de status de pagamento para a API
  Para que o e-commerce atualize o status do pedido do cliente

  Cenário: Atualizar pedido para pago com sucesso
    Dado que existe um pedido com o status "PENDING"
    Quando o gateway enviar um webhook de pagamento com o status "SUCCESS" para este pedido
    Então a API de pagamentos deve retornar o status HTTP 200
    E o status do pedido na base de dados deve ser atualizado para "PAID"

  Cenário: Impedir processamento duplo de pagamento
    Dado que existe um pedido com o status "PAID"
    Quando o gateway enviar um webhook de pagamento com o status "SUCCESS" para este pedido
    Então a API de pagamentos deve recusar a operação
    E a API de pagamentos deve retornar o status HTTP 422
    E a API de pagamentos deve retornar uma mensagem de erro informando "Este pedido já foi pago anteriormente"

  Cenário: Pagamento recusado ou cancelado pelo cliente
    Dado que existe um pedido com o status "PENDING"
    Quando o gateway enviar um webhook de pagamento com o status "FAILED" para este pedido
    Então a API de pagamentos deve retornar o status HTTP 200
    E o status do pedido na base de dados deve ser atualizado para "CANCELLED"

  Cenário: Retornar erro ao receber webhook de um pedido inexistente
    Dado que não existe nenhum pedido com o ID informado
    Quando o gateway enviar um webhook de pagamento com o status "SUCCESS" para um pedido inexistente
    Então a API de pagamentos deve retornar o status HTTP 404
    E a API de pagamentos deve retornar uma mensagem de erro informando "Pedido não encontrado"