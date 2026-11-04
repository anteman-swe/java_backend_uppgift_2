# Helpdesk System - Spring Boot Application

A comprehensive helpdesk/ticket management system built with Java Spring Boot, Spring Security, Spring JPA, and PostgreSQL.

## Features

### User Roles
- **USER**: Can create, view, and comment on their own tickets
- **AGENT**: Can view all tickets, assign tickets to themselves, change status, and add comments
- **ADMIN**: Full access including user management (create, edit, enable/disable users)

### Ticket Management
- Create, view, edit, and delete tickets
- Add comments to tickets
- Assign tickets to agents
- Change ticket status (NEW, IN_PROGRESS, WAITING_FOR_USER, RESOLVED, CLOSED)
- Set ticket priority (LOW, MEDIUM, HIGH, CRITICAL)
- Categorize tickets

### User Management (Admin only)
- Create new users
- Edit existing users
- Change user roles
- Enable/disable user accounts
- Delete users

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Database
- **Thymeleaf** - Server-side templating
- **Bootstrap 5** - Frontend styling
- **Maven** - Build tool
- **Docker** - Containerization

## Project Structure

```
helpdesk/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/helpdesk/
│   │   │       ├── config/           # Configuration classes
│   │   │       ├── controller/       # Web controllers
│   │   │       ├── entity/           # JPA entities
│   │   │       ├── repository/       # Spring Data repositories
│   │   │       ├── security/         # Security configuration
│   │   │       └── service/          # Business services
│   │   └── resources/
│   │       ├── static/              # Static files (CSS, JS)
│   │       └── templates/           # Thymeleaf templates
│   └── test/                        # Test classes
├── docker-compose.yml               # Docker Compose configuration
├── Dockerfile                       # Docker configuration
└── pom.xml                          # Maven configuration
```

## Quick Start

### Prerequisites
- Java 17 or later
- Maven 3.8.6 or later
- Docker and Docker Compose

### Option 1: Run with Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/anteman-swe/java_backend_uppgift_2.git
cd java_backend_uppgift_2

# Start the application with Docker Compose
docker-compose up -d

