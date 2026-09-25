# language: pt
Funcionalidade: Gerenciamento de Perfil e Endereços do Cliente

  Cenário: Bloqueio de excesso de endereços
    Dado que o cliente "Maria Oliveira" já possui 3 endereços cadastrados
    Quando o sistema tentar adicionar um quarto endereço com CEP "01000-000"
    Então o sistema deve recusar a adição do endereço
    E retornar uma mensagem de erro informando limite máximo de endereços

  Cenário: Atualizar o perfil do cliente com sucesso
    Dado que existe um cliente com ID válido e nome "João"
    Quando eu enviar um pedido PUT para alterar o nome para "João Atualizado"
    Então o sistema deve atualizar o cliente e retornar o status 204