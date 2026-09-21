# language: pt
Funcionalidade: Processamento de Pedido e Checkout

  Cenário: Cálculo correto do valor total do pedido com frete e itens
    Dado que o cliente possui um carrinho com "Anel de Prata" custando R$ 150,00 e quantidade 1
    E possui "Corrente de Ouro" custando R$ 300,00 e quantidade 1
    Quando o sistema processar a criação do pedido com taxa de frete de R$ 25,00
    Então o valor total do pedido deve ser R$ 475,00
    E o status do pedido deve ser iniciado como "PENDING_PAYMENT"

  Cenário: Falha ao tentar fechar pedido com produto sem estoque suficiente
    Dado que o produto "Brinco de Diamante" possui apenas 1 unidade disponível em estoque
    Quando o cliente tentar fechar o pedido solicitando 2 unidades deste produto
    Então o sistema deve recusar o checkout
    E retornar uma mensagem de erro informando que o estoque é insuficiente