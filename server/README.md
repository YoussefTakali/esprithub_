# EspritHub Backend

A secure Spring Boot backend for an education platform with GitHub integration and role-based authentication.

## Features

- 🔐 **Multi-level Authentication**: Esprit email + GitHub token validation
- 👥 **Role-based Access Control**: Admin, Chief, Teacher, Student roles
- 🔗 **GitHub Integration**: Link and validate GitHub accounts
- 📊 **Dashboard Analytics**: Role-based statistics and metrics
- 🛡️ **Security**: JWT tokens, password encryption, CORS protection
- 🐘 **PostgreSQL Database**: Robust data persistence with Docker

## Architecture

```
src/main/java/tn/esprithub/server/
├── auth/           # Authentication & JWT services
├── user/           # User management (CRUD, roles)
├── github/         # GitHub API integration
├── dashboard/      # Analytics & statistics
├── security/       # Security configuration & filters
├── common/         # Shared DTOs, enums, exceptions
└── utils/          # Mappers & helper utilities
```

## Quick Start

### 1. Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven (included via wrapper)

### 2. Database Setup
```bash
# Start PostgreSQL container
docker-compose up -d postgres

# Verify database is running
docker ps | grep esprithub-postgres
```

### 3. Configuration
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your GitHub OAuth credentials
# GITHUB_CLIENT_ID=your_github_client_id
# GITHUB_CLIENT_SECRET=your_github_client_secret
```

### 4. Run Application
```bash
# Build and run
./mvnw spring-boot:run

# Or build JAR and run
./mvnw clean package
java -jar target/server-0.0.1-SNAPSHOT.jar
```

### 5. Verify Setup
```bash
# Health check
curl http://localhost:8080/api/actuator/health

# Should return: {"status":"UP"}
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login with Esprit email/password
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - Logout

### User Management
- `GET /api/users` - List users (Admin/Chief only)
- `POST /api/users` - Create user (Admin only)
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

### GitHub Integration
- `POST /api/github/link` - Link GitHub account
- `POST /api/github/validate-token` - Validate GitHub token
- `GET /api/github/user-info` - Get GitHub user info

### Dashboard
- `GET /api/dashboard/stats` - System statistics (Admin/Chief)
- `GET /api/dashboard/profile` - User profile
- `GET /api/dashboard/welcome` - Welcome message

## Authentication Flow

1. **Initial Login**: User logs in with `email@esprit.tn` and password
2. **GitHub Check**: System checks if user has valid GitHub token
3. **Dashboard Redirect**: 
   - ✅ Has GitHub token → Redirect to role-based dashboard
   - ❌ No GitHub token → Prompt for GitHub authentication

## User Roles & Permissions

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full system access, user management, all statistics |
| **CHIEF** | Department management, teacher oversight, department stats |
| **TEACHER** | Course management, student grades, class statistics |
| **STUDENT** | Course access, assignments, personal progress |

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | esprithub |
| `JWT_SECRET` | JWT signing secret | *required* |
| `GITHUB_CLIENT_ID` | GitHub OAuth Client ID | *required* |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth Secret | *required* |

## Security Features

- **JWT Authentication**: Stateless token-based auth
- **Password Encryption**: BCrypt hashing
- **Role-based Authorization**: Method-level security
- **CORS Protection**: Configurable origins
- **Input Validation**: Request DTO validation
- **Exception Handling**: Global error handling

## Database Schema

### Users Table
```sql
users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR UNIQUE NOT NULL,
  first_name VARCHAR NOT NULL,
  last_name VARCHAR NOT NULL,
  password VARCHAR,
  role VARCHAR NOT NULL,
  provider VARCHAR NOT NULL,
  provider_id VARCHAR,
  github_token VARCHAR,
  github_username VARCHAR,
  profile_picture VARCHAR,
  enabled BOOLEAN DEFAULT true,
  email_verified BOOLEAN DEFAULT false,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)
```

## Development Tools

### Database Access
- **pgAdmin**: http://localhost:5050
  - Email: admin@esprithub.tn
  - Password: admin

### API Testing
```bash
# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@esprit.tn","password":"admin123"}'

# Test with JWT token
curl -X GET http://localhost:8080/api/dashboard/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Troubleshooting

### Common Issues

1. **Port 5432 already in use**
   ```bash
   docker stop $(docker ps -q --filter "expose=5432")
   docker-compose up -d postgres
   ```

2. **JWT Secret Error**
   - Ensure JWT_SECRET in .env is at least 32 characters
   - Use a secure random string for production

3. **GitHub API Rate Limits**
   - Implement token rotation
   - Cache GitHub responses
   - Use GitHub App instead of OAuth tokens

## Production Deployment

### Docker Production Build
```bash
# Build production image
docker build -t esprithub-backend .

# Run with production profile
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  --env-file .env.prod \
  esprithub-backend
```

### Security Checklist
- [ ] Use strong JWT secrets (64+ characters)
- [ ] Enable HTTPS in production
- [ ] Configure proper CORS origins
- [ ] Set up database connection pooling
- [ ] Enable request rate limiting
- [ ] Configure proper logging levels

## Next Steps

1. **Frontend Integration**: Angular client development
2. **Advanced Features**: File uploads, notifications, webhooks
3. **Monitoring**: Metrics, health checks, logging
4. **Testing**: Unit tests, integration tests
5. **Documentation**: OpenAPI/Swagger integration

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
