#!/bin/bash

echo "🎯 EspritHub Backend Quick Start"
echo "================================"

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command_exists docker; then
    echo "❌ Docker is required but not installed"
    exit 1
fi

if ! command_exists docker-compose; then
    echo "❌ Docker Compose is required but not installed"
    exit 1
fi

if ! command_exists java; then
    echo "❌ Java 17+ is required but not installed"
    exit 1
else
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo "❌ Java 17+ is required (found Java $JAVA_VERSION)"
        exit 1
    fi
    echo "✅ Java $JAVA_VERSION found"
fi

echo "✅ All prerequisites met"

# Setup environment
echo ""
echo "🔧 Setting up environment..."

if [ ! -f ".env" ]; then
    cp .env.example .env
    echo "✅ Environment file created from template"
    echo "⚠️  Please edit .env with your GitHub OAuth credentials"
else
    echo "✅ Environment file already exists"
fi

# Start database
echo ""
echo "🐘 Starting PostgreSQL database..."

if docker ps | grep -q esprithub-postgres; then
    echo "✅ PostgreSQL is already running"
else
    docker-compose up -d postgres
    echo "⏳ Waiting for database to be ready..."
    sleep 5
    
    # Wait for database to be ready
    for i in {1..30}; do
        if docker exec esprithub-postgres pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
            echo "✅ PostgreSQL is ready"
            break
        fi
        echo "⏳ Still waiting for database... ($i/30)"
        sleep 2
    done
fi

# Build application
echo ""
echo "🔨 Building application..."

if [ ! -f "target/server-0.0.1-SNAPSHOT.jar" ] || [ "pom.xml" -nt "target/server-0.0.1-SNAPSHOT.jar" ]; then
    echo "Building JAR file..."
    ./mvnw clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo "✅ Application built successfully"
    else
        echo "❌ Build failed"
        exit 1
    fi
else
    echo "✅ Application already built"
fi

# Start application
echo ""
echo "🚀 Starting EspritHub Backend..."
echo ""

# Check if application is already running
if curl -s http://localhost:8080/api/actuator/health >/dev/null 2>&1; then
    echo "⚠️  Application seems to be already running on port 8080"
    echo "   Stop it first or use a different port"
    exit 1
fi

echo "Starting server on http://localhost:8080/api"
echo "Press Ctrl+C to stop"
echo ""

# Start the application
java -jar target/server-0.0.1-SNAPSHOT.jar
