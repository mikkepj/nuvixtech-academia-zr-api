# NuvixTech Courses API — Plan de Seguimiento

**Proyecto:** Academia ZR | **Stack:** Spring Boot 4.0.2 · PostgreSQL · JPA · Lombok · Java 21

---

## Sprint 1 — CRUD Base de Cursos ✅

| # | Tarea | Estado |
|---|-------|--------|
| 1.1 | Modelo `Course` + enum `CourseType` (PRESENCIAL/ONLINE) | ✅ |
| 1.2 | `CourseRepository` (JpaRepository + query methods) | ✅ |
| 1.3 | DTOs: `CourseRequest` / `CourseResponse` | ✅ |
| 1.4 | `CourseService` (findAll, findById, create, update, delete) | ✅ |
| 1.5 | `CourseController` — endpoints REST `/api/courses` | ✅ |
| 1.6 | `CourseNotFoundException` + `GlobalExceptionHandler` | ✅ |
| 1.7 | `CourseRepositoryTest` — @SpringBootTest + SQL 10 cursos contra Neon | ✅ |
| 1.8 | `CourseServiceTest` — Mockito unitario | ✅ |
| 1.9 | `CourseControllerTest` — @WebMvcTest + MockMvc | ✅ |
| 1.10 | `DataSourceConfig` — normaliza URL libpq → JDBC | ✅ |
| 1.11 | Eliminar dialecto Hibernate explícito (auto-detectado en v7) | ✅ |

### Endpoints disponibles

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/api/courses` | 200 — lista completa |
| GET | `/api/courses/{id}` | 200 OK · 404 Not Found |
| POST | `/api/courses` | 201 Created |
| PUT | `/api/courses/{id}` | 200 OK · 404 Not Found |
| DELETE | `/api/courses/{id}` | 204 No Content · 404 Not Found |

### Configuración BD

| Parámetro | Valor | Nota |
|-----------|-------|------|
| `ddl-auto` | `update` | agrega tablas/columnas, nunca borra datos ✅ |
| Credenciales | Variables de entorno | `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASS` ✅ |
| URL format | Normalizado en `DataSourceConfig` | Soporta formato libpq y JDBC ✅ |
| Tests | `@SpringBootTest` contra Neon real | `@Commit` = datos visibles en Neon ✅ |

### Notas técnicas Sprint 1

- Spring Boot 4 renombró paquetes test: `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`, `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- Jackson 3.x cambió namespace: `tools.jackson.databind.ObjectMapper` (antes `com.fasterxml`)
- `@MockBean` reemplazado por `@MockitoBean` (`org.springframework.test.context.bean.override.mockito`)
- `DATABASE_URL` del Codespace viene en formato libpq — `DataSourceConfig` lo normaliza automáticamente

---

## Sprint 2 — Validaciones ⬜

| # | Tarea | Estado |
|---|-------|--------|
| 2.1 | Agregar `spring-boot-starter-validation` al `pom.xml` | ⬜ |
| 2.2 | Anotar `CourseRequest` con `@NotBlank`, `@NotNull`, `@Positive` | ⬜ |
| 2.3 | Agregar `@Valid` en `create` y `update` del controller | ⬜ |
| 2.4 | Manejar `MethodArgumentNotValidException` en `GlobalExceptionHandler` (400) | ⬜ |
| 2.5 | Tests de validaciones en `CourseControllerTest` | ⬜ |

---

## Sprint 3 — Filtros y Paginación ⬜

| # | Tarea | Estado |
|---|-------|--------|
| 3.1 | Paginación con `Pageable` en `GET /api/courses` | ⬜ |
| 3.2 | Filtro por tipo — `GET /api/courses?type=ONLINE` | ⬜ |
| 3.3 | Búsqueda por nombre — `GET /api/courses?name=java` | ⬜ |
| 3.4 | Respuesta paginada con `Page<CourseResponse>` | ⬜ |
| 3.5 | Tests de filtros y paginación | ⬜ |

---

## Sprint 4 — Producción ⬜

| # | Tarea | Estado |
|---|-------|--------|
| 4.1 | Restringir `CORS allowed-origins` (reemplazar `*`) | ⬜ |
| 4.2 | Migrar `ddl-auto` → `validate` + Flyway/Liquibase | ⬜ |
| 4.3 | Documentación OpenAPI/Swagger (`springdoc-openapi`) | ⬜ |
| 4.4 | Perfiles Spring: `dev` · `prod` | ⬜ |
| 4.5 | Corregir `DATABASE_URL` en Codespaces secret — formato: `jdbc:postgresql://ep-spring-credit-acoasqru-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require` | ⬜ |

---

*Leyenda: ✅ Completado · ⬜ Pendiente · 🔄 En progreso*
