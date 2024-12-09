@echo off
echo Starting deployment process...

REM Check if minikube is running
minikube status | findstr "Running" > nul
if errorlevel 1 (
    echo Starting Minikube...
    minikube start
)

REM Set docker env to minikube's docker daemon
echo Configuring Docker environment...
@FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env --shell cmd') DO @%%i

REM Build the application image
echo Building application Docker image...
docker build -t order-service:latest ..

REM Apply Kubernetes configurations
echo Applying Kubernetes configurations...

echo Creating ConfigMap...
kubectl apply -f configmap.yaml

echo Deploying PostgreSQL...
kubectl apply -f postgres.yaml

echo Waiting for PostgreSQL to be ready (this may take a few minutes)...
:postgres_wait_loop
kubectl get pods -l app=postgres -o jsonpath="{.items[0].status.phase}" | findstr "Running" > nul
if errorlevel 1 (
    kubectl get pods
    echo Checking PostgreSQL pod status...
    for /f "tokens=1" %%p in ('kubectl get pods -l app^=postgres -o name') do kubectl describe pod %%p
    timeout /t 10 /nobreak > nul
    goto postgres_wait_loop
)

echo PostgreSQL is running. Waiting for service to be fully ready...
timeout /t 10 /nobreak > nul

echo Deploying RabbitMQ...
kubectl apply -f rabbitmq.yaml

echo Waiting for RabbitMQ to be ready...
:rabbitmq_wait_loop
kubectl get pods -l app=rabbitmq -o jsonpath="{.items[0].status.phase}" | findstr "Running" > nul
if errorlevel 1 (
    kubectl get pods
    timeout /t 10 /nobreak > nul
    goto rabbitmq_wait_loop
)

echo RabbitMQ is running. Waiting for service to be fully ready...
timeout /t 10 /nobreak > nul

echo Deploying Order Service...
kubectl apply -f app-deployment.yaml

echo Waiting for Order Service to be ready...
:orderservice_wait_loop
kubectl get pods -l app=order-service -o jsonpath="{.items[0].status.phase}" | findstr "Running" > nul
if errorlevel 1 (
    echo Current pod status:
    kubectl get pods
    echo.
    echo Checking Order Service pod details...
    for /f "tokens=1" %%p in ('kubectl get pods -l app^=order-service -o name') do kubectl describe pod %%p
    timeout /t 10 /nobreak > nul
    goto orderservice_wait_loop
)

echo All services are deployed!

echo Getting service URLs...
echo Order Service URL:
minikube service order-service --url

echo RabbitMQ Management Console:
minikube service rabbitmq --url | findstr "15672"

echo Deployment complete! Use 'kubectl get pods' to check the status of your pods
kubectl get pods
