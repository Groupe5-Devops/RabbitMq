#!/bin/bash

echo "Starting deployment process..."

# Check if minikube is running
if ! minikube status | grep -q "Running"; then
    echo "Starting Minikube..."
    minikube start
fi

# Set docker env to minikube's docker daemon
echo "Configuring Docker environment..."
eval $(minikube docker-env)

# Build the application image
echo "Building application Docker image..."
docker build -t order-service:latest ..

# Apply Kubernetes configurations
echo "Applying Kubernetes configurations..."

echo "Creating ConfigMap..."
kubectl apply -f configmap.yaml

echo "Deploying PostgreSQL..."
kubectl apply -f postgres.yaml

echo "Deploying RabbitMQ..."
kubectl apply -f rabbitmq.yaml

echo "Deploying Order Service..."
kubectl apply -f app-deployment.yaml

# Wait for pods to be ready
echo "Waiting for pods to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres --timeout=120s
kubectl wait --for=condition=ready pod -l app=rabbitmq --timeout=120s
kubectl wait --for=condition=ready pod -l app=order-service --timeout=120s

# Get service URLs
echo "Getting service URLs..."
echo "Order Service URL: $(minikube service order-service --url)"
echo "RabbitMQ Management Console: $(minikube service rabbitmq --url -n default | grep 15672)"

echo "Deployment complete! Use 'kubectl get pods' to check the status of your pods"
