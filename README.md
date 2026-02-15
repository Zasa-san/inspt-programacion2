# INSPT - Programación 2
## Trabajo Final
### Espacio de entrega para el grupo 12, tema 13.

- #### Informe del programa: <a href="INFORME.md">INFORME.md</a>

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

#### 1 - Clonar el repositorio

#### 2 - Iniciar MySQL

#### 3 - Crear la base de datos corriendo el script de inicialización en 
`db/init_mysql_inspt_programacion2_kfc.sql`.

#### 4 - Compilar el proyecto:

**Windows:**
```cmd
.\mvnw.cmd -DskipTests package
```

**Linux/Mac:**
```bash
./mvnw -DskipTests package
```

Inicializar base de datos por defecto:

**Windows:**
```cmd
.\mvnw.cmd -P dataloader exec:java@dataloader
```

**Linux/Mac:**
```bash
./mvnw -P dataloader exec:java@dataloader
```

#### 5 - Ejecutar la aplicación (arranca Jetty en el puerto 8080 por defecto):

**Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

#### 6 - Abrir en el navegador:
http://localhost:8080

## Ejecutar .jar compilado

> **Nota importante:** Es necesario copiar la carpeta `uploads/` del repositorio en el mismo directorio donde se encuentra el archivo `.jar` para que la aplicación funcione correctamente.

- Usando propiedades del sistema Java:

```
java -DMYSQL_HOST=db.example.com -DMYSQL_PORT=3307 -DMYSQL_DB=mi_db -DMYSQL_USER=mi_usuario -DMYSQL_PASSWORD=secreto -jar inspt-programacion2-kfc.jar
```

- Usando argumentos de Spring Boot:

```powershell
java -jar inspt-programacion2-kfc.jar --MYSQL_HOST=db.example.com --MYSQL_PORT=3307 --MYSQL_DB=mi_db --MYSQL_USER=mi_usuario --MYSQL_PASSWORD=secreto
```

- Usando variables de entorno:

**Windows (CMD):**
```cmd
set "MYSQL_HOST=db.example.com" && set "MYSQL_PORT=3307" && java -jar inspt-programacion2-kfc.jar
```

**Windows (PowerShell):**
```powershell
$env:MYSQL_HOST="db.example.com"; $env:MYSQL_PORT="3307"; java -jar inspt-programacion2-kfc.jar
```

**Linux/Mac:**
```bash
MYSQL_HOST=db.example.com MYSQL_PORT=3307 java -jar inspt-programacion2-kfc.jar
```