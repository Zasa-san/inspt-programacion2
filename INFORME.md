# Sistema de Gestión para Cadena de Comida Rápida (KFC)

**Nombre de la materia:** Programación II

**Docente:** Miguel Silva

**Integrantes del grupo:** 
- Castellano Mauricio
- Schneider Bruno
- Borrajo Santiago

**Curso y año:** [2.603] - 2025

**Título del proyecto:** Sistema de Gestión para Cadena de Comida Rápida - KFC

---

## 2. Link al Repositorio de GitHub

**Repositorio:** [https://github.com/Zasa-san/inspt-programacion2](https://github.com/Zasa-san/inspt-programacion2)

El proyecto está completo y es ejecutable siguiendo las instrucciones del README.md

---

## 3. Descripción General del Proyecto

### 3.1 Tema Elegido

Sistema de gestión integral para una cadena de comida rápida (KFC), que permite administrar productos, gestionar pedidos, controlar stock y administrar usuarios con diferentes niveles de acceso.

### 3.2 Objetivos del Sistema

- **Gestión de Productos:** Permitir la administración completa del catálogo de productos (CRUD), incluyendo nombre, descripción, precio y disponibilidad.
- **Control de Pedidos:** Facilitar la creación, seguimiento y gestión de pedidos de clientes con diferentes estados.
- **Administración de Stock:** Registrar movimientos de entrada y salida de productos, vinculados a pedidos y con motivos específicos.
- **Seguridad y Usuarios:** Implementar un sistema de autenticación y autorización basado en roles para proteger funcionalidades sensibles.
- **Interfaz de Usuario:** Proporcionar una interfaz web intuitiva y responsive para diferentes tipos de usuarios.

### 3.3 Descripción de los Actores y sus Roles

El sistema cuenta con tres roles principales de usuarios:

#### **ROLE_ADMIN (Administrador)**
- Acceso total al sistema
- Gestión completa de usuarios (crear, modificar, eliminar)
- Administración de productos (CRUD completo)
- Gestión de pedidos
- Control y visualización de stock
- Configuración del sistema

#### **ROLE_VENDEDOR (Vendedor)**
- Gestión de pedidos (crear, modificar estado)
- Visualización de productos disponibles
- Acceso al carrito de compras y checkout
- Consulta de historial de pedidos
- Sin acceso a gestión de productos ni usuarios

#### **ROLE_SOPORTE (Soporte)**
- Visualización y gestión de stock
- Registro de movimientos de inventario
- Gestión de pedidos
- Sin acceso a administración de productos ni usuarios

#### **Cliente (sin autenticación)**
- Visualización del catálogo de productos en la página principal
- Agregado de productos al carrito de compras
- Realización de pedidos (checkout)
- Sin acceso a funcionalidades administrativas

---

## 4. Arquitectura y Desarrollo

### 4.1 Descripción de las Capas del Sistema

El proyecto implementa una arquitectura en capas basada en Spring Boot, siguiendo el patrón MVC (Model-View-Controller) con una clara separación entre backend y frontend:

#### **Capa de Backend**
Ubicada en `src/main/java/inspt_programacion2_kfc/backend/`

- **Models:** Entidades JPA que representan el modelo de datos
- **Repositories:** Interfaces para acceso a datos (Spring Data JPA)
- **Services:** Lógica de negocio y operaciones del sistema
- **Exceptions:** Manejo de excepciones personalizadas

#### **Capa de Frontend**
Ubicada en `src/main/java/inspt_programacion2_kfc/frontend/`

- **Controllers:** Controladores que manejan las peticiones HTTP y renderizan vistas
- **Models:** DTOs específicos para la capa de presentación
- **Services:** Servicios auxiliares para la interfaz de usuario
- **Utils:** Utilidades como generación de metadatos de página

#### **Capa de Seguridad**
Ubicada en `src/main/java/inspt_programacion2_kfc/security/`

- **SecurityConfig:** Configuración de Spring Security
- **AppUserDetailsService:** Servicio de autenticación de usuarios

#### **Capa de Configuración**
Ubicada en `src/main/java/inspt_programacion2_kfc/config/`

- **WebConfig:** Configuración de recursos estáticos y handlers
- **DataLoaderCli:** Cargador de datos iniciales

#### **Capa de Presentación**
Ubicada en `src/main/resources/`

- **Templates Thymeleaf:** Vistas HTML con templates
- **Static Resources:** CSS (Bulma Framework), JavaScript, imágenes
- **Application Properties:** Configuración de la aplicación

### 4.2 Explicación de la Estructura del Proyecto

```
inspt-programacion2-kfc/
│
├── src/main/java/inspt_programacion2_kfc/
│   ├── backend/
│   │   ├── exceptions/          # Excepciones personalizadas
│   │   ├── models/              # Modelos de dominio
│   │   │   ├── auth/            # Modelos de autenticación
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── orders/          # Entidades de pedidos
│   │   │   ├── products/        # Entidades de productos
│   │   │   ├── stock/           # Entidades de stock
│   │   │   └── users/           # Entidades de usuarios
│   │   ├── repositories/        # Repositorios JPA
│   │   └── services/            # Servicios de negocio
│   │
│   ├── config/                  # Configuraciones generales
│   ├── frontend/                # Capa de presentación
│   │   ├── controllers/         # Controladores MVC
│   │   ├── models/              # DTOs de vista
│   │   ├── services/            # Servicios de frontend
│   │   └── utils/               # Utilidades de frontend
│   │
│   └── security/                # Configuración de seguridad
│
├── src/main/resources/
│   ├── application.properties   # Configuración de Spring Boot
│   ├── static/                  # Recursos estáticos
│   │   ├── css/                 # Estilos CSS
│   │   ├── img/                 # Imágenes
│   │   └── js/                  # JavaScript
│   └── templates/               # Plantillas Thymeleaf
│       ├── fragments/           # Fragmentos reutilizables
│       ├── pedidos/             # Vistas de pedidos
│       ├── products/            # Vistas de productos
│       ├── stock/               # Vistas de stock
│       └── users/               # Vistas de usuarios
│
├── db/                          # Scripts de base de datos
├── uploads/                     # Archivos subidos (imágenes)
└── pom.xml                      # Configuración Maven
```

### 4.3 Descripción de las Tecnologías y Frameworks Utilizados

#### **Backend**

- **Spring Boot 3.5.7:** Framework principal para el desarrollo de la aplicación
- **Spring Data JPA:** Para el mapeo objeto-relacional y acceso a datos
- **Spring Security:** Gestión de autenticación y autorización
- **Hibernate:** Implementación de JPA para persistencia
- **MySQL Connector/J:** Driver para conexión con base de datos MySQL/MariaDB
- **Lombok:** Reducción de código boilerplate con anotaciones
- **Jakarta Validation:** Validación de datos
- **Java 21:** Lenguaje de programación

#### **Frontend**

- **Thymeleaf:** Motor de plantillas para renderizado del lado del servidor
- **Bulma CSS 0.9.4:** Framework CSS moderno y responsive
- **jQuery 3.7.1:** Librería JavaScript para manipulación del DOM
- **FontAwesome:** Iconos vectoriales

#### **Servidor de Aplicaciones**

- **Jetty:** Contenedor de servlets embebido (alternativa a Tomcat)

#### **Base de Datos**

- **MariaDB 10.4.32+:** Sistema de gestión de bases de datos relacional

#### **Herramientas de Construcción**

- **Maven:** Gestión de dependencias y construcción del proyecto
- **Maven Wrapper:** Ejecuta Maven sin instalación global

### 4.4 Explicación de las Clases Principales y sus Responsabilidades

#### **Backend - Modelos**

**User.java**
```java
@Entity
@Table(name = "users")
public class User implements UserDetails
```
- Representa un usuario del sistema
- Implementa `UserDetails` de Spring Security para integración con autenticación
- Campos principales: `id`, `username`, `password`, `enabled`, `role`
- Relación con `Role` enum que define los roles del sistema
- Responsabilidad: Almacenar credenciales y datos de usuario

**ProductoEntity.java**
```java
@Entity
@Table(name = "productos")
public class ProductoEntity
```
- Representa un producto del catálogo
- Campos: `id`, `name`, `description`, `price` (en centavos), `imgUrl`, `available`
- Responsabilidad: Almacenar información de productos disponibles para venta

**Pedido.java**
```java
@Entity
@Table(name = "pedidos")
public class Pedido
```
- Representa un pedido realizado en el sistema
- Campos: `id`, `createdAt`, `estado`, `total`, `customerName`, `creadoPor`
- Relación OneToMany con `ItemPedido`
- Relación ManyToOne con `User` (creador del pedido)
- Responsabilidad: Gestionar información de pedidos y su estado

**ItemPedido.java**
- Representa un item dentro de un pedido
- Relación ManyToOne con `Pedido` y `ProductoEntity`
- Campos: `id`, `cantidad`, `precioUnitario`, `subtotal`
- Responsabilidad: Almacenar detalles de cada producto en un pedido

**MovimientoStock.java**
```java
@Entity
@Table(name = "movimientos_stock")
public class MovimientoStock
```
- Representa un movimiento de inventario
- Campos: `id`, `producto`, `tipo`, `cantidad`, `fecha`, `motivo`, `pedidoId`
- Relación ManyToOne con `ProductoEntity`
- Responsabilidad: Registrar entradas y salidas de stock

**Role.java**
```java
public enum Role {
    ROLE_SOPORTE,
    ROLE_VENDEDOR,
    ROLE_ADMIN;
}
```
- Enum que define los roles disponibles en el sistema
- Métodos auxiliares para integración con Spring Security
- Responsabilidad: Definir niveles de acceso

#### **Backend - Servicios**

**ProductoService**
- CRUD completo de productos
- Validaciones de negocio
- Gestión de imágenes asociadas
- Responsabilidad: Lógica de negocio para productos

**PedidoService**
- Creación y gestión de pedidos
- Cambio de estados de pedidos
- Cálculo de totales
- Integración con gestión de stock
- Responsabilidad: Lógica de negocio para pedidos

**UserService**
- CRUD de usuarios
- Encriptación de contraseñas
- Validación de datos de usuario
- Responsabilidad: Gestión de usuarios del sistema

**StockService**
- Registro de movimientos de stock
- Consulta de inventario
- Responsabilidad: Control de inventario

**FileUploadService**
- Gestión de archivos subidos (imágenes de productos)
- Almacenamiento en sistema de archivos
- Eliminación de archivos
- Responsabilidad: Manejo de archivos multimedia

#### **Security**

**SecurityConfig.java**
```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig
```
- Configuración de Spring Security
- Define reglas de autorización por URL
- Configuración de login y logout
- Manejo de acceso denegado
- Responsabilidad: Seguridad de la aplicación

Reglas principales:
- `/`, `/index`, `/login`, `/cart/**`, `/checkout/**`: Acceso público
- `/users/**`: Usuarios autenticados
- `/products/**`: Solo ADMIN
- `/stock/**`: ADMIN y SOPORTE
- `/pedidos/**`: ADMIN, VENDEDOR y SOPORTE

**AppUserDetailsService.java**
- Implementa `UserDetailsService` de Spring Security
- Carga usuarios desde la base de datos para autenticación
- Responsabilidad: Integración de usuarios con Spring Security

#### **Frontend - Controllers**

**ProductsPageController.java**
- Maneja todas las operaciones de la interfaz de productos
- Endpoints: listar, crear, editar, eliminar productos
- Gestión de imágenes de productos
- Responsabilidad: Controlador de vistas de productos

**CheckoutController.java**
- Gestión del proceso de checkout
- Creación de pedidos desde el carrito
- Integración con sesión HTTP para carrito
- Responsabilidad: Proceso de compra

**PedidosPageController.java**
- Visualización de pedidos
- Cambio de estados de pedidos
- Filtrado y búsqueda
- Responsabilidad: Gestión de interfaz de pedidos

**CartController.java**
- Gestión del carrito de compras en sesión
- Añadir, actualizar, eliminar items
- Responsabilidad: Carrito de compras en sesión

**UsersPageController.java**
- CRUD de usuarios desde interfaz web
- Responsabilidad: Administración de usuarios

**StockPageController.java**
- Visualización de movimientos de stock
- Registro de nuevos movimientos
- Responsabilidad: Interfaz de gestión de inventario

### 4.5 Capturas y Ejemplos de Código Relevantes

#### Ejemplo 1: Configuración de Seguridad

```java
@Bean
public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index", "/login", "/access-denied", 
                "/css/**", "/js/**", "/img/**", "/uploads/**", 
                "/favicon.ico", "/cart/**", "/checkout/**").permitAll()
            .requestMatchers("/users/**").authenticated()
            .requestMatchers("/products/**").hasRole(Role.ROLE_ADMIN.getRoleName())
            .requestMatchers("/stock/**").hasAnyRole(
                Role.ROLE_ADMIN.getRoleName(), 
                Role.ROLE_SOPORTE.getRoleName())
            .requestMatchers("/pedidos/**").hasAnyRole(
                Role.ROLE_ADMIN.getRoleName(), 
                Role.ROLE_VENDEDOR.getRoleName(), 
                Role.ROLE_SOPORTE.getRoleName())
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            .accessDeniedHandler(new AccessDeniedHandlerImpl() {
                {
                    setErrorPage("/access-denied");
                }
            })
        )
        .logout(withDefaults());

    return http.build();
}
```

#### Ejemplo 2: Modelo de Entidad con Relaciones

```java
@Data
@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.CREADO;

    @Column(nullable = false)
    private int total;

    @Column(nullable = true)
    private String customerName;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creadoPor;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    public void addItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }
}
```

#### Ejemplo 3: Configuración de Base de Datos

```properties
# application.properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DB:inspt_programacion2_kfc}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.upload-dir=uploads
spring.servlet.multipart.max-file-size=15MB
spring.servlet.multipart.max-request-size=15MB
```

---

## 5. Casos de Uso y Diagramas

### 5.1 Diagrama de Casos de Uso

```
                          Sistema KFC
    ┌─────────────────────────────────────────────────────┐
    │                                                     │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Gestión de Productos (CRUD)         │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Gestión de Usuarios (CRUD)          │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │               [ADMIN]────────────────┐              │
    │                    │                 │              │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Gestión de Pedidos                  │◄──────┤
    │  └─────────────────────────────────────────┘       │
    │                    ▲                 ▲              │
    │                    │                 │              │
    │               [VENDEDOR]        [SOPORTE]──┐        │
    │                    │                 │     │        │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Gestión de Stock                    │◄──────┘
    │  └─────────────────────────────────────────┘       │
    │                                                     │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Ver Catálogo de Productos           │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Agregar Productos al Carrito        │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Realizar Pedido (Checkout)          │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │                [CLIENTE]                            │
    │                                                     │
    └─────────────────────────────────────────────────────┘
```

### 5.2 Descripción de Casos de Uso

#### **CU-01: Gestión de Productos (ADMIN)**
- **Actor:** Administrador
- **Descripción:** El administrador puede crear, leer, actualizar y eliminar productos del catálogo
- **Precondiciones:** Usuario autenticado con rol ADMIN
- **Flujo Principal:**
  1. El administrador accede a `/products`
  2. Visualiza el listado de productos existentes
  3. Puede crear nuevo producto con nombre, descripción, precio e imagen
  4. Puede editar productos existentes
  5. Puede eliminar productos
  6. Puede marcar productos como disponibles/no disponibles
- **Postcondiciones:** Los cambios se reflejan en la base de datos y en el catálogo público

#### **CU-02: Gestión de Usuarios (ADMIN)**
- **Actor:** Administrador
- **Descripción:** El administrador gestiona usuarios del sistema
- **Precondiciones:** Usuario autenticado con rol ADMIN
- **Flujo Principal:**
  1. Accede a `/users`
  2. Visualiza lista de usuarios
  3. Crea nuevos usuarios asignando username, password y rol
  4. Edita usuarios existentes
  5. Elimina usuarios
  6. Habilita/deshabilita usuarios
- **Postcondiciones:** Los usuarios creados/modificados pueden autenticarse con sus credenciales

#### **CU-03: Ver Catálogo (CLIENTE)**
- **Actor:** Cliente (no autenticado)
- **Descripción:** Visualización del catálogo de productos disponibles
- **Precondiciones:** Ninguna
- **Flujo Principal:**
  1. El cliente accede a la página principal `/`
  2. Visualiza productos disponibles con imagen, nombre, descripción y precio
  3. Puede ver detalles de cada producto
- **Postcondiciones:** Ninguna

#### **CU-04: Agregar al Carrito (CLIENTE)**
- **Actor:** Cliente (no autenticado)
- **Descripción:** El cliente agrega productos al carrito de compras
- **Precondiciones:** Ninguna
- **Flujo Principal:**
  1. El cliente visualiza el catálogo
  2. Selecciona cantidad deseada de un producto
  3. Hace clic en "Agregar al carrito"
  4. El producto se añade a la sesión del carrito
  5. Puede modificar cantidades o eliminar items del carrito
- **Postcondiciones:** Los items permanecen en la sesión hasta completar el pedido

#### **CU-05: Realizar Pedido - Checkout (CLIENTE)**
- **Actor:** Cliente (no autenticado) o Usuario autenticado
- **Descripción:** Completar la compra de los productos en el carrito
- **Precondiciones:** Tener al menos un item en el carrito
- **Flujo Principal:**
  1. El cliente accede a `/checkout`
  2. Revisa los productos y el total
  3. Ingresa nombre del cliente (si no está autenticado)
  4. Confirma el pedido
  5. El sistema crea el pedido en estado "CREADO"
  6. Se registran movimientos de stock (salida) por cada producto
  7. El carrito se vacía
- **Postcondiciones:** Pedido registrado en base de datos, stock actualizado

#### **CU-06: Gestión de Pedidos (VENDEDOR, SOPORTE, ADMIN)**
- **Actor:** Vendedor, Soporte, Administrador
- **Descripción:** Visualización y gestión de pedidos del sistema
- **Precondiciones:** Usuario autenticado con rol autorizado
- **Flujo Principal:**
  1. El usuario accede a `/pedidos`
  2. Visualiza lista de pedidos con filtros
  3. Puede ver detalles de cada pedido (items, total, cliente, fecha)
  4. Puede cambiar el estado del pedido (CREADO → EN_PROCESO → COMPLETADO → ENTREGADO o CANCELADO)
  5. Puede filtrar por estado o fecha
- **Postcondiciones:** Estados actualizados en base de datos

#### **CU-07: Gestión de Stock (SOPORTE, ADMIN)**
- **Actor:** Soporte, Administrador
- **Descripción:** Visualización y registro de movimientos de inventario
- **Precondiciones:** Usuario autenticado con rol SOPORTE o ADMIN
- **Flujo Principal:**
  1. El usuario accede a `/stock`
  2. Visualiza historial de movimientos de stock
  3. Puede registrar nuevos movimientos (ENTRADA o SALIDA)
  4. Especifica producto, tipo, cantidad y motivo
  5. El sistema registra el movimiento con fecha actual
- **Postcondiciones:** Movimiento registrado en base de datos

#### **CU-08: Autenticación (TODOS LOS USUARIOS)**
- **Actor:** Usuario registrado
- **Descripción:** Login en el sistema
- **Precondiciones:** Tener credenciales válidas
- **Flujo Principal:**
  1. El usuario accede a `/login`
  2. Ingresa username y password
  3. El sistema valida las credenciales
  4. Si son válidas, establece sesión autenticada
  5. Redirige a página principal
  6. El usuario tiene acceso a funcionalidades según su rol
- **Postcondiciones:** Sesión autenticada con permisos según rol

---

## 6. Base de Datos

### 6.1 Diagrama Entidad-Relación

```
┌─────────────────────┐
│       USERS         │
├─────────────────────┤
│ PK  id             │
│     username        │◄──────────┐
│     password        │           │
│     enabled         │           │
│     role            │           │
└─────────────────────┘           │
                                  │
                                  │ user_id (FK)
                                  │
┌─────────────────────┐    ┌─────────────────────┐
│     PRODUCTOS       │    │      PEDIDOS        │
├─────────────────────┤    ├─────────────────────┤
│ PK  id             │    │ PK  id             │
│     name            │    │     created_at      │
│     description     │    │     estado          │
│     price           │    │     total           │
│     img_url         │    │     customer_name   │
│     available       │    │ FK  user_id         │
└─────────────────────┘    └─────────────────────┘
         │                           │
         │                           │
         │ producto_id (FK)          │ pedido_id (FK)
         │                           │
         │         ┌─────────────────────────┐
         └────────►│    ITEM_PEDIDO          │◄────┘
                   ├─────────────────────────┤
                   │ PK  id                 │
                   │ FK  pedido_id          │
                   │ FK  producto_id        │
                   │     cantidad            │
                   │     precio_unitario     │
                   │     subtotal            │
                   └─────────────────────────┘
                   
         ┌─────────────────────────┐
         │  MOVIMIENTOS_STOCK      │
         ├─────────────────────────┤
         │ PK  id                 │
         │ FK  producto_id        │
         │     tipo                │
         │     cantidad            │
         │     fecha               │
         │     motivo              │
         │     pedido_id           │
         └─────────────────────────┘
                   ▲
                   │
                   │ producto_id (FK)
                   │
         ┌─────────┘
         │
┌─────────────────────┐
│     PRODUCTOS       │
└─────────────────────┘

┌─────────────────────┐
│    API_TOKEN        │
├─────────────────────┤
│ PK  id             │
│     token           │
│     description     │
│     enabled         │
│     created_at      │
└─────────────────────┘
```

### 6.2 Descripción de las Tablas y Relaciones

#### **Tabla: USERS**
- **Descripción:** Almacena información de usuarios del sistema
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `username` (VARCHAR, UNIQUE): Nombre de usuario para login
  - `password` (VARCHAR): Contraseña encriptada con BCrypt
  - `enabled` (BOOLEAN): Si el usuario está activo
  - `role` (VARCHAR): Rol del usuario (ROLE_ADMIN, ROLE_VENDEDOR, ROLE_SOPORTE)
- **Relaciones:**
  - Uno a muchos con PEDIDOS (un usuario puede crear múltiples pedidos)

#### **Tabla: PRODUCTOS**
- **Descripción:** Catálogo de productos disponibles para venta
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `name` (VARCHAR): Nombre del producto
  - `description` (TEXT): Descripción detallada
  - `price` (INT): Precio en centavos (ej: 5500 = $55.00)
  - `img_url` (VARCHAR): Ruta de la imagen del producto
  - `available` (BOOLEAN): Si el producto está disponible para venta
- **Relaciones:**
  - Uno a muchos con ITEM_PEDIDO
  - Uno a muchos con MOVIMIENTOS_STOCK

#### **Tabla: PEDIDOS**
- **Descripción:** Registro de pedidos realizados en el sistema
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `created_at` (TIMESTAMP): Fecha y hora de creación
  - `estado` (VARCHAR): Estado actual (CREADO, EN_PROCESO, COMPLETADO, ENTREGADO, CANCELADO)
  - `total` (INT): Total del pedido en centavos
  - `customer_name` (VARCHAR): Nombre del cliente
  - `user_id` (BIGINT, FK): Usuario que creó el pedido (puede ser NULL si es cliente no autenticado)
- **Relaciones:**
  - Muchos a uno con USERS (creador del pedido)
  - Uno a muchos con ITEM_PEDIDO (items del pedido)

#### **Tabla: ITEM_PEDIDO**
- **Descripción:** Detalle de productos incluidos en cada pedido
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `pedido_id` (BIGINT, FK): Referencia al pedido
  - `producto_id` (BIGINT, FK): Referencia al producto
  - `cantidad` (INT): Cantidad del producto
  - `precio_unitario` (INT): Precio unitario al momento de la compra (en centavos)
  - `subtotal` (INT): Subtotal (cantidad × precio_unitario)
- **Relaciones:**
  - Muchos a uno con PEDIDOS
  - Muchos a uno con PRODUCTOS

#### **Tabla: MOVIMIENTOS_STOCK**
- **Descripción:** Registro de entradas y salidas de inventario
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `producto_id` (BIGINT, FK): Producto afectado
  - `tipo` (VARCHAR): Tipo de movimiento (ENTRADA, SALIDA)
  - `cantidad` (INT): Cantidad del movimiento
  - `fecha` (TIMESTAMP): Fecha y hora del movimiento
  - `motivo` (VARCHAR): Descripción del motivo
  - `pedido_id` (BIGINT, nullable): Referencia al pedido si el movimiento es por venta
- **Relaciones:**
  - Muchos a uno con PRODUCTOS

#### **Tabla: API_TOKEN**
- **Descripción:** Tokens de API para autenticación programática (funcionalidad futura)
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `token` (VARCHAR, UNIQUE): Token de acceso
  - `description` (VARCHAR): Descripción del token
  - `enabled` (BOOLEAN): Si el token está activo
  - `created_at` (TIMESTAMP): Fecha de creación

### 6.3 Ejemplo de Datos Cargados

#### Usuarios por defecto:
```sql
INSERT INTO users (username, password, enabled, role) VALUES
('admin', '$2a$10$...', 1, 'ROLE_ADMIN'),
('vendedor1', '$2a$10$...', 1, 'ROLE_VENDEDOR'),
('soporte1', '$2a$10$...', 1, 'ROLE_SOPORTE');
```

#### Productos de ejemplo:
```sql
INSERT INTO productos (name, description, price, img_url, available) VALUES
('Combo Clásico', 'Pollo frito con papas y bebida', 1200, '/uploads/products/combo-clasico.jpg', 1),
('Alitas Picantes', '8 alitas con salsa BBQ', 850, '/uploads/products/alitas.jpg', 1),
('Hamburguesa XXL', 'Hamburguesa doble con queso', 950, '/uploads/products/hamburguesa.jpg', 1);
```

#### Pedido de ejemplo:
```sql
-- Pedido
INSERT INTO pedidos (created_at, estado, total, customer_name, user_id) VALUES
('2025-11-17 14:30:00', 'COMPLETADO', 2050, 'Juan Pérez', 2);

-- Items del pedido
INSERT INTO item_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 1200, 1200),
(1, 2, 1, 850, 850);
```

#### Movimientos de stock:
```sql
-- Entrada de stock
INSERT INTO movimientos_stock (producto_id, tipo, cantidad, fecha, motivo) VALUES
(1, 'ENTRADA', 100, '2025-11-15 10:00:00', 'Reposición semanal');

-- Salida por venta
INSERT INTO movimientos_stock (producto_id, tipo, cantidad, fecha, motivo, pedido_id) VALUES
(1, 'SALIDA', 1, '2025-11-17 14:30:00', 'Venta - Pedido #1', 1);
```

---

## 7. Manual de Usuario

### 7.1 Acceso al Sistema

#### URL de acceso:
```
http://localhost:8080
```

#### Credenciales por defecto:

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin`

**Vendedor:**
- Usuario: `vendedor1`
- Contraseña: `vendedor123`

**Soporte:**
- Usuario: `soporte1`
- Contraseña: `soporte123`

### 7.2 Manual para CLIENTE (Sin Autenticación)

#### 7.2.1 Visualizar Catálogo
1. Acceder a `http://localhost:8080`
2. La página principal muestra todos los productos disponibles
3. Cada producto muestra:
   - Imagen
   - Nombre
   - Descripción
   - Precio

#### 7.2.2 Agregar Productos al Carrito
1. En la página principal, seleccionar la cantidad deseada del producto
2. Hacer clic en el botón "Agregar al Carrito"
3. El contador del carrito (en la barra de navegación) se actualizará
4. Para ver el contenido del carrito, hacer clic en el ícono del carrito

#### 7.2.3 Gestionar el Carrito
1. En la vista del carrito (`/cart`):
   - Ver todos los productos agregados
   - Modificar cantidades
   - Eliminar productos
   - Ver el total actualizado
2. Hacer clic en "Proceder al Checkout" para realizar el pedido

#### 7.2.4 Realizar un Pedido (Checkout)
1. En la página de checkout (`/checkout`):
   - Revisar el resumen del pedido
   - Ingresar el nombre del cliente
   - Verificar el total
2. Hacer clic en "Confirmar Pedido"
3. El sistema confirmará el pedido y vaciará el carrito
4. Se generará un número de pedido

**Capturas conceptuales:**
- Página principal con grid de productos
- Vista del carrito con lista de items
- Formulario de checkout con resumen

### 7.3 Manual para VENDEDOR (ROLE_VENDEDOR)

#### 7.3.1 Iniciar Sesión
1. Acceder a `http://localhost:8080/login`
2. Ingresar usuario: `vendedor1`
3. Ingresar contraseña
4. Hacer clic en "Iniciar Sesión"

#### 7.3.2 Funcionalidades Disponibles
- **Gestión de Pedidos:** Acceso completo
- **Carrito y Checkout:** Puede realizar pedidos como cliente
- **Visualización de Productos:** Solo lectura

#### 7.3.3 Gestión de Pedidos
1. Acceder a "Pedidos" desde el menú de navegación (`/pedidos`)
2. Visualizar lista de todos los pedidos con:
   - Número de pedido
   - Fecha y hora
   - Cliente
   - Total
   - Estado actual
3. **Ver detalles de un pedido:**
   - Hacer clic en el pedido
   - Ver items incluidos con cantidades y precios
4. **Cambiar estado de pedido:**
   - Seleccionar nuevo estado del dropdown
   - Estados disponibles: CREADO → EN_PROCESO → COMPLETADO → ENTREGADO
   - También puede marcar como CANCELADO
   - Hacer clic en "Actualizar Estado"
5. **Filtrar pedidos:**
   - Por estado (Todos, Creado, En Proceso, etc.)
   - Por rango de fechas

#### 7.3.4 Crear Pedido Manualmente
1. Desde la página principal, agregar productos al carrito
2. Ir a Checkout
3. Ingresar nombre del cliente
4. Confirmar pedido
5. El pedido quedará registrado con el vendedor como creador

**Capturas conceptuales:**
- Dashboard de pedidos con tabla
- Vista de detalle de pedido con items
- Selector de estado de pedido

### 7.4 Manual para SOPORTE (ROLE_SOPORTE)

#### 7.4.1 Iniciar Sesión
1. Acceder a `http://localhost:8080/login`
2. Ingresar usuario: `soporte1`
3. Ingresar contraseña
4. Hacer clic en "Iniciar Sesión"

#### 7.4.2 Funcionalidades Disponibles
- **Gestión de Pedidos:** Acceso completo (igual que VENDEDOR)
- **Gestión de Stock:** Acceso completo
- **Productos:** Solo lectura

#### 7.4.3 Gestión de Stock
1. Acceder a "Stock" desde el menú de navegación (`/stock`)
2. **Visualizar movimientos de stock:**
   - Lista de todos los movimientos históricos
   - Información mostrada:
     - Producto
     - Tipo (ENTRADA / SALIDA)
     - Cantidad
     - Fecha y hora
     - Motivo
     - Pedido relacionado (si aplica)
3. **Registrar nuevo movimiento:**
   - Hacer clic en "Nuevo Movimiento"
   - Seleccionar producto del dropdown
   - Seleccionar tipo de movimiento (ENTRADA o SALIDA)
   - Ingresar cantidad
   - Ingresar motivo (ej: "Reposición mensual", "Producto dañado")
   - Hacer clic en "Registrar"
4. **Filtrar movimientos:**
   - Por producto
   - Por tipo de movimiento
   - Por rango de fechas

#### 7.4.4 Consultar Inventario Actual
1. En la página de stock, ver el balance actual por producto
2. Suma de entradas menos suma de salidas
3. Identificar productos con bajo stock

**Capturas conceptuales:**
- Tabla de movimientos de stock con filtros
- Formulario de registro de nuevo movimiento
- Vista de inventario actual por producto

### 7.5 Manual para ADMINISTRADOR (ROLE_ADMIN)

#### 7.5.1 Iniciar Sesión
1. Acceder a `http://localhost:8080/login`
2. Ingresar usuario: `admin`
3. Ingresar contraseña: `admin`
4. Hacer clic en "Iniciar Sesión"

#### 7.5.2 Funcionalidades Disponibles
- **Gestión de Productos:** CRUD completo
- **Gestión de Usuarios:** CRUD completo
- **Gestión de Pedidos:** Acceso completo
- **Gestión de Stock:** Acceso completo

#### 7.5.3 Gestión de Productos

**Listar Productos:**
1. Acceder a "Productos" desde el menú (`/products`)
2. Visualizar tabla con todos los productos:
   - ID
   - Imagen miniatura
   - Nombre
   - Descripción
   - Precio
   - Disponibilidad
   - Acciones (Editar, Eliminar)

**Crear Nuevo Producto:**
1. Hacer clic en "Nuevo Producto"
2. Completar el formulario:
   - Nombre del producto (requerido)
   - Descripción (requerido)
   - Precio en pesos (se convierte automáticamente a centavos)
   - Imagen (archivo JPG, PNG, máximo 15MB)
   - Disponible (checkbox)
3. Hacer clic en "Guardar"
4. El producto aparecerá en el catálogo

**Editar Producto:**
1. Hacer clic en el botón "Editar" del producto deseado
2. Modificar los campos necesarios
3. Opcionalmente cambiar la imagen (la anterior se elimina)
4. Hacer clic en "Actualizar"

**Eliminar Producto:**
1. Hacer clic en el botón "Eliminar"
2. Confirmar la eliminación
3. El producto y su imagen se eliminarán permanentemente
4. NOTA: Si el producto está en pedidos, puede generar errores de integridad

**Marcar Producto como No Disponible:**
1. Editar el producto
2. Desmarcar checkbox "Disponible"
3. El producto no aparecerá en el catálogo público

**Capturas conceptuales:**
- Tabla de productos con acciones
- Formulario de creación/edición de producto
- Vista previa de imagen cargada

#### 7.5.4 Gestión de Usuarios

**Listar Usuarios:**
1. Acceder a "Usuarios" desde el menú (`/users`)
2. Visualizar tabla con usuarios:
   - ID
   - Username
   - Rol
   - Estado (Habilitado/Deshabilitado)
   - Acciones (Editar, Eliminar)

**Crear Nuevo Usuario:**
1. Hacer clic en "Nuevo Usuario"
2. Completar el formulario:
   - Username (único, requerido)
   - Password (requerido, mínimo 4 caracteres)
   - Seleccionar Rol:
     - ADMIN
     - VENDEDOR
     - SOPORTE
   - Habilitado (checkbox)
3. Hacer clic en "Crear Usuario"
4. La contraseña se encriptará automáticamente

**Editar Usuario:**
1. Hacer clic en "Editar" del usuario deseado
2. Modificar campos:
   - Username (debe seguir siendo único)
   - Password (dejar vacío para no cambiar)
   - Rol
   - Estado habilitado
3. Hacer clic en "Actualizar"

**Eliminar Usuario:**
1. Hacer clic en "Eliminar"
2. Confirmar eliminación
3. ADVERTENCIA: Los pedidos creados por este usuario quedarán huérfanos

**Deshabilitar Usuario:**
1. Editar usuario
2. Desmarcar "Habilitado"
3. El usuario no podrá iniciar sesión

**Capturas conceptuales:**
- Tabla de usuarios con filtros
- Formulario de creación de usuario
- Formulario de edición con validaciones

#### 7.5.5 Acceso a Todas las Funcionalidades

El administrador tiene acceso completo a:
- **Pedidos:** Gestión completa (ver sección 7.3.3)
- **Stock:** Gestión completa (ver sección 7.4.3)
- **Productos:** CRUD completo (sección 7.5.3)
- **Usuarios:** CRUD completo (sección 7.5.4)

#### 7.5.6 Panel de Administración

El menú de navegación del administrador muestra:
- Inicio (catálogo público)
- Productos
- Stock
- Pedidos
- Usuarios
- Perfil (ver/editar propio perfil)
- Cerrar Sesión

### 7.6 Funcionalidades Comunes

#### Modo Oscuro (Tema Dark Mode)
1. En la barra de navegación, hacer clic en el icono de luna/sol
2. El tema cambiará entre claro y oscuro
3. La preferencia se guarda en el navegador

#### Cerrar Sesión
1. Hacer clic en "Cerrar Sesión" en el menú
2. La sesión se cerrará y se redirigirá al login

#### Cambiar Contraseña Propia
1. Acceder a la sección de perfil/usuarios
2. Editar el propio usuario
3. Ingresar nueva contraseña
4. Guardar cambios

#### Manejo de Errores
- **Error 403 (Acceso Denegado):** Se muestra una página informando que no tiene permisos
- **Error 404:** Página no encontrada
- **Errores de validación:** Se muestran en el formulario con mensajes específicos
- **Errores del servidor:** Se muestra mensaje genérico

### 7.7 Pantallas Principales

#### Página Principal (/)
- Header con logo y menú de navegación
- Grid responsive de productos
- Botones de "Agregar al Carrito"
- Footer con información

#### Login (/login)
- Formulario centrado con campos de usuario y contraseña
- Enlace de "¿Olvidaste tu contraseña?" (si está implementado)
- Botón de "Iniciar Sesión"

#### Dashboard de Productos (/products)
- Tabla con listado de productos
- Botones de acción (Nuevo, Editar, Eliminar)
- Filtros y búsqueda
- Paginación (si hay muchos productos)

#### Dashboard de Pedidos (/pedidos)
- Tabla con pedidos ordenados por fecha
- Filtros por estado y fecha
- Acceso a detalle de cada pedido
- Selector de cambio de estado

#### Dashboard de Stock (/stock)
- Tabla de movimientos
- Formulario de registro de movimiento
- Filtros por producto, tipo y fecha
- Resumen de inventario actual

#### Dashboard de Usuarios (/users)
- Tabla de usuarios registrados
- Botones de acción (Nuevo, Editar, Eliminar)
- Indicadores de estado (habilitado/deshabilitado)
- Badges de roles con colores distintivos

---

## 8. Conclusiones

### 8.1 Objetivos Alcanzados

El sistema desarrollado cumple con los objetivos planteados:

✅ **Sistema completo de gestión** para una cadena de comida rápida
✅ **Arquitectura en capas** bien definida con separación de responsabilidades
✅ **Seguridad robusta** implementada con Spring Security
✅ **Gestión de roles** con permisos diferenciados
✅ **Interfaz responsive** y moderna utilizando Bulma CSS
✅ **Base de datos relacional** con integridad referencial
✅ **Funcionalidades CRUD completas** para todas las entidades principales
✅ **Sistema de carrito de compras** funcional en sesión HTTP
✅ **Control de inventario** con trazabilidad de movimientos

### 8.2 Tecnologías Implementadas

- ✅ Java 21 con Spring Boot 3.5.7
- ✅ Spring Data JPA con Hibernate
- ✅ Spring Security con autenticación basada en formularios
- ✅ Thymeleaf como motor de plantillas
- ✅ MySQL/MariaDB como base de datos
- ✅ Maven para gestión de dependencias
- ✅ Jetty como servidor de aplicaciones embebido
- ✅ Bulma CSS para diseño responsive
- ✅ Lombok para reducción de código boilerplate

## Anexos

### A. Instrucciones de Instalación y Ejecución

Ver archivo `README.md` en la raíz del proyecto para instrucciones detalladas de:
- Requisitos del sistema
- Configuración de base de datos
- Compilación con Maven
- Ejecución de la aplicación
- Variables de entorno disponibles

### B. Estructura de Directorios Completa

```
inspt-programacion2-kfc/
├── db/
│   └── init_mysql_inspt_programacion2_kfc.sql
├── src/
│   └── main/
│       ├── java/inspt_programacion2_kfc/
│       │   ├── backend/
│       │   │   ├── exceptions/
│       │   │   ├── models/
│       │   │   ├── repositories/
│       │   │   └── services/
│       │   ├── config/
│       │   ├── frontend/
│       │   │   ├── controllers/
│       │   │   ├── models/
│       │   │   ├── services/
│       │   │   └── utils/
│       │   └── security/
│       └── resources/
│           ├── application.properties
│           ├── static/
│           └── templates/
├── uploads/
│   └── products/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

### C. Dependencias del Proyecto (pom.xml)

- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-thymeleaf
- spring-boot-starter-validation
- spring-boot-starter-web
- spring-boot-starter-jetty
- thymeleaf-extras-springsecurity6
- mysql-connector-j
- lombok

### D. Puertos y URLs

- **Puerto por defecto:** 8080
- **URL base:** http://localhost:8080
- **Login:** http://localhost:8080/login
- **Recursos estáticos:** http://localhost:8080/css/*, /js/*, /img/*
- **Uploads:** http://localhost:8080/uploads/*

### E. Variables de Entorno Configurables

- `MYSQL_HOST`: Host del servidor MySQL (default: localhost)
- `MYSQL_PORT`: Puerto de MySQL (default: 3306)
- `MYSQL_DB`: Nombre de la base de datos (default: inspt_programacion2_kfc)
- `MYSQL_USER`: Usuario de MySQL (default: root)
- `MYSQL_PASSWORD`: Contraseña de MySQL (default: vacío)

**Fin del documento**
