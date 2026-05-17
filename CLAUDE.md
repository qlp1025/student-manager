# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A full-stack student management system (学生管理系统) with Vue 3 frontend and Spring Boot backend.

- **Backend**: Spring Boot 3.2.0, Java 21, MyBatis, MySQL, Spring Security, JWT
- **Frontend**: Vue 3, Vite 5, Pinia, Vue Router, Element Plus

## Commands

### Backend (Spring Boot)

```bash
cd backend
# Run the application
mvn spring-boot:run

# Build
mvn clean package

# Run tests
mvn test
```

### Frontend (Vue)

```bash
cd frontend
# Development server (http://localhost:5173)
npm run dev

# Production build
npm run build

# Preview production build
npm run preview
```

## Architecture

### Backend Structure

```
backend/src/main/java/com/student/
├── config/          # Spring configuration (SecurityConfig, CorsConfig, MybatisConfig)
├── controller/      # REST controllers
├── dto/             # Data transfer objects (LoginDTO, ApiResponse, QueryDTOs)
├── entity/          # JPA/MyBatis entities (StudentInfo, ClassInfo, Subject, ScoreInfo, etc.)
├── mapper/          # MyBatis mapper interfaces
├── security/        # JWT filter (JwtAuthenticationFilter), UserPrincipal
├── service/         # Business logic services
└── util/            # Utilities (JwtUtil, ExcelUtil)
```

**MyBatis mappers**: `backend/src/main/resources/mapper/*.xml`

**Key security flow**:
1. `JwtAuthenticationFilter` extracts JWT from `Authorization: Bearer <token>` header
2. Token validated via `JwtUtil`, user details stored in `UserPrincipal`
3. `SecurityConfig` requires authentication for `/api/**`, permits `/api/auth/**`
4. Role-based access via `@PreAuthorize("hasRole('ADMIN')")` annotations

**Database**: MySQL, configured in `application.yml`. SQL initialization script at `sql/init.sql`.

### Frontend Structure

```
frontend/src/
├── api/             # Axios API modules (index.js, student.js)
├── router/          # Vue Router with route guards
├── stores/          # Pinia stores (user.js for auth state)
└── views/           # Vue page components (Login, Home, Student, Class, etc.)
```

**Auth flow**:
- `stores/user.js` holds `token` and `roleKey` in Pinia
- `router/index.js` guards check token presence and role permissions
- API calls include JWT via axios interceptor in `api/index.js`

### API Conventions

All API responses wrap data in `ApiResponse<T>`:
```json
{ "code": 200, "msg": "success", "data": ... }
```

**Pagination**: Uses PageHelper, endpoints accept `queryDTO` with pagination params and return `PageInfo<T>`.

**Excel Import/Export**: Handled by `ExcelUtil` (Apache POI) in `StudentInfoController` and `ImportController`.

## Key Dependencies

- **JWT**: `io.jsonwebtoken:jjwt-api` - token generation/validation in `JwtUtil`
- **MyBatis**: `mybatis-spring-boot-starter:3.0.3` with XML mappers
- **Security**: Spring Security with stateless JWT sessions, BCrypt password encoding
- **UI**: Element Plus components, Vue 3 Composition API
