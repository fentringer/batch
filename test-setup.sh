#!/bin/bash

# Test Script - Validates Docker setup before running start.sh

echo "🧪 Docker Setup Validation"
echo "=========================================="
echo ""

ERRORS=0

# Test 1: Docker installed
echo -n "1. Docker installed... "
if command -v docker >/dev/null 2>&1; then
    echo "✅"
else
    echo "❌ FAILED"
    ERRORS=$((ERRORS + 1))
fi

# Test 2: Docker running
echo -n "2. Docker daemon running... "
if docker info >/dev/null 2>&1; then
    echo "✅"
else
    echo "❌ FAILED"
    ERRORS=$((ERRORS + 1))
fi

# Test 3: Docker Compose installed
echo -n "3. Docker Compose installed... "
if command -v docker-compose >/dev/null 2>&1; then
    echo "✅"
else
    echo "❌ FAILED"
    ERRORS=$((ERRORS + 1))
fi

# Test 4: docker-compose.yml valid
echo -n "4. docker-compose.yml valid... "
cd "/home/fernando/Desktop/New Folder/ENTREVISTA/demo"
if docker-compose config --quiet 2>/dev/null; then
    echo "✅"
else
    echo "❌ FAILED"
    ERRORS=$((ERRORS + 1))
fi

# Test 5: Dockerfiles exist
echo -n "5. Backend Dockerfile exists... "
if [ -f "Dockerfile" ]; then
    echo "✅"
else
    echo "❌ FAILED"
    ERRORS=$((ERRORS + 1))
fi

echo -n "6. Frontend Dockerfile exists... "
if [ -f "frontend/Dockerfile" ]; then
    echo "✅"
else
    echo "❌ FAILED"
    ERRORS=$((ERRORS + 1))
fi

# Test 7: Nginx config exists
echo -n "7. Nginx config exists... "
if [ -f "frontend/nginx.conf" ]; then
    echo "✅"
else
    echo "❌ FAILED"
    ERRORS=$((ERRORS + 1))
fi

# Test 8: Ports available
echo -n "8. Port 3000 available... "
if ! lsof -i :3000 >/dev/null 2>&1; then
    echo "✅"
else
    echo "⚠️  IN USE"
fi

echo -n "9. Port 8080 available... "
if ! lsof -i :8080 >/dev/null 2>&1; then
    echo "✅"
else
    echo "⚠️  IN USE"
fi

echo -n "10. Port 1521 available... "
if ! lsof -i :1521 >/dev/null 2>&1; then
    echo "✅"
else
    echo "⚠️  IN USE"
fi

echo ""
echo "=========================================="
if [ $ERRORS -eq 0 ]; then
    echo "✅ ALL TESTS PASSED!"
    echo ""
    echo "You can now run: ./start.sh"
else
    echo "❌ $ERRORS TEST(S) FAILED"
    echo ""
    echo "Please fix the issues above before running start.sh"
fi
echo "=========================================="
echo ""

