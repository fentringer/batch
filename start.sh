#!/bin/bash

echo "🚀 Starting Project Setup..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Start Docker Compose
echo "🐳 Starting Docker Compose..."
docker-compose up --build -d

echo ""
echo "⏳ Waiting for services to be ready..."
echo ""

# Wait for Oracle
echo "⏳ Waiting for Oracle Database (this may take 30-60 seconds)..."
until docker exec oracle-xe bash -c "echo 'SELECT 1 FROM DUAL;' | sqlplus -s system/oracle@//localhost:1521/XEPDB1" > /dev/null 2>&1; do
    printf '.'
    sleep 5
done
echo ""
echo "✅ Oracle is ready!"

# Wait for Backend
echo "⏳ Waiting for Spring Boot backend..."
until curl -s http://localhost:8080/person/all > /dev/null 2>&1; do
    printf '.'
    sleep 3
done
echo ""
echo "✅ Backend is ready!"

# Wait for Frontend
echo "⏳ Waiting for React frontend..."
until curl -s http://localhost:3000 > /dev/null 2>&1; do
    printf '.'
    sleep 2
done
echo ""
echo "✅ Frontend is ready!"

echo ""
echo "=========================================="
echo "✅ All services are running!"
echo "=========================================="
echo ""
echo "🌐 Frontend:  http://localhost:3000"
echo "🔧 Backend:   http://localhost:8080"
echo "🗄️  Oracle:    localhost:1521/XEPDB1"
echo ""
echo "📚 Test the ETL:"
echo "   curl -X POST http://localhost:8080/etl/run"
echo ""
echo "📊 View logs:"
echo "   docker-compose logs -f backend"
echo ""
echo "🛑 Stop all services:"
echo "   docker-compose down"
echo ""

