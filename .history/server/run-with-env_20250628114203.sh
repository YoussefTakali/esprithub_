#!/bin/bash

echo "🔧 Loading Environment & Starting EspritHub"
echo "============================================"

# Function to load .env file properly (ignoring comments)
load_env() {
    if [ -f .env ]; then
        echo "📂 Loading environment variables from .env..."
        export $(grep -v '^#' .env | grep -v '^$' | xargs)
        echo "✅ Environment variables loaded"
        echo "   - JWT_SECRET: $(echo -n "$JWT_SECRET" | wc -c) characters"
        echo "   - DB_HOST: $DB_HOST"
        echo "   - SERVER_PORT: $SERVER_PORT"
    else
        echo "❌ .env file not found!"
        exit 1
    fi
}

# Check prerequisites
check_prerequisites() {
    echo ""
    echo "🔍 Checking prerequisites..."
    
    # Check if database is running
    if docker ps | grep -q esprithub-postgres; then
        echo "✅ PostgreSQL database is running"
    else
        echo "❌ PostgreSQL database is not running"
        echo "   Run: docker-compose up -d postgres"
        exit 1
    fi
    
    # Check if JAR exists
    if [ -f "target/server-0.0.1-SNAPSHOT.jar" ]; then
        echo "✅ Application JAR found"
    else
        echo "❌ Application JAR not found"
        echo "   Run: ./mvnw clean package -DskipTests"
        exit 1
    fi
}

# Main execution
main() {
    load_env
    check_prerequisites
    
    echo ""
    echo "🚀 Starting EspritHub Backend..."
    echo "   Server will start on: http://localhost:$SERVER_PORT/api"
    echo "   Press Ctrl+C to stop"
    echo ""
    
    # Start the application with loaded environment
    java -jar target/server-0.0.1-SNAPSHOT.jar
}

# Run the main function
main
