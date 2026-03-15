# CRM System

A Customer Relationship Management (CRM) system with OAuth2 Single Sign-On support.

## Technology Stack

- **Backend**: Spring Boot 3.5.11
- **Database**: MySQL 8
- **Frontend**: Vue 3 + TypeScript
- **Authentication**: OAuth2 Single Sign-On
- **Architecture**: Monolithic application (non-separated frontend and backend)

## Features

- User authentication via OAuth2 SSO
- Customer management
- Contact management
- Deal tracking
- Activity logging
- Responsive web interface
- RESTful API for AJAX operations

## Project Structure

```
CRM/
├── src/
│   ├── main/
│   │   ├── java/com/crm/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST and MVC controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Exception handlers
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── security/        # Security components
│   │   │   ├── service/         # Service layer
│   │   │   └── utils/           # Utility classes
│   │   └── resources/
│   │       ├── static/          # Static resources (Vue build output)
│   │       ├── templates/       # Thymeleaf templates
│   │       ├── application.yml  # Application configuration
│   │       ├── schema.sql       # Database schema
│   │       └── data.sql         # Sample data
│   └── test/                    # Test classes
└── frontend/                    # Vue 3 + TypeScript project
    ├── src/
    │   ├── components/          # Vue components
    │   ├── views/               # Vue views/pages
    │   ├── router/              # Vue Router configuration
    │   ├── assets/              # Static assets
    │   └── main.ts              # Application entry point
    ├── package.json
    ├── vite.config.ts
    └── tsconfig.json
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 20.x
- MySQL 8.0+
- OAuth2 authorization server (separate service)

## Installation

### 1. Clone the repository

```bash
git clone <repository-url>
cd CRM
```

### 2. Database Setup

Create a MySQL database and user:

```sql
CREATE DATABASE crm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'crm_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON crm_db.* TO 'crm_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Application

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/crm_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

  security:
    oauth2:
      client:
        registration:
          oauth2-server:
            client-id: your-client-id
            client-secret: your-client-secret
            redirect-uri: http://localhost:8080/login/oauth2/code/oauth2-server
        provider:
          oauth2-server:
            authorization-uri: http://localhost:8081/oauth/authorize
            token-uri: http://localhost:8081/oauth/token
            user-info-uri: http://localhost:8081/userinfo
```

### 4. Build and Run

#### Option 1: Full Build (includes frontend)

```bash
# Build the entire project (backend + frontend)
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Option 2: Development Mode

```bash
# Terminal 1: Run Spring Boot
mvn spring-boot:run

# Terminal 2: Run Vue development server
cd frontend
npm install
npm run dev
```

### 5. Access the Application

- Application URL: http://localhost:8080
- Login page: http://localhost:8080/login
- Dashboard: http://localhost:8080/dashboard
- API documentation: http://localhost:8080/api

## Usage

### Authentication

1. Click "Sign in with OAuth2" on the login page
2. You will be redirected to the OAuth2 authorization server
3. Approve the authorization request
4. You will be redirected back to the CRM dashboard

### Customer Management

- View customer list: `/customers`
- Add new customer: `/customers/new`
- Edit customer: `/customers/edit/{id}`
- Delete customer: `/customers/delete/{id}`
- View customer details: `/customers/{id}`

### Contact Management

- View contact list: `/contacts`
- Add new contact: `/contacts/new`
- Edit contact: `/contacts/edit/{id}`
- Delete contact: `/contacts/delete/{id}`
- View contact details: `/contacts/{id}`

### API Endpoints

#### Customers

- `GET /api/customers` - List customers (paginated)
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer
- `GET /api/stats/summary` - Get summary statistics

## Database Schema

### Users
- `id` - Primary key
- `username` - Unique username
- `email` - Unique email
- `full_name` - Full name
- `role` - User role (USER/ADMIN)
- `provider` - OAuth2 provider
- `provider_id` - OAuth2 provider ID

### Customers
- `id` - Primary key
- `name` - Customer name
- `company_name` - Company name
- `email` - Contact email
- `phone` - Contact phone
- `address` - Physical address
- `industry` - Industry sector
- `status` - Active/Inactive
- `revenue` - Total revenue
- `assigned_user_id` - Assigned user ID

### Contacts
- `id` - Primary key
- `first_name` - First name
- `last_name` - Last name
- `email` - Contact email
- `phone` - Contact phone
- `mobile` - Mobile phone
- `position` - Job position
- `department` - Department
- `customer_id` - Foreign key to customers
- `is_primary` - Primary contact flag

### Deals
- `id` - Primary key
- `title` - Deal title
- `description` - Deal description
- `amount` - Deal amount
- `currency` - Currency code
- `customer_id` - Foreign key to customers
- `stage` - Deal stage (PROSPECT/QUALIFICATION/PROPOSAL/NEGOTIATION/CLOSED)
- `probability` - Success probability (0-100)
- `expected_close_date` - Expected close date
- `actual_close_date` - Actual close date
- `status` - Deal status (OPEN/CLOSED)
- `source` - Lead source
- `priority` - Priority level

### Activities
- `id` - Primary key
- `type` - Activity type (MEETING/CALL/EMAIL/NOTE)
- `title` - Activity title
- `description` - Activity description
- `customer_id` - Foreign key to customers
- `contact_id` - Foreign key to contacts
- `deal_id` - Foreign key to deals
- `activity_date` - Activity date/time
- `duration` - Duration in minutes
- `status` - Activity status
- `location` - Activity location
- `user_id` - User who created the activity
- `outcome` - Activity outcome

## Configuration

### Spring Boot Configuration

Main configuration is in `src/main/resources/application.yml`:

- Server port
- Database connection
- JPA/Hibernate settings
- OAuth2 client configuration
- Thymeleaf settings
- Logging levels

### Vue Configuration

Vue configuration is in `frontend/vite.config.ts`:

- Build output directory (configured for Spring Boot integration)
- Development server proxy settings
- Path aliases

## Development

### Backend Development

- Add new entities in `src/main/java/com/crm/entity/`
- Add repositories in `src/main/java/com/crm/repository/`
- Add controllers in `src/main/java/com/crm/controller/`
- Add services in `src/main/java/com/crm/service/`

### Frontend Development

- Add components in `frontend/src/components/`
- Add views in `frontend/src/views/`
- Update router in `frontend/src/router/index.ts`
- Add API calls in `frontend/src/api/`

### Building for Production

```bash
# Build the entire project
mvn clean package

# The generated JAR will be in target/
# Run with: java -jar target/crm-system-1.0.0.jar
```

## OAuth2 Integration

This system integrates with an external OAuth2 authorization server. To configure:

1. Register your application with the OAuth2 provider
2. Obtain client ID and client secret
3. Configure the OAuth2 endpoints in `application.yml`
4. Set up the redirect URI in your OAuth2 provider: `http://localhost:8080/login/oauth2/code/oauth2-server`

## Security

- All routes except `/login`, `/`, and `/error` require authentication
- OAuth2 flow handles user authentication
- User information is synchronized with the database
- Role-based access control can be implemented

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

## Troubleshooting

### Database Connection Issues

- Verify MySQL is running
- Check database credentials in `application.yml`
- Ensure database `crm_db` exists

### OAuth2 Login Issues

- Verify OAuth2 authorization server is accessible
- Check client ID and secret in configuration
- Verify redirect URI matches OAuth2 provider settings

### Frontend Build Issues

- Clear node_modules: `rm -rf frontend/node_modules`
- Reinstall dependencies: `cd frontend && npm install`
- Check Node.js version compatibility

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please contact the development team.

---

**CRM System** - Customer Relationship Management Solution
