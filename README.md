# Department Management System

A Spring Boot application for managing departments and employees in an organization.

## Prerequisites

- Java 8 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## Database Setup

1. Create a MySQL database named `department_db`
2. Update the database configuration in `src/main/resources/application.properties` if needed:
   - spring.datasource.username=root
   - spring.datasource.password=root

## Building and Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on port 8080.

## Project Structure

- `entity/` - Contains JPA entity classes
- `repository/` - Contains Spring Data JPA repositories
- `service/` - Contains business logic (to be implemented)
- `controller/` - Contains REST controllers (to be implemented)

## Features

- Department management (create, read, update, delete)
- Employee management (create, read, update, delete)
- Department hierarchy management
- Employee-Department assignment
- Department manager assignment # myproject
