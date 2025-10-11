#!/bin/bash

curl -i localhost:8080/api/users \
    -X POST \
    -H 'Content-Type: application/json' \
    -d '{
        "email": "user@test.com",
        "name": "Test User",
        "password": "supersecret"
    }'
echo
curl -i localhost:8080/api/users \
    -X POST \
    -H 'Content-Type: application/json' \
    -d '{
        "email": "anotheruser@test.com",
        "name": "Another Test User",
        "password": "passw0rd"
    }'
