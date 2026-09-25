-- V2: Criação da tabela de múltiplos endereços para os usuários (clientes)

CREATE TABLE addresses (
                           id UUID PRIMARY KEY,
                           user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                           zip_code VARCHAR(20) NOT NULL,
                           street VARCHAR(255) NOT NULL
);