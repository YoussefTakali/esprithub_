#!/bin/bash

echo "🚀 EspritHub Backend Setup Test"
echo "================================"

# Check if database is running
echo "1. Checking PostgreSQL database..."
if docker ps | grep -q esprithub-postgres; then
    echo "✅ PostgreSQL container is running"
else
    echo "❌ PostgreSQL container is not running"
    echo "   Run: docker-compose up -d postgres"
    exit 1
fi

# Check if we can connect to database
echo "2. Testing database connection..."
if docker exec esprithub-postgres pg_isready -h localhost -p 5432; then
    echo "✅ Database is ready to accept connections"
else
    echo "❌ Cannot connect to database"
    exit 1
fi

# Check if JAR was built successfully
echo "3. Checking application build..."
if [ -f "target/server-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ JAR file built successfully"
    echo "   Size: $(du -h target/server-0.0.1-SNAPSHOT.jar | cut -f1)"
else
    echo "❌ JAR file not found"
    echo "   Run: ./mvnw clean package -DskipTests"
    exit 1
fi

# Check environment file
echo "4. Checking environment configuration..."
if [ -f ".env" ]; then
    echo "✅ Environment file exists"
    if grep -q "GITHUB_CLIENT_ID=your_github_client_id" .env; then
        echo "⚠️  GitHub OAuth credentials need to be configured"
        echo "   Edit .env file with your GitHub app credentials"
    else
        echo "✅ GitHub OAuth appears to be configured"
    fi
else
    echo "❌ Environment file missing"
    echo "   Run: cp .env.example .env"
    exit 1
fi

echo ""
echo "🎉 Setup looks good! Ready to start the application."
echo ""
echo "To start the server:"
echo "  java -jar target/server-0.0.1-SNAPSHOT.jar"
echo ""
echo "Or with Maven:"
echo "  ./mvnw spring-boot:run"
echo ""
echo "Once running, test with:"
echo "  curl http://localhost:8080/api/actuator/health"
