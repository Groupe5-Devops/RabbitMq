# RabbitMQ Order Processing System

A Spring Boot application that processes orders using RabbitMQ with retry mechanism and PostgreSQL persistence.

## Features

- Order processing with RabbitMQ
- Retry mechanism for failed messages
- Dead Letter Queue for unprocessable messages
- PostgreSQL persistence
- Docker containerization
- Kubernetes deployment

## Prerequisites

- Java 17
- Docker and Docker Compose
- Maven
- Minikube (for Kubernetes deployment)
- kubectl (for Kubernetes deployment)

## Running the Application

1. Build the application:
```bash
mvn clean package -DskipTests
```

2. Start the services:
```bash
docker-compose up -d
```

3. Test the application using the provided script:
```bash
./test-order.sh
```

## Architecture

- Main Queue: `order.queue`
- Dead Letter Queue: `order.dlq`
- Exchange: `order.exchange`
- Routing Key: `order.routing.key`

## Message Flow

1. Order received via REST API
2. Message sent to RabbitMQ queue
3. Message processed with validation
4. If processing fails:
   - Retry up to 3 times
   - Move to Dead Letter Queue if all retries fail
5. Successful messages saved to PostgreSQL

## Monitoring

- RabbitMQ Management UI: http://localhost:15672
  - Username: guest
  - Password: guest
- Application: http://localhost:8080

## API Endpoints

### Create Order
```
POST /api/orders
Content-Type: application/json

{
  "orderNumber": "string",
  "orderItems": [
    {
      "product": {
        "name": "string",
        "description": "string",
        "price": number,
        "quantity": number
      },
      "quantity": number,
      "price": number
    }
  ],
  "totalAmount": number,
  "payment": {
    "paymentMethod": "string",
    "transactionId": "string",
    "amount": number,
    "status": "string"
  }
}
```

## Kubernetes Deployment

The application can be deployed to Kubernetes using the provided configuration files in the `k8s` directory.

### Prerequisites for Kubernetes Deployment

- Minikube
- kubectl
- Docker
- Java 17
- Maven

### Kubernetes Configuration Files

The `k8s` directory contains the following configuration files:

1. `configmap.yaml`: Application configuration for database and RabbitMQ connections
2. `postgres.yaml`: PostgreSQL deployment with persistent storage
3. `rabbitmq.yaml`: RabbitMQ deployment with management console
4. `app-deployment.yaml`: Spring Boot application deployment

### Deployment Scripts

For easy deployment, we provide both Windows batch files and Unix shell scripts:

#### Windows Users
- `deploy.bat`: Deploys the entire application stack
- `cleanup.bat`: Removes all deployed resources

#### Unix/Linux Users
- `deploy.sh`: Deploys the entire application stack
- `cleanup.sh`: Removes all deployed resources

### Deployment Steps

1. Start Minikube (if not already running):
```bash
minikube start
```

2. Navigate to the k8s directory:
```bash
cd k8s
```

3. Run the deployment script:
```powershell
# For Windows PowerShell
.\deploy.bat

# For Windows Command Prompt (cmd.exe)
deploy.bat

# For Unix/Linux
./deploy.sh
```

The deployment script will:
- Start Minikube (if not running)
- Build the application Docker image
- Deploy all Kubernetes resources
- Wait for all pods to be ready
- Display service URLs

### Accessing the Services

After deployment, you can access:
1. Order Service: URL provided by deployment script
2. RabbitMQ Management Console: URL provided by deployment script (credentials: guest/guest)

### Monitoring the Deployment

Check the status of your pods:
```bash
kubectl get pods
```

View logs for a specific pod:
```bash
kubectl logs <pod-name>
```

### Cleanup

To remove all deployed resources:
```bash
# For Windows
cleanup.bat

# For Unix/Linux
./cleanup.sh
```

### Kubernetes Architecture

The deployment consists of:
- PostgreSQL StatefulSet with persistent storage
- RabbitMQ deployment with management console
- Spring Boot application deployment with:
  - Resource limits and requests
  - Health checks
  - ConfigMap for configuration
  - LoadBalancer service for external access

### Security Considerations

The current configuration is suitable for development. For production:
1. Use Secrets instead of ConfigMap for sensitive data
2. Configure proper security settings for RabbitMQ and PostgreSQL
3. Set up proper ingress rules
4. Implement network policies
5. Use proper SSL/TLS certificates
