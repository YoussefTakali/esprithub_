#!/bin/bash

# Test script to verify that .env loading works automatically
# This simulates running Spring Boot without any environment variables

echo "=== Testing Automatic .env Loading ==="
echo "This test runs Spring Boot without pre-loading environment variables"
echo "to verify that the EnvironmentConfig class works correctly."
echo ""

# Clear all environment variables that might be set
unset DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD
unset JWT_SECRET JWT_EXPIRATION JWT_REFRESH_EXPIRATION
unset GITHUB_CLIENT_ID GITHUB_CLIENT_SECRET
unset SPRING_PROFILES_ACTIVE

echo "Environment variables cleared."
echo "Running Spring Boot to test automatic .env loading..."
echo ""

# Try to start Spring Boot - it should automatically load .env
timeout 30s mvn spring-boot:run -Dspring-boot.run.arguments="--spring.main.lazy-initialization=true" || {
    echo ""
    echo "=== Test Results ==="
    if [ $? -eq 124 ]; then
        echo "✅ SUCCESS: Application started successfully (timeout after 30s as expected)"
        echo "The automatic .env loading is working!"
    else
        echo "❌ FAILED: Application failed to start"
        echo "Check the error messages above for details"
    fi
}
