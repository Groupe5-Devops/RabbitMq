#!/bin/bash

echo "Starting cleanup process..."

# Delete all deployments
echo "Deleting deployments..."
kubectl delete deployment order-service
kubectl delete deployment postgres
kubectl delete deployment rabbitmq

# Delete all services
echo "Deleting services..."
kubectl delete service order-service
kubectl delete service postgres
kubectl delete service rabbitmq

# Delete ConfigMap
echo "Deleting ConfigMap..."
kubectl delete configmap app-config

# Delete PVC
echo "Deleting PersistentVolumeClaim..."
kubectl delete pvc postgres-pvc

echo "Cleanup complete!"
