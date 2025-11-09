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
(Opcional) Inicializar usuarios por defecto desde la consola
  ```
  .\mvnw.cmd --% exec:java@dataloader -DskipTests
  ```

#### 5 - Ejecutar la aplicación (arranca Jetty en el puerto 8080 por defecto):
```
.\mvnw.cmd spring-boot:run
```

#### 6 - Abrir en el navegador:
http://localhost:8080

Notas importantes:
- El script SQL crea la base de datos `inspt_programacion2_kfc`. Hibernate está configurado con `spring.jpa.hibernate.ddl-auto=update` (en `application.properties`), por lo que las tablas necesarias se crearán/actualizarán al arrancar la app.
- Spring Security está activo por defecto. Spring genera un usuario en memoria `user` con una contraseña aleatoria mostrada en los logs al iniciar la aplicación.

## Ubicaciones relevantes
- Script de inicialización SQL: `db/init_mysql_inspt_programacion2_kfc.sql`
- Configuración de la aplicación: `src/main/resources/application.properties`
- Clase principal: `src/main/java/inspt_programacion2_kfc/InsptProgramacion2KfcApplication.java`
