-- V3__Insert_Initial_Data.sql
-- Carga inicial de dados para desenvolvimento e testes

-- 1. Inserir Usuário ADMIN
-- O hash abaixo equivale à senha: senha123
INSERT INTO users (id, name, email, password_hash, role, created_at)
VALUES (
           '00000000-0000-0000-0000-000000000000',
           'Admin MARÉ',
           'admin@marejoias.com',
           '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQsGbnH2K',
           'ADMIN',
           CURRENT_TIMESTAMP
       );

-- 2. Inserir Categorias Base
INSERT INTO categories (id, name, slug) VALUES
                                            ('11111111-1111-1111-1111-111111111111', 'Anéis', 'aneis'),
                                            ('22222222-2222-2222-2222-222222222222', 'Colares', 'colares'),
                                            ('33333333-3333-3333-3333-333333333333', 'Brincos', 'brincos');

-- 3. Inserir Produtos de Teste (Pratas com a estética tropical)
INSERT INTO products (id, category_id, sku, name, slug, description, material, price_cents, image_url, stock_quantity, is_active) VALUES
                                                                                                                                      (
                                                                                                                                          'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
                                                                                                                                          '11111111-1111-1111-1111-111111111111',
                                                                                                                                          'SKU-ANEL-001',
                                                                                                                                          'Anel Falange Costela de Adão',
                                                                                                                                          'anel-falange-costela-de-adao',
                                                                                                                                          'Anel de falange em prata com design tropical de Costela de Adão. Perfeito para compor um mix maximalista.',
                                                                                                                                          'Prata 925',
                                                                                                                                          8990,
                                                                                                                                          'https://via.placeholder.com/500/f4f4f4/333333?text=Anel+Costela+de+Adao',
                                                                                                                                          15,
                                                                                                                                          TRUE
                                                                                                                                      ),
                                                                                                                                      (
                                                                                                                                          'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
                                                                                                                                          '22222222-2222-2222-2222-222222222222',
                                                                                                                                          'SKU-COL-001',
                                                                                                                                          'Colar Concha do Mar Esculpida',
                                                                                                                                          'colar-concha-do-mar',
                                                                                                                                          'Colar em prata com pingente de concha natural esculpida. A essência do mar no seu pescoço.',
                                                                                                                                          'Prata 925',
                                                                                                                                          18900,
                                                                                                                                          'https://via.placeholder.com/500/f4f4f4/333333?text=Colar+Concha',
                                                                                                                                          10,
                                                                                                                                          TRUE
                                                                                                                                      ),
                                                                                                                                      (
                                                                                                                                          'cccccccc-cccc-cccc-cccc-cccccccccccc',
                                                                                                                                          '33333333-3333-3333-3333-333333333333',
                                                                                                                                          'SKU-BRIN-001',
                                                                                                                                          'Brinco Argola Ondas',
                                                                                                                                          'brinco-argola-ondas',
                                                                                                                                          'Argola texturizada inspirada no movimento das ondas. Design jovem e acessível, a cara do verão.',
                                                                                                                                          'Prata 925',
                                                                                                                                          11990,
                                                                                                                                          'https://via.placeholder.com/500/f4f4f4/333333?text=Brinco+Ondas',
                                                                                                                                          20,
                                                                                                                                          TRUE
                                                                                                                                      );

-- 4. Inserir Tamanhos e Distribuir o Estoque
INSERT INTO product_sizes (id, product_id, size_name, stock_quantity) VALUES
                                                                          ('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '14', 5),
                                                                          ('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '16', 5),
                                                                          ('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '18', 5),
                                                                          ('dddddddd-dddd-dddd-dddd-ddddddddddd4', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Único', 10),
                                                                          ('dddddddd-dddd-dddd-dddd-ddddddddddd5', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Único', 20);