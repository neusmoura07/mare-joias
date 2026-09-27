# language: pt
Funcionalidade: API de Finalização de Compra (Checkout API)

  Como um cliente autenticado no e-commerce
  Quero poder enviar um JSON com os dados da minha compra
  Para que o sistema processe o pedido, valide o estoque por tamanho e retorne os status HTTP corretos

  Cenário: API de Checkout com sucesso, cálculo correto do total e baixa de estoque
    Dado que estou autenticado na API como cliente
    E o produto "Aliança de Ouro" possui 5 unidades em estoque para o tamanho "18" custando 50000 centavos
    Quando eu enviar um POST de checkout comprando 2 unidades da "Aliança de Ouro" tamanho "18"
    Então a API deve processar o pedido com sucesso
    E o pedido deve ser salvo na base de dados com o status "PENDING"
    E o valor total do pedido deve ser calculado como 100000 centavos
    E o estoque do tamanho "18" do produto deve ser atualizado para 3 unidades

  Cenário: API deve impedir checkout e retornar HTTP 422 quando não há estoque
    Dado que estou autenticado na API como cliente
    E o produto "Colar de Prata" possui 0 unidades em estoque para o tamanho "Único" custando 15000 centavos
    Quando eu enviar um POST de checkout comprando 1 unidades da "Colar de Prata" tamanho "Único"
    Então a API deve recusar o pedido
    E retornar uma mensagem de erro informando "Estoque insuficiente para o produto Colar de Prata"
    E retornar o status HTTP 422