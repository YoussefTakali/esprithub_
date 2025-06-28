# EspritHub Backend - API Testing Guide

## Quick Test Commands

### 1. Start the Application
```bash
cd /home/rhaast/Desktop/projet/server
./start.sh
```

### 2. Health Check
```bash
curl http://localhost:8080/api/actuator/health
# Expected: {"status":"UP"}
```

## Authentication Flow Testing

### Step 1: Create a Test User (Admin Required)
First, you'll need to manually insert an admin user into the database:

```sql
-- Connect to PostgreSQL
docker exec -it esprithub-postgres psql -U postgres -d esprithub

-- Insert admin user (password is 'admin123' bcrypted)
INSERT INTO users (email, first_name, last_name, password, role, provider, enabled, email_verified) 
VALUES (
    'admin@esprit.tn', 
    'Admin', 
    'User', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
    'ADMIN', 
    'LOCAL', 
    true, 
    true
);
```

### Step 2: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@esprit.tn",
    "password": "admin123"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "email": "admin@esprit.tn",
      "firstName": "Admin",
      "lastName": "User",
      "role": "ADMIN",
      "hasGithubToken": false
    },
    "requiresGithubAuth": true
  }
}
```

### Step 3: Use JWT Token for Authenticated Requests
```bash
# Replace YOUR_JWT_TOKEN with the actual token from login response
export JWT_TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# Get user profile
curl -X GET http://localhost:8080/api/dashboard/profile \
  -H "Authorization: Bearer $JWT_TOKEN"

# Get dashboard stats
curl -X GET http://localhost:8080/api/dashboard/stats \
  -H "Authorization: Bearer $JWT_TOKEN"

# Create a new user
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teacher@esprit.tn",
    "firstName": "John",
    "lastName": "Teacher",
    "password": "teacher123",
    "role": "TEACHER"
  }'
```

## GitHub Integration Testing

### Step 1: Get GitHub Personal Access Token
1. Go to GitHub → Settings → Developer settings → Personal access tokens
2. Generate token with `user:email` and `read:user` scopes
3. Copy the token

### Step 2: Link GitHub Account
```bash
curl -X POST http://localhost:8080/api/github/link \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "githubToken": "your_github_token_here"
  }'
```

### Step 3: Validate GitHub Token
```bash
curl -X POST http://localhost:8080/api/github/validate-token \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "githubToken": "your_github_token_here"
  }'
```

### Step 4: Get GitHub User Info
```bash
curl -X GET "http://localhost:8080/api/github/user-info?token=your_github_token_here" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

## Role-Based Access Testing

### Test Different User Roles
```bash
# Create users with different roles
for role in CHIEF TEACHER STUDENT; do
  curl -X POST http://localhost:8080/api/users \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"email\": \"${role,,}@esprit.tn\",
      \"firstName\": \"Test\",
      \"lastName\": \"${role}\",
      \"password\": \"password123\",
      \"role\": \"$role\"
    }"
done
```

### Test Authorization
```bash
# Login as teacher
TEACHER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teacher@esprit.tn","password":"password123"}' \
  | jq -r '.data.accessToken')

# Try to access admin-only endpoint (should fail)
curl -X GET http://localhost:8080/api/dashboard/stats \
  -H "Authorization: Bearer $TEACHER_TOKEN"
# Expected: 403 Forbidden

# Access allowed endpoint (should work)
curl -X GET http://localhost:8080/api/dashboard/profile \
  -H "Authorization: Bearer $TEACHER_TOKEN"
```

## Error Testing

### Invalid Credentials
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "wrong@esprit.tn",
    "password": "wrongpassword"
  }'
# Expected: 400 Bad Request
```

### Invalid Email Domain
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@gmail.com",
    "password": "password123"
  }'
# Expected: 400 Bad Request with domain validation error
```

### Expired/Invalid JWT
```bash
curl -X GET http://localhost:8080/api/dashboard/profile \
  -H "Authorization: Bearer invalid_token"
# Expected: 401 Unauthorized
```

## Monitoring & Health

### Application Health
```bash
curl http://localhost:8080/api/actuator/health
```

### Database Connection
```bash
# Check if database is reachable
docker exec esprithub-postgres pg_isready -h localhost -p 5432
```

### Container Status
```bash
# Check running containers
docker ps | grep esprithub

# View application logs
docker logs esprithub-postgres
```

## Performance Testing

### Simple Load Test
```bash
# Install apache bench if not available
# sudo apt-get install apache2-utils

# Test login endpoint
ab -n 100 -c 10 -p login.json -T application/json \
  http://localhost:8080/api/auth/login

# Content of login.json:
echo '{"email":"admin@esprit.tn","password":"admin123"}' > login.json
```

## Troubleshooting

### Common Issues

1. **Port 5432 already in use**
   ```bash
   docker stop $(docker ps -q --filter "expose=5432")
   docker-compose up -d postgres
   ```

2. **Application won't start**
   ```bash
   # Check Java version
   java -version
   
   # Check if port 8080 is free
   lsof -i :8080
   ```

3. **Database connection issues**
   ```bash
   # Check database logs
   docker logs esprithub-postgres
   
   # Test database connection
   docker exec -it esprithub-postgres psql -U postgres -d esprithub -c "SELECT 1;"
   ```

4. **JWT token issues**
   - Ensure JWT_SECRET in .env is at least 32 characters
   - Check token expiration (default: 24 hours)
   - Verify token format: `Bearer <token>`

## Production Checklist

- [ ] Set strong JWT_SECRET (64+ characters)
- [ ] Configure GitHub OAuth app
- [ ] Set production database credentials
- [ ] Enable HTTPS
- [ ] Configure proper CORS origins
- [ ] Set up monitoring and logging
- [ ] Configure backup strategy
- [ ] Set up CI/CD pipeline
