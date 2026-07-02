param(
    [ValidateSet(
            "help",
            "build-all","build-frontend","build-backend","build-images",
            "test-frontend",
            "docker-login","docker-logout",
            "k8s-pre-init","k8s-deploy","k8s-delete",
            "k8s-log-backend","k8s-log-frontend",
            "k8s-get"
    )]
    [string]$exec = "help"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# =====================
# VARIÁVEIS
# =====================
$Registry   = "ghcr.io"
$Namespace  = "andrepenteado/apvenda"
$ModuleName = "venda"
$Ambiente   = "production"
$K8sNS      = "apvenda"

$Versao = ([xml](Get-Content backend/pom.xml)).project.version

# =====================
# FUNÇÕES AUXILIARES
# =====================
function Assert-LastExit {
    param([string]$Command)

    if ($LASTEXITCODE -ne 0) {
        throw "❌ Falha ao executar: $Command (exit code $LASTEXITCODE)"
    }
}

# =====================
# DOCKER
# =====================
function Docker-Login {
    Write-Host "🔐 Login no GitHub Container Registry" -ForegroundColor Cyan
    Write-Output $env:GITHUB_TOKEN | docker login $Registry --username andrepenteado --password-stdin
    Assert-LastExit "docker login"
}

function Docker-Logout {
    Write-Host "🔓 Logout do GHCR" -ForegroundColor DarkGray
    docker logout $Registry
    Assert-LastExit "docker logout"
}

# =====================
# BUILD
# =====================
function Test-Frontend {
    Write-Host "🧪 Rodando testes do frontend" -ForegroundColor Cyan

    Push-Location "frontend"

    npm ci
    Assert-LastExit "npm ci"

    npx vitest run
    Assert-LastExit "vitest run"

    Pop-Location

    Write-Host "✅ Testes do frontend aprovados" -ForegroundColor Green
}

function Build-Frontend {
    Write-Host "🎨 Build do frontend Angular ($Ambiente)" -ForegroundColor Cyan

    Test-Frontend

    Push-Location "frontend"

    ng build --configuration=$Ambiente --output-path="dist/$Ambiente"
    Assert-LastExit "ng build"

    Pop-Location

    Write-Host "✅ Frontend buildado com sucesso" -ForegroundColor Green
}

function Build-Backend {
    Write-Host "☕ Build do backend Java (Maven)" -ForegroundColor Cyan

    mvn -U -f backend/pom.xml clean package
    Assert-LastExit "mvn build"

    Write-Host "✅ Backend buildado com sucesso" -ForegroundColor Green
}

function Build-Images {
    Write-Host "🐳 Build e push das imagens Docker (versão $Versao)" -ForegroundColor Cyan

    Write-Host "📦 Backend"
    docker buildx build `
        -f "./backend/Dockerfile" `
        --build-arg MODULE_NAME=$ModuleName `
        -t "$Registry/$Namespace/backend" `
        -t "$Registry/$Namespace/backend:$Versao" `
        --push .
    Assert-LastExit "docker build backend"

    Write-Host "📦 Frontend (produção)"
    docker buildx build `
        -f "./frontend/Dockerfile" `
        --build-arg MODULE_NAME=$ModuleName `
        --build-arg ENV_NAME=$Ambiente `
        -t "$Registry/$Namespace/frontend" `
        -t "$Registry/$Namespace/frontend:$Versao" `
        --push .
    Assert-LastExit "docker build frontend prod"

    Write-Host "🚀 Imagens publicadas com sucesso" -ForegroundColor Green
}

# =====================
# KUBERNETES
# =====================
function K8s-Pre-Init {
    kubectl apply -f .helm/namespace.yaml
    if (-not (Test-Path .helm/secret.yaml)) {
        throw "❌ .helm/secret.yaml não encontrado. Copie de .helm/secret.template.yaml e preencha os valores reais."
    }
    kubectl apply -f .helm/secret.yaml
    Write-Host "✅ Namespace e Secrets criados" -ForegroundColor Green
}

function K8s-Deploy {
    Write-Host "🚀 Iniciando deploy no Kubernetes (namespace: $K8sNS)" -ForegroundColor Cyan
    Write-Host "Entre com a senha do gitlab.com para baixar o helm chart" -ForegroundColor Blue
    $Chart        = "oci://registry.gitlab.com/andrepenteado/apdevops/springboot-angular-chart"
    $ChartVersion = "1.4.0"
    helm registry login registry.gitlab.com -u andrepenteado
    helm upgrade --install backend $Chart --version $ChartVersion -f .helm/values.backend.yaml --set app.image.tag=$Versao -n apvenda
    helm upgrade --install frontend $Chart --version $ChartVersion -f .helm/values.frontend.yaml --set app.image.tag=$Versao -n apvenda
    Write-Host "✅ Deploy do APvenda finalizado com sucesso" -ForegroundColor Green
}

function K8s-Delete {
    Write-Host "🧹 Removendo namespace $K8sNS"
    kubectl delete namespace $K8sNS
    Assert-LastExit "kubectl delete namespace"
}

function K8s-Logs($service) {
    kubectl logs -n $K8sNS service/$service -f
}

# =====================
# DISPATCH
# =====================
switch ($exec) {

    "help" {
        Write-Host ""
        Write-Host "🛠️  Makefile.ps1 — APvenda"
        Write-Host ""
        Write-Host "🔐 Registry:"
        Write-Host "   docker-login         → Login no GHCR"
        Write-Host "   docker-logout        → Logout do registry"
        Write-Host ""
        Write-Host "🧪 Testes:"
        Write-Host "   test-frontend        → Roda testes unitários do frontend"
        Write-Host ""
        Write-Host "🚀 Build:"
        Write-Host "   build-frontend       → Testa + Build do frontend Angular"
        Write-Host "   build-backend        → Build do backend Java"
        Write-Host "   build-images         → Build & push das imagens Docker"
        Write-Host "   build-all            → Build completo"
        Write-Host ""
        Write-Host "☸️ Kubernetes:"
        Write-Host "   k8s-pre-init         → Cria namespace e secrets"
        Write-Host "   k8s-deploy           → Deploy com validação de LOGIN"
        Write-Host "   k8s-log-backend      → Logs do backend"
        Write-Host "   k8s-log-frontend     → Logs do frontend"
        Write-Host "   k8s-delete           → Remove namespace"
        Write-Host "   k8s-get              → Lista recursos"
        Write-Host ""
    }

    "docker-login"   { Docker-Login }
    "docker-logout"  { Docker-Logout }

    "test-frontend"  { Test-Frontend }
    "build-frontend" { Build-Frontend }
    "build-backend"  { Build-Backend }
    "build-images"   { Build-Images }

    "build-all" {
        Docker-Login
        Build-Frontend
        Build-Backend
        Build-Images
        Docker-Logout
        Write-Host ""
        Write-Host "🎉 Build completo finalizado com sucesso!" -ForegroundColor Green
    }

    "k8s-pre-init"   { K8s-Pre-Init }
    "k8s-deploy"     { K8s-Deploy }
    "k8s-delete"     { K8s-Delete }

    "k8s-log-backend"  { Write-Host "📄 Logs BACKEND";  K8s-Logs "apvenda-backend" }
    "k8s-log-frontend" { Write-Host "📄 Logs FRONTEND"; K8s-Logs "apvenda-frontend" }

    "k8s-get" {
        Write-Host "🔍 Recursos do namespace $K8sNS"
        kubectl get all -n $K8sNS
    }
}
