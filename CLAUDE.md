# CLAUDE.md — NuvixTech Courses API

Guía para Claude Code al trabajar en este proyecto Spring Boot.

## Contexto del proyecto

- **Nombre:** NuvixTech Courses API (Academia ZR)
- **Tipo:** REST API backend para gestión de cursos educativos
- **Paquete base:** `com.nuvixtech.courses`
- **Puerto:** `8080`
- **Base de datos:** PostgreSQL 16 (Neon en producción, Docker local en dev)

## Context7 — Documentación actualizada

Context7 es un MCP que inyecta documentación oficial y actualizada directamente en el contexto. Está instalado en este entorno y **debe usarse** en los casos descritos a continuación.

### Cuándo usar Context7

| Situación | Usar Context7 |
|-----------|---------------|
| Nueva funcionalidad con una librería del stack (Spring Boot, JPA, Hibernate, Lombok) | Siempre |
| Actualización de dependencia o cambio de versión | Siempre |
| Duda sobre una API, anotación o configuración específica | Siempre |
| Corrección de bug relacionado con el comportamiento de un framework | Sí |
| Refactor que cambia el uso de una librería | Sí |
| Cambios simples de lógica interna sin uso de APIs externas | No necesario |

### Cómo invocarlo

```
# Consulta general por librería
use context7 for Spring Boot 4 @Transactional configuration

# Con ID directo (más rápido cuando se conoce la librería)
use context7 with /spring-projects/spring-framework for JPA repository query methods
use context7 with /projectlombok/lombok for @Builder with inheritance
```

### Librerías relevantes de este proyecto

- `spring-projects/spring-boot` — configuración, auto-configuration, starters
- `spring-projects/spring-framework` — Spring MVC, Data JPA, anotaciones core
- `projectlombok/lombok` — anotaciones Lombok
- `hibernate/hibernate-orm` — comportamiento Hibernate, dialectos, relaciones JPA

---

## Stack tecnológico

- Java 21
- Spring Boot 4.0.2
- Spring Data JPA + Hibernate
- PostgreSQL con driver JDBC
- Lombok
- Maven (usar siempre `./mvnw`, nunca `mvn` directamente)

## Comandos esenciales

```bash
# Compilar
./mvnw compile

# Ejecutar tests
./mvnw test

# Ejecutar la aplicación
./mvnw spring-boot:run

# Empaquetar
./mvnw package -DskipTests
```

## Arquitectura en capas

Seguir estrictamente la separación por capas dentro de `com.nuvixtech.courses`:

```
controller/   → @RestController — endpoints REST, sin lógica de negocio
service/      → @Service — lógica de negocio, transacciones
repository/   → @Repository / JpaRepository — acceso a datos únicamente
model/        → @Entity — entidades JPA que mapean tablas de BD
dto/          → POJOs — objetos de transferencia de datos (request/response)
```

No mezclar responsabilidades entre capas. Los controllers no acceden a repositorios directamente.

## Convenciones de código

### Entidades JPA

```java
@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    // ...
}
```

- Usar `@GeneratedValue(strategy = GenerationType.IDENTITY)` para IDs.
- Anotar siempre con `@Column` para especificar restricciones.
- Usar Lombok: preferir `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`.
- Evitar `@Data` en entidades JPA (problemas con `equals`/`hashCode` y relaciones lazy).

### Repositorios

```java
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTitleContainingIgnoreCase(String title);
}
```

- Extender `JpaRepository<Entity, ID>`.
- Usar query methods derivados del nombre cuando sea posible.
- Usar `@Query` solo cuando los métodos derivados no sean suficientes.

### DTOs

- Crear DTOs separados para request y response.
- No exponer entidades JPA directamente como respuesta de la API.
- Usar Lombok `@Getter @Builder` para response DTOs.
- Usar Lombok `@Getter @Setter` para request DTOs.

### Controllers

```java
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> findAll() {
        return ResponseEntity.ok(courseService.findAll());
    }
}
```

- Mapear bajo `/api/{recurso}` (plural).
- Devolver siempre `ResponseEntity<T>`.
- Inyectar dependencias por constructor (Lombok `@RequiredArgsConstructor`).

### Services

```java
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() { ... }
}
```

- Anotar la clase con `@Transactional`.
- Usar `@Transactional(readOnly = true)` en métodos de solo lectura.

## Configuración

El archivo principal es `src/main/resources/application.yml`.

**Advertencia de seguridad:** Las credenciales de base de datos están actualmente hardcodeadas en `application.yml`. Esto es un riesgo de seguridad. Al implementar nuevas funcionalidades, no agregar credenciales adicionales al archivo. El objetivo es migrar a variables de entorno:

```yaml
datasource:
  url: ${SPRING_DATASOURCE_URL}
  username: ${SPRING_DATASOURCE_USERNAME}
  password: ${SPRING_DATASOURCE_PASSWORD}
```

**CORS:** Actualmente configurado con `allowed-origins: "*"`. Restringir en producción.

**DDL Auto:** Está en `update`. Para producción, migrar a `validate` y usar Flyway o Liquibase.

## Convenciones de respuesta HTTP

| Operación | Método HTTP | Código de éxito |
|-----------|-------------|-----------------|
| Listar todos | GET `/api/recurso` | 200 OK |
| Obtener por ID | GET `/api/recurso/{id}` | 200 OK |
| Crear | POST `/api/recurso` | 201 Created |
| Actualizar | PUT `/api/recurso/{id}` | 200 OK |
| Eliminar | DELETE `/api/recurso/{id}` | 204 No Content |
| No encontrado | — | 404 Not Found |
| Error de validación | — | 400 Bad Request |

## Tests

- Los tests de integración usan `@SpringBootTest`.
- Los tests de controllers usan `@WebMvcTest` con `MockMvc`.
- Los tests de servicios son tests unitarios con Mockito.
- Ejecutar siempre los tests antes de considerar una tarea completa: `./mvnw test`.

## Lo que NO hacer

- No usar `mvn` directamente; siempre usar `./mvnw`.
- No inyectar repositorios en controllers.
- No exponer entidades JPA como body de respuesta.
- No usar `@Data` de Lombok en entidades JPA.
- No hardcodear nuevas credenciales o URLs en archivos de configuración.
- No modificar `application.yml` con credenciales reales; sugerir variables de entorno.
- No usar `ddl-auto: create` o `create-drop` sin consultar; puede destruir datos.

## Archivos clave

| Archivo | Propósito |
|---------|-----------|
| `src/main/java/com/nuvixtech/courses/CoursesApiApplication.java` | Punto de entrada |
| `src/main/resources/application.yml` | Configuración de la aplicación |
| `.devcontainer/devcontainer.json` | Entorno de desarrollo en contenedor |
| `.devcontainer/docker-compose.yml` | PostgreSQL local para desarrollo |
| `pom.xml` | Dependencias y build Maven |