# The application will be available at http://localhost:8080
```

### Option 2: Run Locally with PostgreSQL

1. **Start PostgreSQL database:**
   ```bash
   docker-compose up -d postgres
   ```

2. **Build and run the application:**
   ```bash
   mvn clean package
   java -jar target/helpdesk-1.0.0.jar
   ```

3. **Access the application:**
   Open http://localhost:8080 in your browser

## Default Users

The application creates sample users on first startup:

| Username | Password | Role   | Full Name      | Email                |
|----------|----------|--------|----------------|----------------------|
| admin    | admin123 | ADMIN  | Admin User     | admin@helpdesk.com   |
| agent    | agent123 | AGENT  | Support Agent  | agent@helpdesk.com   |
| user     | user123  | USER   | Regular User   | user@helpdesk.com    |
| user2    | user123  | USER   | Test User      | user2@helpdesk.com   |

## Application URLs

| URL | Description | Access |
|-----|-------------|--------|
| `/` | Home page | Redirects to /tickets or /login |
| `/login` | Login page | Public |
| `/tickets` | My tickets | Authenticated users |
| `/tickets/new` | Create new ticket | Authenticated users |
| `/tickets/{id}` | View ticket details | Ticket owner or agent/admin |
| `/tickets/{id}/edit` | Edit ticket | Ticket owner or agent/admin |
| `/agent/tickets` | All tickets | AGENT, ADMIN |
| `/agent/tickets/new` | New tickets | AGENT, ADMIN |
| `/agent/tickets/in-progress` | In progress tickets | AGENT, ADMIN |
| `/agent/tickets/assigned` | My assigned tickets | AGENT, ADMIN |
| `/agent/tickets/unassigned` | Unassigned tickets | AGENT, ADMIN |
| `/admin/users` | User management | ADMIN |
| `/admin/users/new` | Create new user | ADMIN |
| `/admin/users/{id}/edit` | Edit user | ADMIN |

## Security Configuration

The application uses Spring Security with form-based authentication:

- **Authentication**: Username/password login
- **Authorization**: Role-based access control (RBAC)
- **Password Encoding**: BCrypt
- **Session Management**: 30-minute session timeout

### Security Rules

- Public pages: `/`, `/login`, `/error`, `/css/**`, `/js/**`
- USER pages: `/tickets`, `/tickets/new`, `/tickets/{id}`, `/tickets/{id}/edit`
- AGENT pages: `/agent/**` (requires AGENT or ADMIN role)
- ADMIN pages: `/admin/**` (requires ADMIN role)

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    last_modified TIMESTAMP NOT NULL
);
```

### Tickets Table
```sql
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    last_modified TIMESTAMP NOT NULL,
    created_by_id BIGINT NOT NULL REFERENCES users(id),
    assigned_to_id BIGINT REFERENCES users(id)
);
```

### Comments Table
```sql
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_modified TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    ticket_id BIGINT NOT NULL REFERENCES tickets(id)
);
```

## Entity Relationships

- **User -> Ticket**: One-to-Many (created_tickets, assigned_tickets)
- **User -> Comment**: One-to-Many (comments)
- **Ticket -> Comment**: One-to-Many (comments)
- **Ticket -> User**: Many-to-One (created_by, assigned_to)
- **Comment -> User**: Many-to-One (user)
- **Comment -> Ticket**: Many-to-One (ticket)

## API Endpoints

### Authentication
- `POST /login` - Login
- `POST /logout` - Logout

### Tickets
- `GET /tickets` - List tickets (USER: own, AGENT/ADMIN: all)
- `GET /tickets/new` - Create ticket form
- `POST /tickets/new` - Create ticket
- `GET /tickets/{id}` - View ticket
- `GET /tickets/{id}/edit` - Edit ticket form
- `POST /tickets/{id}/edit` - Update ticket
- `POST /tickets/{id}/add-comment` - Add comment to ticket
- `POST /tickets/{id}/change-status` - Change ticket status

### Agent
- `GET /agent/tickets` - List all tickets
- `GET /agent/tickets/new` - List new tickets
- `GET /agent/tickets/in-progress` - List in-progress tickets
- `GET /agent/tickets/assigned` - List assigned tickets
- `GET /agent/tickets/unassigned` - List unassigned tickets
- `POST /agent/tickets/{id}/assign` - Assign ticket to self
- `POST /agent/tickets/{id}/unassign` - Unassign ticket

### Admin
- `GET /admin/users` - List all users
- `GET /admin/users/new` - Create user form
- `POST /admin/users/new` - Create user
- `GET /admin/users/{id}/edit` - Edit user form
- `POST /admin/users/{id}/edit` - Update user
- `POST /admin/users/{id}/toggle-status` - Toggle user status
- `POST /admin/users/{id}/change-role` - Change user role
- `POST /admin/users/{id}/delete` - Delete user

## Frontend Features

- **Responsive Design**: Works on mobile, tablet, and desktop
- **Bootstrap 5**: Modern UI components
- **Bootstrap Icons**: Icon library
- **Thymeleaf**: Server-side rendering
- **Form Validation**: Client and server-side validation
- **Flash Messages**: Success/error notifications

## Customization

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Security
spring.security.user.name=admin
spring.security.user.password=admin
spring.security.user.roles=ADMIN
```

### Docker Configuration

Edit `docker-compose.yml` to change database settings:

```yaml
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: helpdesk
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
```

## Testing

The application includes sample data that is loaded on first startup. You can test the following:

1. **USER Role**: Login as `user/user123`
   - Create a ticket
   - View your tickets
   - Add comments to your tickets

2. **AGENT Role**: Login as `agent/agent123`
   - View all tickets
   - Assign tickets to yourself
   - Change ticket status
   - Add comments to any ticket

3. **ADMIN Role**: Login as `admin/admin123`
   - All AGENT functions
   - Create new users
   - Edit user information
   - Change user roles
   - Enable/disable users

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running: `docker ps`
- Check connection: `psql -h localhost -U postgres -d helpdesk`
- Verify credentials in `application.properties`

### Application Not Starting
- Check logs: `docker-compose logs app`
- Build manually: `mvn clean package`
- Check Java version: `java -version`

### Login Issues
- Verify user credentials in the database
- Check if user is active
- Ensure correct role is assigned

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -DskipTests
```

### Running with Docker
```bash
# Build the image
docker build -t helpdesk-app .

# Run the container
docker run -p 8080:8080 --network=host helpdesk-app
```

## License

This project is licensed under the MIT License.

## Acknowledgments

- Spring Boot Documentation
- Thymeleaf Documentation
- Bootstrap Documentation
- PostgreSQL Documentation
