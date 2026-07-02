.ONESHELL:
.SHELLFLAGS = -ec
.PHONY: help \
        build-all build-frontend build-backend build-images \
        test-frontend \
        docker-login docker-logout \
        k8s-pre-init k8s-deploy k8s-delete \
        k8s-log-backend k8s-log-frontend \
        k8s-get

SHELL := /bin/bash

# =====================
# VARIÁVEIS
# =====================
REGISTRY     := ghcr.io
NAMESPACE    := andrepenteado/apvenda
MODULE_NAME  := venda
ANGULAR_DIST := production
K8S_NS       := apvenda
CHART         := oci://registry.gitlab.com/andrepenteado/apdevops/springboot-angular-chart
CHART_VERSION := 1.4.0
VERSAO_APP    := $(shell sed -n 's:.*<version>\(.*\)</version>.*:\1:p' backend/pom.xml | head -n 1)

# =====================
# HELP
# =====================
help:
	@echo ""
	@echo "🛠️  Makefile APvenda — comandos disponíveis"
	@echo ""
	@echo "🔐 Registry (GHCR):"
	@echo "   docker-login         → Login no GitHub Container Registry"
	@echo "   docker-logout        → Logout do registry"
	@echo ""
	@echo "🧪 Testes:"
	@echo "   test-frontend        → Roda testes unitários do frontend"
	@echo ""
	@echo "🚀 Build:"
	@echo "   build-frontend       → Testa + Build do frontend Angular"
	@echo "   build-backend        → Build do backend Java (Maven)"
	@echo "   build-images         → Build & push das imagens Docker"
	@echo "   build-all            → Build completo (frontend + backend + imagens)"
	@echo ""
	@echo "☸️ Kubernetes:"
	@echo "   k8s-pre-init         → Cria namespace e secrets necessários para deploy"
	@echo "   k8s-deploy           → Deploy com validação prévia do LOGIN"
	@echo "   k8s-log-backend      → Logs do backend"
	@echo "   k8s-log-frontend     → Logs do frontend"
	@echo "   k8s-delete           → Remove o namespace apvenda"
	@echo "   k8s-get              → Lista recursos do namespace"
	@echo ""
	@echo "Exemplo:"
	@echo "  make build-all"
	@echo "  make k8s-deploy"
	@echo ""

# =====================
# LOGIN / LOGOUT
# =====================
docker-login:
	@if [ -n "$(GITHUB_TOKEN)" ]; then \
		echo "🔐 Login automático no GHCR"; \
		echo "$(GITHUB_TOKEN)" | docker login $(REGISTRY) --username andrepenteado --password-stdin; \
	else \
		echo "❌ GITHUB_TOKEN não definido"; \
		exit 1; \
	fi

docker-logout:
	@echo "🔓 Logout do GHCR"
	@docker logout $(REGISTRY)

# =====================
# TESTES FRONTEND
# =====================
test-frontend:
	@echo "🧪 Rodando testes do frontend"
	cd frontend
	npm ci
	npx ng test --watch=false
	cd ..
	@echo "✅ Testes do frontend aprovados"

# =====================
# BUILD FRONTEND
# =====================
build-frontend: test-frontend
	@echo "🎨 Build do frontend Angular ($(ANGULAR_DIST))"
	cd frontend
	ng build --configuration=$(ANGULAR_DIST) --output-path=dist/$(ANGULAR_DIST)
	cd ..
	@echo "✅ Frontend buildado com sucesso"

# =====================
# BUILD BACKEND
# =====================
build-backend:
	@echo "☕ Build do backend Java (Maven)"
	mvn -U clean package \
		--file backend/pom.xml \
		-DskipTests
	@echo "✅ Backend buildado com sucesso"

# =====================
# BUILD IMAGENS
# =====================
build-images:
	@echo "🐳 Build e push das imagens Docker (versão $(VERSAO_APP))"

	@echo "   📦 Backend"
	docker buildx build \
		-f ./backend/Dockerfile \
        --build-arg MODULE_NAME=$(MODULE_NAME) \
		-t $(REGISTRY)/$(NAMESPACE)/backend \
		-t $(REGISTRY)/$(NAMESPACE)/backend:$(VERSAO_APP) \
		--push .

	@echo "   📦 Frontend (produção)"
	docker buildx build \
		-f ./frontend/Dockerfile \
        --build-arg MODULE_NAME=$(MODULE_NAME) \
        --build-arg ENV_NAME=$(ANGULAR_DIST) \
		-t $(REGISTRY)/$(NAMESPACE)/frontend \
		-t $(REGISTRY)/$(NAMESPACE)/frontend:$(VERSAO_APP) \
		--push .

	@echo "🚀 Imagens publicadas com sucesso"

# =====================
# BUILD COMPLETO
# =====================
build-all: docker-login build-frontend build-backend build-images docker-logout
	@echo "🎉 Build completo finalizado!"

# ================================================
# Criar namespace e secrets (apvenda-secrets + github-secret via secret.yaml)
# ================================================
k8s-pre-init:
	@echo "🚀 Inicializando namespace e secrets"
	kubectl apply -f .helm/namespace.yaml
	@if [ ! -f .helm/secret.yaml ]; then \
		echo "❌ .helm/secret.yaml não encontrado. Copie de .helm/secret.template.yaml e preencha os valores reais."; \
		exit 1; \
	fi
	kubectl apply -f .helm/secret.yaml

# ============================================================
# 🚀 Deploy Kubernetes (com validação de LOGIN)
# ============================================================
k8s-deploy:
	@echo "🚀 Iniciando deploy do APvenda no Kubernetes"
	@echo "Entre com a senha do gitlab.com para baixar o helm chart"
	echo "$(GITLAB_TOKEN)" | helm registry login registry.gitlab.com -u andrepenteado --password-stdin
	helm upgrade --install backend $(CHART) --version $(CHART_VERSION) -f .helm/values.backend.yaml --set app.image.tag=$(VERSAO_APP) -n apvenda
	helm upgrade --install frontend $(CHART) --version $(CHART_VERSION) -f .helm/values.frontend.yaml --set app.image.tag=$(VERSAO_APP) -n apvenda
	@echo "✅ Deploy do APvenda finalizado com sucesso"

# =====================
# 🧹 Remoção completa
# =====================
k8s-delete:
	@echo "🧹 Removendo namespace $(K8S_NS)"
	kubectl delete namespace $(K8S_NS)

# =====================
# 📄 Logs
# =====================
k8s-log-backend:
	@echo "📄 Logs do BACKEND"
	kubectl logs -n $(K8S_NS) service/apvenda-backend -f

k8s-log-frontend:
	@echo "📄 Logs do FRONTEND"
	kubectl logs -n $(K8S_NS) service/apvenda-frontend -f

# =====================
# 🔍 Inspeção
# =====================
k8s-get:
	@echo "🔍 Recursos do namespace $(K8S_NS)"
	kubectl get all -n $(K8S_NS)
