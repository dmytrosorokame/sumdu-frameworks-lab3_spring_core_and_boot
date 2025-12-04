# Spring Boot Books Application

## Lab 3 - Spring Core & Boot

This project demonstrates the migration of a 3-layer architecture application to Spring Boot, showcasing Inversion of Control (IoC), Dependency Injection (DI), and Spring Boot auto-configuration.

## Project Overview

A web application for managing a catalog of books and comments, implemented using Spring Boot with:

- **Domain Layer**: Book and Comment domain models
- **Service Layer**: Business logic (CommentService with 24-hour deletion rule)
- **Repository Layer**: JDBC implementations for data access
- **Web Layer**: Spring MVC controllers and JSP views

## Technologies

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring MVC**: Web layer with JSP views
- **H2 Database**: File-based database
- **Maven**: Build tool
- **Jackson**: JSON serialization

## Project Structure

```
spring-boot-books-app/
├── pom.xml
├── src/main/java/sumdu/edu/ua/
│   ├── AppInit.java                    # Main Spring Boot application
│   ├── config/
│   │   ├── AppConfig.java            # Custom bean configuration
│   │   └── WebConfig.java             # JSP view resolver configuration
│   ├── core/
│   │   ├── domain/                    # Domain models (Book, Comment, Page, PageRequest)
│   │   ├── port/                      # Repository interfaces
│   │   └── service/
│   │       └── CommentService.java    # @Service with constructor DI
│   ├── persistence/jdbc/
│   │   ├── JdbcBookRepository.java   # @Repository with field DI
│   │   ├── JdbcCommentRepository.java # @Repository with field DI
│   │   └── DbInit.java               # @Component for DB initialization
│   └── web/
│       ├── controller/               # Spring MVC controllers
│       └── http/                     # DTOs
├── src/main/resources/
│   ├── application.properties        # Spring Boot configuration
│   ├── schema.sql                    # Database schema
│   └── initial-data.sql              # Initial test data
└── src/main/webapp/WEB-INF/views/   # JSP views
```

## Key Concepts Demonstrated

### 1. Inversion of Control (IoC)

**IoC** is a design principle where the control of object creation and lifecycle is inverted from the application code to the Spring framework. Instead of manually creating objects with `new`, Spring's IoC container manages all beans.

**Example**: In the old project, objects were created manually in the `Beans` class:

```java
// Old approach - manual creation
private static final CatalogRepositoryPort bookRepo = new JdbcBookRepository();
```

In Spring Boot, we use annotations and let Spring manage the lifecycle:

```java
// New approach - Spring manages the bean
@Repository
public class JdbcBookRepository implements CatalogRepositoryPort {
    // Spring creates and manages this instance
}
```

### 2. Dependency Injection (DI)

**DI** is a technique where dependencies are injected into objects rather than created by the objects themselves. Spring supports three types of DI:

#### Constructor-Based Injection (Demonstrated in CommentService)

```java
@Service
public class CommentService {
    private final CommentRepositoryPort repo;

    @Autowired
    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;  // Dependency injected via constructor
    }
}
```

**Benefits**:

- Ensures all required dependencies are provided
- Makes the class immutable (final fields)
- Easier to test (can pass mock dependencies)

#### Field-Based Injection (Demonstrated in Repositories)

```java
@Repository
public class JdbcBookRepository implements CatalogRepositoryPort {

    @Autowired
    private DataSource dataSource;  // Dependency injected via field
}
```

**Benefits**:

- Less boilerplate code
- Convenient for optional dependencies

#### Custom Bean Injection (Demonstrated in BooksApiController)

```java
@RestController
public class BooksApiController {
    private final ObjectMapper objectMapper;

    @Autowired
    public BooksApiController(CatalogRepositoryPort bookRepo,
                              ObjectMapper objectMapper) {
        // Custom bean (defined in AppConfig) injected via constructor
        this.objectMapper = objectMapper;
    }
}
```

### 3. Spring Annotations

#### @Component

Marks a class as a Spring-managed component. Spring will automatically detect and register it.

**Example**: `DbInit` class

```java
@Component
public class DbInit implements CommandLineRunner {
    // Spring will create an instance and call run() after context loads
}
```

#### @Service

Specialized `@Component` for service layer classes.

**Example**: `CommentService`

```java
@Service
public class CommentService {
    // Business logic service
}
```

#### @Repository

Specialized `@Component` for data access layer classes. Provides exception translation.

**Example**: `JdbcBookRepository`

