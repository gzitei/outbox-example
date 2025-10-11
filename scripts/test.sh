#!/bin/sh
curl -i localhost:8080/api/users/1 \
    -X PATCH \
    -H 'Content-Type: application/json' \
    -d '{
        "id": 1,
        "password": "p@s5w0rd"
    }'
echo
curl -i localhost:8080/api/users \
    -X POST \
    -H 'Content-Type: application/json' \
    -d '{
        "email": "newuser@test.com",
        "name": "New User",
        "password": "mybirthday"
    }'
echo
curl -i localhost:8080/api/users/2 \
    -X PATCH \
    -H 'Content-Type: application/json' \
    -d '{
        "id": 2,
        "password": "youshallnotpass"
    }'
echo
curl -i localhost:8080/api/users/3 \
    -X PATCH \
    -H 'Content-Type: application/json' \
    -d '{
        "id": 3,
        "password": "avant!pal3str4"
    }'
echo
