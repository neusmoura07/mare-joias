# language: pt
Funcionalidade: Políticas de Segurança de Rede (CORS e Rate Limit)

  Como um arquiteto de software da MareJoias
  Quero que a API valide as origens das requisições e limite a frequência de acessos
  Para permitir a integração fluida com o frontend e proteger o servidor contra sobrecargas

  Cenário: Permitir requisição (Preflight) de uma origem front-end autorizada
    Dado que a API está configurada para aceitar a origem "http://localhost:3000"
    Quando o navegador enviar uma requisição OPTIONS para "/api/v1/products" com o cabeçalho de origem "http://localhost:3000"
    Então o sistema deve processar a requisição com sucesso
    E retornar o status HTTP 200 OK
    E o cabeçalho da resposta deve conter "Access-Control-Allow-Origin" igual a "http://localhost:3000"

  Cenário: Proteger a API contra abusos retornando erro de limite excedido
    Dado que o limite de taxa de requisições da API é configurado para 5 por minuto por IP
    E um determinado IP já enviou 5 requisições válidas no último minuto
    Quando esse mesmo IP enviar a 6ª requisição GET para "/api/v1/products"
    Então o sistema deve interceptar e recusar a requisição
    E retornar o status HTTP 429 Too Many Requests
