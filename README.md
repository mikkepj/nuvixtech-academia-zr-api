# NuvixTech Academia ZR — Courses API

Backend REST API para la plataforma Academia ZR de NuvixTech. Gestiona cursos, contenido educativo y datos relacionados.

## Tech Stack

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.2 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos (producción) | PostgreSQL 16 en Neon (cloud, sa-east-1) |
| Base de datos (desarrollo) | PostgreSQL 16 Alpine (Docker) |
| Build | Maven 3.9.12 (wrapper incluido) |
| Utilidades | Lombok |
| Entorno dev | VS Code Dev Containers |

## Prerequisitos

- Docker y Docker Compose (para el devcontainer)
- VS Code con la extensión **Dev Containers**
- O bien: Java 21 + Maven 3.9+ instalados localmente

## Inicio rápido

### Con Dev Container (recomendado)

1. Abre el proyecto en VS Code.
2. Cuando aparezca el prompt, selecciona **Reopen in Container** (o usa `F1` → _Dev Containers: Reopen in Container_).
3. El contenedor levantará automáticamente una instancia de PostgreSQL local.
4. Ejecuta la aplicación:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

### Sin Dev Container

1. Asegúrate de tener una instancia de PostgreSQL accesible.
2. Configura las variables de entorno de conexión (ver sección de configuración).
3. Ejecuta:

```bash
./mvnw spring-boot:run
```

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/nuvixtech/courses/
│   │   └── CoursesApiApplication.java   # Punto de entrada
│   └── resources/
│       └── application.yml              # Configuración principal
└── test/
    └── java/com/nuvixtech/courses/
        └── CoursesApiApplicationTests.java
.devcontainer/
├── devcontainer.json                    # Configuración del entorno dev
└── docker-compose.yml                   # Servicios locales (app + DB)
```

Arquitectura en capas esperada (en desarrollo):

```
com.nuvixtech.courses/
├── controller/     # REST controllers (@RestController)
├── service/        # Lógica de negocio (@Service)
├── repository/     # Acceso a datos (@Repository, JPA)
├── model/          # Entidades JPA (@Entity)
└── dto/            # Objetos de transferencia de datos
```

## Configuración

La configuración principal está en `src/main/resources/application.yml`.

> **Importante:** Las credenciales de base de datos no deben hardcodearse en el archivo de configuración. Usa variables de entorno en producción:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=secret
```

| Propiedad | Valor por defecto |
|-----------|-------------------|
| Puerto del servidor | `8080` |
| DDL auto | `update` |
| SQL logging | habilitado |
| CORS origins | `*` (restringir en producción) |

## Comandos útiles

```bash
# Compilar el proyecto
./mvnw compile

# Ejecutar tests
./mvnw test

# Empaquetar JAR
./mvnw package -DskipTests

# Ejecutar la aplicación
./mvnw spring-boot:run
```

## Base de datos de desarrollo (devcontainer)

| Parámetro | Valor |
|-----------|-------|
| Host | `localhost` |
| Puerto | `5432` |
| Base de datos | `courses_db` |
| Usuario | `courses_user` |
| Contraseña | `courses_pass` |

## Estado del proyecto

El proyecto se encuentra en fase de scaffolding. La infraestructura base (Spring Boot, JPA, conexión a BD, devcontainer) está configurada. Los endpoints y la lógica de negocio están pendientes de implementación.
