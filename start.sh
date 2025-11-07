#!/bin/bash
echo "===================================="
echo "   Pornirea aplicatiei Angular + Spring Boot"
echo "===================================="
echo

# 1. Pornesc backend-ul (Spring Boot)
echo "[1/2] Pornesc backend-ul (Spring Boot)..."
(cd backend && ./mvnw spring-boot:run) &

# 2. Pornesc frontend-ul (Angular)
echo "[2/2] Pornesc frontend-ul (Angular)..."
(cd frontend && npm start) &

echo
echo "===================================="
echo "Aplicatia ruleaza acum pe:"
echo "   Frontend: http://localhost:4200"
echo "   Backend:  http://localhost:8080"
echo "===================================="
echo
