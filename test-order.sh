#!/bin/bash

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD-001",
    "orderItems": [
      {
        "product": {
          "name": "iPhone 13",
          "description": "Latest iPhone model",
          "price": 999.99,
          "quantity": 1
        },
        "quantity": 1,
        "price": 999.99
      }
    ],
    "totalAmount": 999.99,
    "payment": {
      "paymentMethod": "CREDIT_CARD",
      "transactionId": "TXN-001",
      "amount": 999.99,
      "status": "PENDING"
    }
}'
