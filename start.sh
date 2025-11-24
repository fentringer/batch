#!/bin/bash

set -e  # Exit on error

echo "🚀 Starting ETL Application Setup..."
echo "=========================================="
echo ""

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Docker
if ! command_exists docker; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    echo "   Ubuntu/Linux: sudo systemctl start docker"
    echo "   macOS: Open Docker Desktop"
    exit 1
fi

echo "✅ Docker is running"

# Check Docker Compose
if ! command_exists docker-compose; then
    echo "❌ docker-compose is not installed. Please install it first."
    exit 1
fi

echo "✅ docker-compose is available"
echo ""

# Stop existing containers if any
echo "🛑 Stopping any existing containers..."
docker-compose down 2>/dev/null || true
echo ""

# Build images manually (workaround for docker-compose bug)
echo "🔨 Building Docker images..."
echo "   Building backend..."
docker build -t demo_backend . -q || { echo "❌ Backend build failed"; exit 1; }
echo "   ✅ Backend built"

echo "   Building frontend..."
cd frontend
docker build -t demo_frontend . -q || { echo "❌ Frontend build failed"; exit 1; }
cd ..
echo "   ✅ Frontend built"
echo ""

# Start all services without rebuild
echo "🚀 Starting all services..."
docker-compose up -d --no-build

if [ $? -ne 0 ]; then
    echo "❌ Failed to start Docker containers"
    echo "   Check logs with: docker-compose logs"
    exit 1
fi

echo ""
echo "✅ Containers started successfully"
echo ""

# Wait for Oracle Database
echo "⏳ Waiting for Oracle Database to initialize..."
echo "   (This can take 30-90 seconds on first run)"
ORACLE_READY=0
for i in {1..30}; do
    if docker exec oracle-xe bash -c "echo 'SELECT 1 FROM DUAL;' | sqlplus -s system/oracle@//localhost:1521/XEPDB1" 2>/dev/null | grep -q "1"; then
        ORACLE_READY=1
        break
    fi
    printf '.'
    sleep 3
done

if [ $ORACLE_READY -eq 1 ]; then
    echo ""
    echo "✅ Oracle Database is ready!"
else
    echo ""
    echo "⚠️  Oracle might still be initializing. Continuing anyway..."
fi

# Wait for Backend (Spring Boot)
echo "⏳ Waiting for Spring Boot backend..."
BACKEND_READY=0
for i in {1..40}; do
    if curl -s http://localhost:8080/person/all > /dev/null 2>&1; then
        BACKEND_READY=1
        break
    fi
    printf '.'
    sleep 3
done

if [ $BACKEND_READY -eq 1 ]; then
    echo ""
    echo "✅ Spring Boot backend is ready!"
else
    echo ""
    echo "⚠️  Backend is taking longer than expected."
    echo "   Check logs: docker-compose logs backend"
fi

# Wait for Frontend (React)
echo "⏳ Waiting for React frontend..."
FRONTEND_READY=0
for i in {1..20}; do
    if curl -s http://localhost:3000 > /dev/null 2>&1; then
        FRONTEND_READY=1
        break
    fi
    printf '.'
    sleep 2
done

if [ $FRONTEND_READY -eq 1 ]; then
    echo ""
    echo "✅ React frontend is ready!"
else
    echo ""
    echo "⚠️  Frontend is taking longer than expected."
    echo "   Check logs: docker-compose logs frontend"
fi

echo ""
echo "=========================================="
echo "✅ ALL SERVICES ARE RUNNING!"
echo "=========================================="
echo ""
echo "📍 Access Points:"
echo "   🌐 Frontend:  http://localhost:3000"
echo "   🔧 Backend:   http://localhost:8080"
echo "   🗄️  Database:  localhost:1521/XEPDB1"
echo "      Username:  system"
echo "      Password:  oracle"
echo ""
echo "📚 Quick Commands:"
echo "   • Test API:    curl http://localhost:8080/person/all"
echo "   • Run ETL:     curl -X POST http://localhost:8080/etl/run"
echo "   • View logs:   docker-compose logs -f"
echo "   • Stop all:    docker-compose down"
echo ""
echo "📊 Container Status:"
docker-compose ps
echo ""
echo "✨ Ready to use! Open http://localhost:3000 in your browser"
echo ""

