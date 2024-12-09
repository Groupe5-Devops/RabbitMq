@echo off
echo Starting cleanup process...

REM Delete all deployments
echo Deleting deployments...
kubectl delete deployment order-service
kubectl delete deployment postgres
kubectl delete deployment rabbitmq

REM Delete all services
echo Deleting services...
kubectl delete service order-service
kubectl delete service postgres
kubectl delete service rabbitmq

REM Delete ConfigMap
echo Deleting ConfigMap...
kubectl delete configmap app-config

REM Delete PVC
echo Deleting PersistentVolumeClaim...
kubectl delete pvc postgres-pvc

echo Cleanup complete!