```java
@Repository
public class JdbcBookRepository implements CatalogRepositoryPort {
    // Data access implementation
}
```

#### @Configuration and @Bean

Used to define custom beans programmatically.

**Example**: `AppConfig`

```java
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();  // Custom bean definition
    }
}
```

### 4. Spring Boot Auto-Configuration

Spring Boot automatically configures the application based on:

- Dependencies in `pom.xml`
- Properties in `application.properties`
- Classpath scanning

**Examples of auto-configuration in this project**:

1. **DataSource Auto-Configuration**: Spring Boot automatically creates a `DataSource` bean based on `application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./data/guest;AUTO_SERVER=TRUE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

2. **Web MVC Auto-Configuration**: Spring Boot automatically configures:

   - DispatcherServlet
   - View resolvers
   - JSON message converters
   - Static resource handling

3. **Embedded Server**: Spring Boot includes an embedded Tomcat server, so no need for external Jetty configuration.

## Configuration

### application.properties

```properties
# Spring Boot Application Configuration
spring.application.name=Books Catalog Application

# Server configuration
server.port=8080
server.servlet.context-path=/

# Database configuration
spring.datasource.url=jdbc:h2:file:./data/guest;AUTO_SERVER=TRUE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JSP configuration
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
spring.mvc.static-path-pattern=/**

# Custom application properties
app.name=Books Catalog with Comments
app.description=Spring Boot application for managing books and comments
app.default-page-size=20
app.max-page-size=100
```

## Running the Application

### Prerequisites

- Java 17 or higher
- Maven 3.x

### Build and Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

### Access the Application

- **Books Catalog**: http://localhost:8080/books
- **Book Details with Comments**: http://localhost:8080/books/{id}
- **REST API**: http://localhost:8080/api/books

## API Endpoints

### GET /api/books

Get a page of books in JSON format.

**Query parameters**:

- `page` - page number (starts from 0, default: 0)
- `size` - page size (1..100, default: 10)
- `q` - optional search by title/author
- `sort` - sort field (title, author, pub_year)

**Example**:

```
GET /api/books?page=0&size=10&q=java&sort=title
```

### POST /api/books

Add a new book.

**Request body** (application/json):

```json
{
  "title": "New Book",
  "author": "Some Author",
  "pubYear": 2024
}
```

## Business Rules

- A comment can be deleted **only within 24 hours** from its creation
- This rule is enforced in `CommentService.delete()` method
- Violation throws `IllegalStateException`

## Bean Lifecycle

1. **Bean Discovery**: Spring scans for classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`
2. **Bean Creation**: Spring creates instances of discovered beans
3. **Dependency Injection**: Spring injects dependencies (via constructor or field)
4. **Initialization**: If bean implements `InitializingBean` or has `@PostConstruct`, methods are called
5. **Ready**: Bean is ready for use
6. **Destruction**: On application shutdown, `@PreDestroy` methods are called

## Comparison with Old Project

| Aspect                | Old Project             | Spring Boot Project                          |
| --------------------- | ----------------------- | -------------------------------------------- |
| Object Creation       | Manual in `Beans` class | Spring IoC container                         |
| Dependency Management | Manual wiring           | Automatic DI                                 |
| Configuration         | `web.xml`, manual setup | `application.properties`, auto-configuration |
| Server                | External Jetty          | Embedded Tomcat                              |
| Lifecycle             | ServletContextListener  | CommandLineRunner, @PostConstruct            |
| Testing               | Manual setup            | Spring Test support                          |

## Summary

This project demonstrates:

1. ✅ **IoC**: Spring manages all bean creation and lifecycle
2. ✅ **DI**: Dependencies injected via constructor and field injection
3. ✅ **Auto-configuration**: Spring Boot automatically configures DataSource, Web MVC, etc.
4. ✅ **Custom Beans**: `@Bean` annotation for custom ObjectMapper
5. ✅ **Component Scanning**: `@Component`, `@Service`, `@Repository` annotations
6. ✅ **Configuration**: `application.properties` for application properties

## Screenshots

1. Books catalog page (`/books`) with search, sorting and pagination.
   ![Books catalog page](/screenshots/books.png)
2. Book page with comments (`/books/{id}`) and the form for adding a new comment.
   ![Book page with comments form](/screenshots/comments.png)
3. Result of `GET /api/books` request in browser.
   ![GET /api/books request result](/screenshots/api.png)
4. Console with Spring Boot application logs during work.
   ![Console with Spring Boot application logs](/screenshots/logs.png)
