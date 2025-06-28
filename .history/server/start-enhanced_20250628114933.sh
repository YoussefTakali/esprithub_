#!/bin/bash

# Enhanced startup script for EspritHub Server
# This script handles .env file loading and provides multiple run options

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== EspritHub Server Startup ===${NC}"

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if .env file exists
if [ ! -f .env ]; then
    print_error ".env file not found!"
    print_info "Please copy .env.example to .env and configure your environment variables:"
    print_info "cp .env.example .env"
    exit 1
fi

# Load environment variables from .env file
print_info "Loading environment variables from .env file..."
set -a  # automatically export all variables
source .env
set +a  # stop automatically exporting

print_success "Environment variables loaded successfully"

# Validate required environment variables
required_vars=("JWT_SECRET" "DB_HOST" "DB_PORT" "DB_NAME" "DB_USERNAME" "DB_PASSWORD")
missing_vars=()

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        missing_vars+=("$var")
    fi
done

if [ ${#missing_vars[@]} -ne 0 ]; then
    print_error "Missing required environment variables:"
    for var in "${missing_vars[@]}"; do
        echo "  - $var"
    done
    exit 1
fi

# Check if Docker is running (for database)
if ! docker info > /dev/null 2>&1; then
    print_warning "Docker is not running. Please start Docker for the PostgreSQL database."
    print_info "You can start the database with: docker-compose up -d"
fi

# Parse command line arguments
MODE="run"
PROFILE="dev"
CLEAN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --mode)
            MODE="$2"
            shift 2
            ;;
        --profile)
            PROFILE="$2"
            shift 2
            ;;
        --clean)
            CLEAN=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --mode <run|debug|build>     Run mode (default: run)"
            echo "  --profile <dev|prod|test>    Spring profile (default: dev)"
            echo "  --clean                      Clean build before running"
            echo "  --help, -h                   Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                           # Run with default settings"
            echo "  $0 --mode debug              # Run in debug mode"
            echo "  $0 --clean --profile prod    # Clean build and run with prod profile"
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Set Spring profile
export SPRING_PROFILES_ACTIVE="$PROFILE"
print_info "Using Spring profile: $PROFILE"

# Clean build if requested
if [ "$CLEAN" = true ]; then
    print_info "Cleaning previous build..."
    mvn clean
fi

# Execute based on mode
case $MODE in
    "run")
        print_info "Starting EspritHub Server..."
        mvn spring-boot:run
        ;;
    "debug")
        print_info "Starting EspritHub Server in debug mode..."
        mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
        ;;
    "build")
        print_info "Building EspritHub Server..."
        mvn clean package -DskipTests
        print_success "Build completed successfully!"
        print_info "You can run the JAR with: java -jar target/server-0.0.1-SNAPSHOT.jar"
        ;;
    *)
        print_error "Unknown mode: $MODE"
        print_info "Available modes: run, debug, build"
        exit 1
        ;;
esac
