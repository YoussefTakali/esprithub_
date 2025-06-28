# Running EspritHub Server - Complete Guide

This guide explains all the different ways to run the EspritHub server, with and without the start scripts.

## Quick Start (Recommended)

The fastest way to get started:

```bash
# Start the database
docker-compose up -d

# Run the server (automatically loads .env)
mvn spring-boot:run
```

## Why the App Now Works Without Scripts

We've implemented an **automatic .env file loader** (`EnvironmentConfig`) that Spring Boot loads during startup. This means you no longer need to use the start script to load environment variables manually.

### What Changed

1. **EnvironmentConfig Class**: Automatically loads `.env` file during Spring Boot startup
2. **spring.factories**: Registers our environment post-processor with Spring Boot
3. **Enhanced Scripts**: Improved scripts with better error handling and options

## Running Methods

### 1. Direct Maven Command (Now Works!)

```bash
# Simple run
mvn spring-boot:run

# With specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# In debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### 2. Using the Enhanced Start Script

```bash
# Basic run
./start-enhanced.sh

# Debug mode
./start-enhanced.sh --mode debug

# Production profile with clean build
./start-enhanced.sh --clean --profile prod

# Build only
./start-enhanced.sh --mode build

# See all options
./start-enhanced.sh --help
```

### 3. Using the Original Start Script

```bash
./start.sh
```

### 4. IDE Integration

You can now run the application directly from your IDE (IntelliJ IDEA, VS Code with Java extensions, etc.) without any special configuration. The `.env` file will be loaded automatically.

### 5. Running the JAR

```bash
# Build the JAR
mvn clean package -DskipTests

# Run the JAR (loads .env automatically)
java -jar target/server-0.0.1-SNAPSHOT.jar
```

## Environment Variables

### Required Variables

Make sure your `.env` file contains:

```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=esprithub
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=your_jwt_secret_here
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# GitHub OAuth (optional for basic testing)
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

### How Environment Loading Works

1. **Primary**: `.env` file in project root (automatically loaded)
2. **Fallback**: System environment variables
3. **Override**: Command line arguments (`-Dkey=value`)

## Database Setup

```bash
# Start PostgreSQL with Docker
docker-compose up -d

# Check database status
docker-compose ps

# Stop database
docker-compose down
```

## Troubleshooting

### Common Issues

1. **"Required environment variable not found"**
   - Ensure `.env` file exists and contains all required variables
   - Copy from `.env.example`: `cp .env.example .env`

2. **Database connection errors**
   - Make sure Docker is running: `docker info`
   - Start the database: `docker-compose up -d`
   - Check database logs: `docker-compose logs postgres`

3. **Port already in use**
   - Change server port in `.env`: `SERVER_PORT=8081`
   - Or kill process using port: `sudo lsof -t -i:8080 | xargs kill -9`

### Testing Environment Loading

Run the test script to verify automatic .env loading:

```bash
./test-env-loading.sh
```

## Development Tips

### Hot Reload

For development with auto-restart on file changes:

```bash
mvn spring-boot:run -Dspring-boot.run.fork=true
```

### Debug Mode

To debug the application:

```bash
# With enhanced script
./start-enhanced.sh --mode debug

# Or directly with Maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

Then connect your IDE debugger to `localhost:5005`.

### Different Profiles

```bash
# Development (default)
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Test
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

## Summary

The key improvement is that **you no longer need the start script** to run the application. The `EnvironmentConfig` class automatically loads your `.env` file, making the development experience much smoother.

Choose the method that works best for your workflow:
- **Quick testing**: `mvn spring-boot:run`
- **Development with options**: `./start-enhanced.sh`
- **IDE development**: Run directly from your IDE
- **Production**: Build JAR and run with `java -jar`
