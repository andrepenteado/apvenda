CREATE USER "apvenda-dbuser" WITH SUPERUSER ENCRYPTED PASSWORD 'apvenda-dbpasswd';
CREATE DATABASE "apvenda-dbname" OWNER "apvenda-dbuser";

CONNECT TO apsso-dbname;

INSERT INTO sistema (data_cadastro, usuario_cadastro, identificador, nome, descricao, fk_empresa) VALUES (now(), 'Arquiteto do Sistema', 'com.github.andrepenteado.venda', 'APvenda', 'Sistema de controle de produtos e suas vendas', 1);
INSERT INTO public.oauth2_registered_client (
    id, client_name, url_acesso, uri_provider, tipo, fk_sistema, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_authentication_methods,
    authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings)
VALUES (
   gen_random_uuid(), 'Máquina Local', 'http://localhost:4200/venda/login', 'http://localhost:30000', 'LOCAL', currval('sistema_id_seq'), 'LOCAL-venda', '2026-04-30 00:00:00.000000',
   '{bcrypt}$2a$10$kX.5PsP1pU2YCCHEd0zhoexDs33MiNfPRBgCmgztsvC8BX3mdGqG6', null, 'client_secret_basic',  'refresh_token,client_credentials,authorization_code',
   'http://localhost:8080/venda-backend/authorized,http://localhost:8080/venda-backend/login/oauth2/code/com.github.andrepenteado.venda', 'http://localhost:8080/venda-backend/logout', 'openid',
   '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
   '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",900.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
);
INSERT INTO public.oauth2_registered_client (
    id, client_name, url_acesso, uri_provider, tipo, fk_sistema, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_authentication_methods,
    authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings)
VALUES (
   gen_random_uuid(), 'Servidor de Produção', 'https://sistemas.apcode.com.br/venda-backend/login', 'https://login.apcode.com.br', 'PRODUCAO', currval('sistema_id_seq'), 'PRODUCAO-venda', '2026-04-30 00:00:00.000000',
   '{bcrypt}$2a$10$WTXtrkwjjVEoi4VLWldcs.VaVR1UjMEdIv3wfzNHXwK1QxAHX4cx6', null, 'client_secret_basic',  'refresh_token,client_credentials,authorization_code',
   'https://sistemas.apcode.com.br/venda-backend/authorized,https://sistemas.apcode.com.br/venda-backend/login/oauth2/code/com.github.andrepenteado.venda', 'https://sistemas.apcode.com.br/venda-backend/oauth2/authorization/com.github.andrepenteado.venda', 'openid',
   '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
   '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",900.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
);
INSERT INTO perfil_sistema (authority, fk_sistema, descricao)
VALUES ('ROLE_com.github.andrepenteado.venda_CAIXA', currval('sistema_id_seq'), 'Operador de Caixa');

INSERT INTO authorities (username, authority) VALUES ('arquiteto', 'ROLE_com.github.andrepenteado.venda_CAIXA');
