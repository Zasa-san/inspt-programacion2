# INSPT - Programación 2
## Trabajo Final
### Espacio de entrega para el grupo 12, tema 13.

- #### Consigna del desarrollo: <a href="consigna-desarrollo.pdf" download>consigna-desarrollo.pdf</a>

- #### Visualización UML: <a href="uml-imagen.png" download>uml-imagen.png</a>
  ##### Archivo base UML:  <a href="uml.drawio" download>uml.drawio</a>

- #### Visualización de casos de uso: <a href="casos_de_uso_base-imagen.png" download>casos_de_uso_base-imagen.png</a>
  ##### Archivo base de casos de uso:  <a href="casos_de_uso_base.drawio" download>casos_de_uso_base.drawio</a>

## Requisitos para ejecutar el proyecto
- Java: JDK 21.
- Base de datos: MariaDB 10.4.32 o superior.
- Cliente `mysql` (opcional, para ejecutar scripts SQL de inicialización de la base).
- Maven Wrapper incluido (`mvnw.cmd` en Windows) — no es necesario tener Maven instalado globalmente.

## Dependencias del proyecto
- [spring-boot-starter-data-jpa](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)
- [spring-boot-starter-security](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security)
- [spring-boot-starter-thymeleaf](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf)
- [spring-boot-starter-validation](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation)
- [spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)
- [spring-boot-starter-jetty](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-jetty)
- [thymeleaf-extras-springsecurity6](https://mvnrepository.com/artifact/org.thymeleaf.extras/thymeleaf-extras-springsecurity6)
- [mysql-connector-j](https://mvnrepository.com/artifact/mysql/mysql-connector-java)
- [Bulma (bulma.io)](https://bulma.io/)
- [jQuery](https://jquery.com/)
- [Lombok](https://projectlombok.org/) (opcional)

## Pasos para ejecturar el proyecto
ACA TENEMOS QUE DEJAR LAS INSTRUCCIONES FINALES POR AHORA SON PROVISORIAS

#### 1 - Clonar el repositorio

#### 2 - Iniciar MySQL

#### 3 - Crear la base de datos corriendo el script de inicialización en 
`db/init_mysql_inspt_programacion2_kfc.sql`.

#### 4 - Compilar el proyecto (omitiendo tests que no se utilizan por ahora):
```
.\mvnw.cmd -DskipTests package
```
Inicializar base de datos por defecto
  ```
  .\mvnw.cmd exec:java@dataloader -DskipTests
  ```

#### 5 - Ejecutar la aplicación (arranca Jetty en el puerto 8080 por defecto):
```
.\mvnw.cmd spring-boot:run
```

#### 6 - Abrir en el navegador:
http://localhost:8080

## Ejecutar .jar compilado

- Usando propiedades del sistema Java:

```
java -DMYSQL_HOST=db.example.com -DMYSQL_PORT=3307 -DMYSQL_DB=mi_db -DMYSQL_USER=mi_usuario -DMYSQL_PASSWORD=secreto -jar inspt-programacion2-kfc.jar
```

- Usando argumentos de Spring Boot:

```powershell
java -jar inspt-programacion2-kfc.jar --MYSQL_HOST=db.example.com --MYSQL_PORT=3307 --MYSQL_DB=mi_db --MYSQL_USER=mi_usuario --MYSQL_PASSWORD=secreto
```

- Usando una variable de entorno:

```cmd
set "MYSQL_HOST=db.example.com" && set "MYSQL_PORT=3307" && java -jar inspt-programacion2-kfc.jar
```


## Ubicaciones relevantes
- Script de inicialización SQL: `db/init_mysql_inspt_programacion2_kfc.sql`
- Configuración de la aplicación: `src/main/resources/application.properties`
- Clase principal: `src/main/java/inspt_programacion2_kfc/InsptProgramacion2KfcApplication.java`

## Arquitectura

La aplicación sigue el patrón **MVC** organizado en tres capas:

- **Backend** (`backend/models`, `backend/repositories`, `backend/services`): Contiene la lógica de negocio, acceso a datos y modelos de la aplicación
- **API REST** (`api/`): Controllers REST que exponen los endpoints de la aplicación
- **Frontend** (`frontend/controllers` + `templates/`): Controladores y vistas Thymeleaf para la interfaz web

Los **DTOs** (`backend/dto/`) se utilizan para transferir datos entre capas sin exponer las entidades internas.

### Autenticación — sesiones (UI) y tokens (API)

La aplicación soporta dos métodos de autenticación que coexisten de forma segura:

- Sesiones (formLogin)
  - Uso: pensado para usuarios humanos que interactúan con la interfaz Thymeleaf.
  - Cómo funciona: el usuario hace login en la página `/login`. Spring Security crea una sesión en el servidor y devuelve una cookie de sesión al navegador. Las solicitudes posteriores usan esa cookie para autenticarse.
  - Implementación: configurado en `src/main/java/inspt_programacion2_kfc/security/web/WebSecurityConfig.java` (formLogin, redirección a `/login`, protección de rutas web, CSRF activo por defecto).

- Tokens API (Bearer tokens)
  - Uso: pensado para clientes programáticos (scripts, servicios) que no usan navegador.
  - Cómo funciona: el cliente obtiene un token (texto plano) mediante `/api/auth/login` (credenciales) o `/api/auth/token` (cuando ya tiene sesión). El servidor guarda sólo el hash SHA-256 del token en la base de datos (`ApiToken`) y devuelve el token en texto plano sólo una vez. En las llamadas posteriores el cliente incluye el header `Authorization: Bearer <token>`.
  - Implementación: filtro `ApiTokenAuthenticationFilter` valida el token y, si es válido, rellena el `SecurityContext`. La lógica de tokens está en `src/main/java/inspt_programacion2_kfc/backend/services/auth/ApiTokenService.java` y la persistencia en `ApiToken`/`ApiTokenRepository`.

Componentes clave (archivo / responsabilidad)
- `WebSecurityConfig` (security/web): configuración de la cadena de seguridad para la UI (formLogin, CSRF, acceso a `/users/**`).
- `ApiSecurityConfig` (security/api): configuración para `/api/**` — aquí se ha deshabilitado CSRF para facilitar clientes programáticos y se registró el filtro de tokens.
- `ApiTokenAuthenticationFilter`: lee `Authorization: Bearer ...`, valida el token con `ApiTokenService` y establece la autenticación para la request.
- `ApiTokenService`: genera tokens con entropía fuerte, almacena sólo el hash, y valida tokens.
- `ApiTokenController` (`/api/auth`): endpoints para generar token desde sesión (`POST /api/auth/token`) y para login por credenciales que emite token (`POST /api/auth/login`).
- `AppUserDetailsService`: carga la entidad `User` desde la base de datos; `User` implementa `UserDetails` para integración con Spring Security.

Flujos comunes
- Flujo UI (usuario humano): login en `/login` → sesión cookie → acceder a `/users` y vistas protegidas.
- Flujo API con token (script): `POST /api/auth/login` {username,password} → recibir `{token}` → usar `Authorization: Bearer <token>` en llamadas a `/api/**`.
- Flujo API desde sesión (script en navegador o cliente que comparte cookie): `POST /api/auth/token` (requiere sesión) → recibir `{token}` → usar header Bearer en futuras llamadas.
