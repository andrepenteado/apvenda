-- Migração: MAT_006.DBF (grupos do Clipper) -> categoria
-- Origem : /home/andrepenteado/Projetos/apcode/apvenda/estoque/MAT_006.DBF
-- Gerado por: .migration/categoria/migrar_categoria.py
-- Registros: 160
-- Idempotente: pode ser reaplicado (UPSERT por id).

BEGIN;

INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (1, 'Abraçadeiras para tele', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (3, 'Abraçadeiras tipo D (3)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (4, 'Abraçadeiras tipo D (4)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (5, 'Abraçadeiras rosc/fi', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (6, 'Abraçadeiras para fl', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (7, 'Abraçadeiras plastic', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (8, 'Acoplamento para disju', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (11, 'Arruelas liza zincad', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (12, 'Arruelas duralumínio', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (13, 'Braço reto/curvo galvanizado', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (14, 'Braquete', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (15, 'Barra rosqueada', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (16, 'Bucha duralumínio para', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (17, 'Bucha plástica', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (18, 'Cabecote mufla duralumínio', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (19, 'Caixa de passagem', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (21, 'Caixa padrão cpfl', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (22, 'Chave margirus', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (23, 'Cola', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (9, 'Arames galvanizado', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (10, 'Armação press bow nu', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (20, 'Caixinha de luz', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (26, 'Conjunto inter/campainha', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (27, 'Conjunto interruptor (27)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (28, 'Conjunto inter/pera', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (34, 'Tomadas diversas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (36, 'Conjunto sistema X diver', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (37, 'Cordão para ferro', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (38, 'Conexão pvc - curvas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (39, 'Conexão galvanizada', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (40, 'Disjuntores unipolar', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (41, 'Disjuntores bipolar', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (42, 'Disjuntores tripolar', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (43, 'Cabo telefone', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (44, 'Cabo flexivel/rigido', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (45, 'Fio para antena pire', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (46, 'Fio solido lousano', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (47, 'Cabo p.p flexível', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (48, 'Cabo coaxial', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (49, 'Cabo cordão paralelo', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (50, 'Fitas isolante/crep/', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (51, 'Fusiveis', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (52, 'Grampo', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (53, 'Grafite em po', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (54, 'Globo vidro bolinha', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (55, 'Haste terra', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (56, 'Interfone', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (57, 'Isolador porcelana', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (58, 'Lâmpadas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (59, 'Lubrificante /rolos/tricha', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (60, 'Luminarias', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (61, 'Parafusos', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (62, 'Fita passa fio', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (63, 'Pedra de afiar', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (64, 'Plafonier', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (65, 'Pendente mecanico', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (66, 'Pilhas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (67, 'Pe de galinha para C', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (68, 'Pinos/adaptadores', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (69, 'Protetor de tomada', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (70, 'Pistola de silicone', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (71, 'Quadro para disjunto', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (72, 'Refletores', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (73, 'Soquete baquelite/po', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (74, 'Relé foto celula', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (75, 'Resistencia para chuvei', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (76, 'Roldana plástica', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (77, 'Roseta de madeira', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (78, 'Reatores convenciona', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (79, 'Reatores eletronico', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (80, 'Retor ho eletronico', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (81, 'Reator partida diret', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (82, 'Soquete para lâmpada (82)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (83, 'Soquete para lâmpada (83)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (84, 'Solda', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (85, 'Starter', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (86, 'Spot', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (87, 'Suporte para disjunt', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (88, 'Suporte para telefon', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (89, 'Tampa mufla para pos', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (90, 'Terminal de pressão', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (91, 'Terminal cofix com P', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (92, 'Teste fenda E rabich', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (93, 'Tomada em barra', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (94, 'Eletroduto flexível', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (95, 'Eletroduto polietile', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (96, 'Eletroduto pvc - rig', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (97, 'Eletroduto calvaniza', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (2, 'Abraçadeiras simples', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (25, 'Conector em geral', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (98, 'Duchas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (99, 'Porta starter com rabi', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (100, 'Serra para arco', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (101, 'Abraçadeira hidra 3/', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (24, 'Antenas E acessorios', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (102, 'Conjunto interruptor (102)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (29, 'Cabos rca', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (103, 'Massa para calafetar', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (104, 'Chave seccionadora', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (105, 'Hidráulica em geral (105)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (106, 'Porcas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (107, 'Placas diversas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (108, 'Poste padrão cpfl', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (109, 'Terminal A compressa', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (110, 'Tomada externa 2p+te', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (111, 'Timer analogico 127v', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (112, 'Corentes/cabo aço para', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (113, 'Pendente mecânico fluo', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (114, 'Campainha cigarra fame/perl', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (115, 'Conjunto interruptor (115)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (116, 'Suporte para tv-video/', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (117, 'Cadeados', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (118, 'Cantoneira', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (119, 'Pontalete', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (120, 'Ascessorios para fogão', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (121, 'Dimer bivolt', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (122, 'Telefone sem chave', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (123, 'Rebite de repuxo', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (124, 'Acessorios para som', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (125, 'Lanterna', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (126, 'Baterias/fontes', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (127, 'Escadas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (128, 'Arandelas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (129, 'Transformadores', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (130, 'Trilho para cortina E A', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (131, 'Fontes', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (132, 'Ventiladores', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (30, 'Maquinas diversas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (31, 'Sirene', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (133, 'Mangueira para agua E A', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (134, 'Hidráulica em geral (134)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (135, 'Cabo chupeta para bate', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (136, 'Conjunto interruptor (136)', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (137, 'Controle remoto tv/v', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (138, 'Caixa para carta', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (139, 'Mascara para po', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (140, 'Etiquetas adesivas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (141, 'Brocas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (142, 'Multimetro digital', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (143, 'Clips para cabo de aço', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (144, 'Alta tensão', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (145, 'Capacitor', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (146, 'Acessorios tanquinho', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (147, 'Eletrocalhas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (148, 'Vaselina', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (149, 'Pregos', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (150, 'Linha pratis', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (151, 'Luminária de mesa', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (152, 'Disco corte/desbaste', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (153, 'Conduletes', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (154, 'Dobradicas/targetas', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (155, 'Luminária emergencia', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (156, 'Minuteira', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (157, 'Repelentes eletronic', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (158, 'Pes pvc diversos', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (159, 'Terminal imformatica', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (160, 'Contatores', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (161, 'Cabo especiais', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (162, 'Cabos aluminio', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;
INSERT INTO categoria (id, nome, ativo, fk_categoria_pai, criado_por, criado_em)
VALUES (163, 'Cerca eleltrica', true, NULL, 'Migração', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, alterado_por = 'Migração', alterado_em = CURRENT_TIMESTAMP;

-- Reposiciona a sequence de id após o maior código, para não colidir
-- com os próximos inserts feitos pela aplicação.
SELECT setval(pg_get_serial_sequence('categoria', 'id'),
              COALESCE((SELECT MAX(id) FROM categoria), 1));

COMMIT;
